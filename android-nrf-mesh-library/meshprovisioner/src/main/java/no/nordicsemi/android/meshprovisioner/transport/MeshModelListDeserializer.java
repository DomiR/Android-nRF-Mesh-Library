package no.nordicsemi.android.meshprovisioner.transport;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.meshprovisioner.models.SigModelParser;
import no.nordicsemi.android.meshprovisioner.models.VendorModel;
import no.nordicsemi.android.meshprovisioner.utils.MeshAddress;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.meshprovisioner.utils.PublicationSettings;

/**
 * Class for deserializing a list of elements stored in the Mesh Configuration Database
 */
public final class MeshModelListDeserializer implements JsonSerializer<List<MeshModel>>, JsonDeserializer<List<MeshModel>> {

    @Override
    public List<MeshModel> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final List<MeshModel> meshModels = new ArrayList<>();
        final JsonArray jsonArray = json.getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            final int modelId = MeshParserUtils.hexToInt(jsonObject.get("modelId").getAsString());

            final PublicationSettings publicationSettings = getPublicationSettings(jsonObject);
            final List<Integer> subscriptionAddresses = getSubscriptionAddresses(jsonObject);
            final List<Integer> boundKeyIndexes = getBoundAppKeyIndexes(jsonObject);
            final MeshModel meshModel = getMeshModel(modelId);
            if (meshModel != null) {
                meshModel.mPublicationSettings = publicationSettings;
                meshModel.subscriptionAddresses.addAll(subscriptionAddresses);
                meshModel.mBoundAppKeyIndexes.addAll(boundKeyIndexes);
                meshModels.add(meshModel);
            }
        }
        return meshModels;
    }

    @Override
    public JsonElement serialize(final List<MeshModel> models, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonArray jsonArray = new JsonArray();
        for (MeshModel model : models) {
            final JsonObject meshModelJson = new JsonObject();
            if (model instanceof VendorModel) {
                meshModelJson.addProperty("modelId", String.format(Locale.US, "%08X", model.getModelId()));
            } else {
                meshModelJson.addProperty("modelId", String.format(Locale.US, "%04X", model.getModelId()));
            }
            if (!model.getSubscribedAddresses().isEmpty()) {
                meshModelJson.add("subscribe", serializeSubscriptionAddresses(model.getSubscribedAddresses()));
            }
            if (model.getPublicationSettings() != null) {
                meshModelJson.add("publish", serializePublicationSettings(model.getPublicationSettings()));
            }

            if (!model.getBoundAppKeyIndexes().isEmpty()) {
                meshModelJson.add("bind", serializeBoundAppKeys(model.getBoundAppKeyIndexes()));
            }
            jsonArray.add(meshModelJson);
        }
        return jsonArray;
    }

    /**
     * Get publication settings from json
     *
     * @param jsonObject json object
     * @return {@link PublicationSettings}
     */
    private PublicationSettings getPublicationSettings(final JsonObject jsonObject) {
        if (!jsonObject.has("publish"))
            return null;

        final JsonObject publish = jsonObject.get("publish").getAsJsonObject();
        final int publishAddress = Integer.parseInt(publish.get("address").getAsString(), 16);

        final int index = publish.get("index").getAsInt();
        final int ttl = publish.get("ttl").getAsByte();

        //Unpack publish period
        final JsonObject period = publish.get("period").getAsJsonObject();
        final int publicationSteps = period.get("numberOfSteps").getAsInt();
        final int publicationResolution = period.get("resolution").getAsInt();

		final JsonObject retransmit = publish.get("retransmit").getAsJsonObject();
        final int publishRetransmitCount = retransmit.get("count").getAsInt();
        final int publishRetransmitIntervalSteps = retransmit.get("interval").getAsInt();

        final boolean credentials = publish.get("credentials").getAsInt() == 1;

        //Set the values
        final PublicationSettings publicationSettings = new PublicationSettings();
        publicationSettings.setPublishAddress(publishAddress);
		publicationSettings.setAppKeyIndex(index);
        publicationSettings.setPublishTtl(ttl);
        publicationSettings.setPublicationSteps(publicationSteps);
        publicationSettings.setPublicationResolution(publicationResolution);
        publicationSettings.setPublishRetransmitCount(publishRetransmitCount);
        publicationSettings.setPublishRetransmitIntervalSteps(publishRetransmitIntervalSteps);
        publicationSettings.setCredentialFlag(credentials);

        return publicationSettings;
    }

    /**
     * Returns subscription addresses from json
     *
     * @param jsonObject json
     * @return list of subscription addresses
     */
    private List<Integer> getSubscriptionAddresses(final JsonObject jsonObject) {
        final List<Integer> subscriptions = new ArrayList<>();
        if (!(jsonObject.has("subscribe")))
            return subscriptions;

        final JsonArray jsonArray = jsonObject.get("subscribe").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            final int address = Integer.parseInt(jsonArray.get(i).getAsString(), 16);
            subscriptions.add(address);
        }
        return subscriptions;
    }

    private List<Integer> getBoundAppKeyIndexes(final JsonObject jsonObject) {
        final List<Integer> boundKeyIndexes = new ArrayList<>();
        if (!(jsonObject.has("bind")))
            return boundKeyIndexes;

        final JsonArray jsonArray = jsonObject.get("bind").getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            final int index = jsonArray.get(i).getAsInt();
            boundKeyIndexes.add(index);
        }
        return boundKeyIndexes;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param subscriptions subscriptions list
     */
    private JsonArray serializeSubscriptionAddresses(final List<Integer> subscriptions) {
        final JsonArray subscriptionsJson = new JsonArray();
        for (Integer address : subscriptions) {
            subscriptionsJson.add(MeshAddress.formatAddress(address, false));
        }
        return subscriptionsJson;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param publicationSettings publication settings for this node
     */
    private JsonObject serializePublicationSettings(final PublicationSettings publicationSettings) {
        final JsonObject publicationJson = new JsonObject();
        publicationJson.addProperty("address", MeshAddress.formatAddress(publicationSettings.getPublishAddress(), false));
        publicationJson.addProperty("index", String.format(Locale.US, "%04X", publicationSettings.getAppKeyIndex()));
        publicationJson.addProperty("ttl", publicationSettings.getPublishTtl());
        publicationJson.addProperty("period", publicationSettings.calculatePublicationPeriod());

        final JsonObject retransmitJson = new JsonObject();
        retransmitJson.addProperty("count", publicationSettings.getPublishRetransmitCount());
        retransmitJson.addProperty("interval", publicationSettings.getPublishRetransmitIntervalSteps());
        publicationJson.add("retransmit", retransmitJson);
        publicationJson.addProperty("credentials", publicationSettings.getCredentialFlag() ? 1 : 0);
        return publicationJson;
    }

    /**
     * Returns JsonElement containing the subscription addresses addresses from json
     *
     * @param boundAppKeys List of bound app key indexes
     */
    private JsonArray serializeBoundAppKeys(final List<Integer> boundAppKeys) {
        final JsonArray boundAppKeyIndexes = new JsonArray();
        for (Integer index : boundAppKeys) {
            boundAppKeyIndexes.add(String.format(Locale.US, "%04X", index));
        }
        return boundAppKeyIndexes;
    }

    /**
     * Returns a {@link MeshModel}
     *
     * @param modelId model Id
     * @return {@link MeshModel}
     */
    private MeshModel getMeshModel(final int modelId) {
        if (modelId < Short.MIN_VALUE || modelId > Short.MAX_VALUE) {
            return new VendorModel(modelId);
        } else {
            return SigModelParser.getSigModel(modelId);
        }
    }
}
