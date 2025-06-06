/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.meshprovisioner.transport;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import no.nordicsemi.android.meshprovisioner.InternalTransportCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.utils.AddressUtils;

public abstract class BaseMeshMessageHandler implements MeshMessageHandlerApi, InternalMeshMsgHandlerCallbacks {

    private static final String TAG = BaseMeshMessageHandler.class.getSimpleName();

    protected final Context mContext;
    protected final MeshTransport mMeshTransport;
    protected final InternalTransportCallbacks mInternalTransportCallbacks;
    protected MeshStatusCallbacks mStatusCallbacks;
    private MeshMessageState mMeshMessageState;

    protected BaseMeshMessageHandler(final Context context, final InternalTransportCallbacks internalTransportCallbacks) {
        this.mContext = context;
        this.mMeshTransport = new MeshTransport(context);
        this.mInternalTransportCallbacks = internalTransportCallbacks;
    }

    protected abstract MeshTransport getMeshTransport();

    /**
     * Handle mesh message States on write callback complete
     * <p>
     * This method will jump to the current state and switch the current state according to the message that has been sent.
     * </p>
     *
     * @param pdu mesh pdu that was sent
     */
    public final void handleMeshMsgWriteCallbacks(final byte[] pdu) {
        if (mMeshMessageState instanceof ProxyConfigMessageState) {
            switch (mMeshMessageState.getState()) {
                case PROXY_CONFIG_SET_FILTER_TYPE_STATE:
                    final ProxyConfigSetFilterTypeState setFilterTypeState = (ProxyConfigSetFilterTypeState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, setFilterTypeState.getMeshMessage(), mMeshTransport, this));
                    break;
                case PROXY_CONFIG_ADD_ADDRESS_TO_FILTER_STATE:
                    final ProxyConfigAddAddressState addAddressState = (ProxyConfigAddAddressState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, addAddressState.getMeshMessage(), mMeshTransport, this));
                    break;
                case PROXY_CONFIG_REMOVE_ADDRESS_FROM_FILTER_STATE:
                    final ProxyConfigRemoveAddressState removeAddressState = (ProxyConfigRemoveAddressState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, removeAddressState.getMeshMessage(), mMeshTransport, this));
                    break;
            }
        } else if (mMeshMessageState instanceof ConfigMessageState) {
            if (mMeshMessageState.getState() == null) {
                Log.d(TAG, "state is null");
                return;
            }

            switch (mMeshMessageState.getState()) {
                case COMPOSITION_DATA_GET_STATE:
                    final ConfigCompositionDataGetState compositionDataGet = (ConfigCompositionDataGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, compositionDataGet.getMeshMessage(), mMeshTransport, this));
                    break;
                case APP_KEY_ADD_STATE:
                    final ConfigAppKeyAddState appKeyAddState = (ConfigAppKeyAddState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, appKeyAddState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_APP_BIND_STATE:
                    final ConfigModelAppBindState appBindState = (ConfigModelAppBindState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, appBindState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_APP_UNBIND_STATE:
                    final ConfigModelAppUnbindState appUnbindState = (ConfigModelAppUnbindState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, appUnbindState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_PUBLICATION_GET_STATE:
                    final ConfigModelPublicationGetState publicationGetState = (ConfigModelPublicationGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, publicationGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_PUBLICATION_SET_STATE:
                    final ConfigModelPublicationSetState publicationSetState = (ConfigModelPublicationSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, publicationSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_SUBSCRIPTION_ADD_STATE:
                    final ConfigModelSubscriptionAddState subscriptionAdd = (ConfigModelSubscriptionAddState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, subscriptionAdd.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_MODEL_SUBSCRIPTION_DELETE_STATE:
                    final ConfigModelSubscriptionDeleteState subscriptionDelete = (ConfigModelSubscriptionDeleteState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, subscriptionDelete.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_NODE_RESET_STATE:
                    final ConfigNodeResetState resetState = (ConfigNodeResetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, resetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_NETWORK_TRANSMIT_GET_STATE:
                    final ConfigNetworkTransmitGetState networkTransmitGet = (ConfigNetworkTransmitGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, networkTransmitGet.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_NETWORK_TRANSMIT_SET_STATE:
                    final ConfigNetworkTransmitSetState networkTransmitSet = (ConfigNetworkTransmitSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, networkTransmitSet.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_RELAY_GET_STATE:
                    final ConfigRelayGetState configRelayGetState = (ConfigRelayGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, configRelayGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_RELAY_SET_STATE:
                    final ConfigRelaySetState configRelaySetState = (ConfigRelaySetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, configRelaySetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_PROXY_GET_STATE:
                    final ConfigProxyGetState configProxyGetState = (ConfigProxyGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, configProxyGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case CONFIG_PROXY_SET_STATE:
                    final ConfigProxySetState configProxySetState = (ConfigProxySetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, configProxySetState.getMeshMessage(), mMeshTransport, this));
                    break;
            }
        } else if (mMeshMessageState instanceof GenericMessageState) {
            switch (mMeshMessageState.getState()) {
                case HEALTH_FAULT_GET_STATE:
                    final HealthFaultGetState healthFaultGetState = (HealthFaultGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, healthFaultGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case HEALTH_FAULT_TEST_STATE:
                    final HealthFaultTestState healthFaultTestState = (HealthFaultTestState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, healthFaultTestState.getMeshMessage(), mMeshTransport, this));
                    break;
                case HEALTH_ATTENTION_GET_STATE:
                    final HealthAttentionGetState healthAttentionGetState = (HealthAttentionGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, healthAttentionGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case HEALTH_ATTENTION_SET_STATE:
                    final HealthAttentionSetState healthAttentionSetState = (HealthAttentionSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, healthAttentionSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case HEALTH_ATTENTION_SET_UNACKNOWLEDGED_STATE:
                    final HealthAttentionSetUnacknowledgedState healthAttentionSetUnacknowledgedState = (HealthAttentionSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, healthAttentionSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_USER_PROPERTY_GET_STATE:
                    final GenericUserPropertyGetState onUserPropertyState = (GenericUserPropertyGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, onUserPropertyState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_ON_OFF_GET_STATE:
                    final GenericOnOffGetState onOffGetState = (GenericOnOffGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, onOffGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_ON_OFF_SET_STATE:
                    final GenericOnOffSetState onOffSetState = (GenericOnOffSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, onOffSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_ON_OFF_SET_UNACKNOWLEDGED_STATE:
                    final GenericOnOffSetUnacknowledgedState onOffSetUnacknowledgedState = (GenericOnOffSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, onOffSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_LEVEL_GET_STATE:
                    final GenericLevelGetState levelGetState = (GenericLevelGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, levelGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_LEVEL_SET_STATE:
                    final GenericLevelSetState levelSetState = (GenericLevelSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, levelSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_LEVEL_SET_UNACKNOWLEDGED_STATE:
                    final GenericLevelSetUnacknowledgedState levelSetUnacknowledgedState = (GenericLevelSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, levelSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_MOVE_SET_STATE:
                    final GenericMoveSetState moveSetState = (GenericMoveSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, moveSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_MOVE_SET_UNACKNOWLEDGED_STATE:
                    final GenericMoveSetUnacknowledgedState moveSetUnacknowledgedState = (GenericMoveSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, moveSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                 case GENERIC_ON_POWER_UP_GET_STATE: {
                     final GenericOnPowerUpGetState state = (GenericOnPowerUpGetState) mMeshMessageState;
                     switchToNoOperationState(new DefaultNoOperationMessageState(mContext, state.getMeshMessage(), mMeshTransport, this));
                     break;
                 }
                case GENERIC_ON_POWER_UP_SET_STATE: {
                    final GenericOnPowerUpSetState state = (GenericOnPowerUpSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, state.getMeshMessage(), mMeshTransport, this));
                    break;
                }
                case GENERIC_ON_POWER_UP_SET_UNACKNOWLEDGED_STATE: {
                    final GenericOnPowerUpSetUnacknowledgedState state = (GenericOnPowerUpSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, state.getMeshMessage(), mMeshTransport, this));
                    break;
                }
                case LIGHT_LIGHTNESS_GET_STATE:
                    final LightLightnessGetState lightnessGetState = (LightLightnessGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_LIGHTNESS_SET_STATE:
                    final LightLightnessSetState lightnessSetState = (LightLightnessSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED_STATE:
                    final LightLightnessSetUnacknowledgedState lightnessSetUnacknowledgedState = (LightLightnessSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_GET_STATE:
                    final LightCtlGetState ctlGetState = (LightCtlGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_SET_STATE:
                    final LightCtlSetState ctlSetState = (LightCtlSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_SET_UNACKNOWLEDGED_STATE:
                    final LightCtlSetUnacknowledgedState ctlSetUnacknowledgedState = (LightCtlSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_GET_STATE:
                    final LightHslGetState hslGetState = (LightHslGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_SET_STATE:
                    final LightHslSetState hslSetState = (LightHslSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_SET_UNACKNOWLEDGED_STATE:
                    final LightHslSetUnacknowledgedState hslSetUnacknowledgedState = (LightHslSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_LIGHTNESS_DEFAULT_GET_STATE:
                    final LightLightnessDefaultGetState lightnessDefaultGetState = (LightLightnessDefaultGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessDefaultGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_LIGHTNESS_DEFAULT_SET_STATE:
                    final LightLightnessDefaultSetState lightnessDefaultSetState = (LightLightnessDefaultSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessDefaultSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_LIGHTNESS_DEFAULT_SET_UNACKNOWLEDGED_STATE:
                    final LightLightnessDefaultSetUnacknowledgedState lightnessDefaultSetUnacknowledgedState = (LightLightnessDefaultSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, lightnessDefaultSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_DEFAULT_GET_STATE:
                    final LightCtlDefaultGetState ctlDefaultGetState = (LightCtlDefaultGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlDefaultGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_DEFAULT_SET_STATE:
                    final LightCtlDefaultSetState ctlDefaultSetState = (LightCtlDefaultSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlDefaultSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_CTL_DEFAULT_SET_UNACKNOWLEDGED_STATE:
                    final LightCtlDefaultSetUnacknowledgedState ctlDefaultSetUnacknowledgedState = (LightCtlDefaultSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, ctlDefaultSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_DEFAULT_GET_STATE:
                    final LightHslDefaultGetState hslDefaultGetState = (LightHslDefaultGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslDefaultGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_DEFAULT_SET_STATE:
                    final LightHslDefaultSetState hslDefaultSetState = (LightHslDefaultSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslDefaultSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case LIGHT_HSL_DEFAULT_SET_UNACKNOWLEDGED_STATE:
                    final LightHslDefaultSetUnacknowledgedState hslDefaultSetUnacknowledgedState = (LightHslDefaultSetUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, hslDefaultSetUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case VENDOR_MODEL_ACKNOWLEDGED_STATE:
                    final VendorModelMessageAckedState vendorModelMessageAckedState = (VendorModelMessageAckedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, vendorModelMessageAckedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case VENDOR_MODEL_UNACKNOWLEDGED_STATE:
                    final VendorModelMessageUnackedState vendorModelMessageUnackedState = (VendorModelMessageUnackedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, vendorModelMessageUnackedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_GET_STATE:
                    final SceneGetState sceneGetState = (SceneGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_REGISTER_GET_STATE:
                    final SceneRegisterGetState sceneRegisterGetState = (SceneRegisterGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneRegisterGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_STORE_STATE:
                    final SceneStoreState sceneStoreState = (SceneStoreState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneStoreState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_STORE_UNACKNOWLEDGED_STATE:
                    final SceneStoreUnacknowledgedState sceneStoreUnacknowledgedState = (SceneStoreUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneStoreUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_RECALL_STATE:
                    final SceneRecallState sceneRecallState = (SceneRecallState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneRecallState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_RECALL_UNACKNOWLEDGED_STATE:
                    final SceneRecallUnacknowledgedState sceneRecallUnacknowledgedState = (SceneRecallUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneRecallUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_DELETE_STATE:
                    final SceneDeleteState sceneDeleteState = (SceneDeleteState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneDeleteState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCENE_DELETE_UNACKNOWLEDGED_STATE:
                    final SceneDeleteUnacknowledgedState sceneDeleteUnacknowledgedState = (SceneDeleteUnacknowledgedState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, sceneDeleteUnacknowledgedState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_GET_STATE:
                    final TimeGetState timeGetState = (TimeGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timeGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_SET_STATE:
                    final TimeSetState timeSetState = (TimeSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timeSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_ZONE_GET_STATE:
                    final TimezoneGetState timezoneGetState = (TimezoneGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timezoneGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_ZONE_SET_STATE:
                    final TimezoneSetState timezoneSetState = (TimezoneSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timezoneSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_ROLE_GET_STATE:
                    final TimeRoleGetState timeRoleGetState = (TimeRoleGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timeRoleGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TIME_ROLE_SET_STATE:
                    final TimeRoleSetState timeRoleSetState = (TimeRoleSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, timeRoleSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TAI_UTC_GET_STATE:
                    final TaiUtcDeltaGetState taiUtcDeltaGetState = (TaiUtcDeltaGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, taiUtcDeltaGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case TAI_UTC_SET_STATE:
                    final TaiUtcDeltaSetState taiUtcDeltaSetState = (TaiUtcDeltaSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, taiUtcDeltaSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCHEDULER_GET_STATE:
                    final SchedulerGetState schedulerGetState = (SchedulerGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, schedulerGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCHEDULER_ACTION_GET_STATE:
                    final SchedulerActionGetState schedulerActionGetState = (SchedulerActionGetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, schedulerActionGetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case SCHEDULER_ACTION_SET_STATE:
                    final SchedulerActionSetState schedulerActionSetState = (SchedulerActionSetState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, schedulerActionSetState.getMeshMessage(), mMeshTransport, this));
                    break;
                case BLOB_TRANSFER_START_STATE:
                    final BLOBTransferStartState blobTransferStartState = (BLOBTransferStartState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, blobTransferStartState.getMeshMessage(), mMeshTransport, this));
                    break;
                case BLOB_BLOCK_START_STATE:
                    final BLOBBlockStartState blobBlockStartState = (BLOBBlockStartState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, blobBlockStartState.getMeshMessage(), mMeshTransport, this));
                    break;
				case BLOB_BLOCK_GET_STATE:
					final BLOBBlockGetState blobBlockGetState = (BLOBBlockGetState) mMeshMessageState;
					switchToNoOperationState(new DefaultNoOperationMessageState(mContext, blobBlockGetState.getMeshMessage(), mMeshTransport, this));
					break;
                case BLOB_CHUNK_TRANSFER_STATE:
                    final BLOBChunkTransferState blobChunkTransferState = (BLOBChunkTransferState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, blobChunkTransferState.getMeshMessage(), mMeshTransport, this));
                    break;
                case GENERIC_ACCESS_MESSAGE_STATE:
					// TODO what is this state variable for?
					final GenericAccessMessageState accessMessageState = (GenericAccessMessageState) mMeshMessageState;
                    switchToNoOperationState(new DefaultNoOperationMessageState(mContext, accessMessageState.getMeshMessage(), mMeshTransport, this));
                    break;
            }
        }
    }

    /**
     * Handle mesh States on receiving mesh message notifications
     * <p>
     * This method will jump to the current state and switch the state depending on the expected and the next message received.
     * </p>
     *
     * @param pdu mesh pdu that was sent
     */
    public final void parseMeshMsgNotifications(final byte[] pdu) {
        if (mMeshMessageState instanceof DefaultNoOperationMessageState) {
            ((DefaultNoOperationMessageState) mMeshMessageState).parseMeshPdu(pdu);
        } else {
            Log.v(TAG, "Dropping mesh message because of missing state.");
        }
    }

    @Override
    public final void onIncompleteTimerExpired(final boolean incompleteTimerExpired) {
        //We switch no operation state if the incomplete timer has expired so that we don't wait on the same state if a particular message fails.
        final MeshMessage meshMessage = mMeshMessageState.getMeshMessage();
        switchToNoOperationState(new DefaultNoOperationMessageState(mContext, meshMessage, mMeshTransport, this));
    }

    /**
     * Switch the current state of the mesh message handler
     * <p>
     * This method will switch the current state of the mesh message handler
     * </p>
     *
     * @param newState new state that is to be switched to
     */
    private void switchToNoOperationState(final MeshMessageState newState) {
        //Switching to unknown message state here for messages that are not
        if (mMeshMessageState.getState() != null && newState.getState() != null) {
            Log.v(TAG, "Switching current state " + mMeshMessageState.getState().name() + " to No operation state");
        } else {
            Log.v(TAG, "Switched to No operation state");
        }
        newState.setTransportCallbacks(mInternalTransportCallbacks);
        newState.setStatusCallbacks(mStatusCallbacks);
        mMeshMessageState = newState;
    }

    @Override
    public void sendMeshMessage(@NonNull final byte[] src, @NonNull final byte[] dst, @NonNull final MeshMessage meshMessage) {
        final int srcAddress = AddressUtils.getUnicastAddressInt(src);
        final int dstAddress = AddressUtils.getUnicastAddressInt(dst);
       this.sendMeshMessage(srcAddress, dstAddress, meshMessage);
    }

    @Override
    public void sendMeshMessage(final int src, final int dst, @NonNull final MeshMessage meshMessage) {
		if (meshMessage instanceof GenericAccessMessage msg) {
			final GenericAccessMessageState genericAccessMessageState = new GenericAccessMessageState(mContext, src, dst, msg, mMeshTransport, this, msg.getTTL());
			genericAccessMessageState.setTransportCallbacks(mInternalTransportCallbacks);
			genericAccessMessageState.setStatusCallbacks(mStatusCallbacks);
			mMeshMessageState = genericAccessMessageState;
			genericAccessMessageState.executeSend();
		} else if (meshMessage instanceof ProxyConfigMessage) {
            sendProxyConfigMeshMessage(src, dst, (ProxyConfigMessage) meshMessage);
        } else if (meshMessage instanceof ConfigMessage) {
            sendConfigMeshMessage(src, dst, (ConfigMessage) meshMessage);
        } else if (meshMessage instanceof GenericMessage) {
            sendAppMeshMessage(src, dst, (GenericMessage) meshMessage);
        }
    }

    /**
     * Sends a mesh message specified within the {@link MeshMessage} object
     *
     * @param configurationMessage {@link ProxyConfigMessage} Mesh message containing the message opcode and message parameters
     */
    private void sendProxyConfigMeshMessage(final int src, final int dst, @NonNull final ProxyConfigMessage configurationMessage) {

        if (configurationMessage instanceof ProxyConfigSetFilterType) {
            final ProxyConfigSetFilterTypeState proxyConfigSetFilterTypeState = new ProxyConfigSetFilterTypeState(mContext, src, dst,
                    (ProxyConfigSetFilterType) configurationMessage, mMeshTransport, this);
            proxyConfigSetFilterTypeState.setTransportCallbacks(mInternalTransportCallbacks);
            proxyConfigSetFilterTypeState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = proxyConfigSetFilterTypeState;
            proxyConfigSetFilterTypeState.executeSend();
        } else if (configurationMessage instanceof ProxyConfigAddAddressToFilter) {
            final ProxyConfigAddAddressState proxyConfigAddAddressState = new ProxyConfigAddAddressState(mContext, src, dst,
                    (ProxyConfigAddAddressToFilter) configurationMessage, mMeshTransport, this);
            proxyConfigAddAddressState.setTransportCallbacks(mInternalTransportCallbacks);
            proxyConfigAddAddressState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = proxyConfigAddAddressState;
            proxyConfigAddAddressState.executeSend();
        } else if (configurationMessage instanceof ProxyConfigRemoveAddressFromFilter) {
            final ProxyConfigRemoveAddressState proxyConfigRemoveAddressState = new ProxyConfigRemoveAddressState(mContext, src, dst,
                    (ProxyConfigRemoveAddressFromFilter) configurationMessage, mMeshTransport, this);
            proxyConfigRemoveAddressState.setTransportCallbacks(mInternalTransportCallbacks);
            proxyConfigRemoveAddressState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = proxyConfigRemoveAddressState;
            proxyConfigRemoveAddressState.executeSend();
        }
    }

    /**
     * Sends a mesh message specified within the {@link MeshMessage} object
     *
     * @param configurationMessage {@link ConfigMessage} Mesh message containing the message opcode and message parameters
     */
    private void sendConfigMeshMessage(final int src, final int dst, @NonNull final ConfigMessage configurationMessage) {
        final ProvisionedMeshNode node = mInternalTransportCallbacks.getProvisionedNode(dst);
        if (node == null) {
            return;
        }

        if (configurationMessage instanceof ConfigCompositionDataGet) {
            final ConfigCompositionDataGetState compositionDataGetState = new
                    ConfigCompositionDataGetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigCompositionDataGet) configurationMessage, mMeshTransport, this);
            compositionDataGetState.setTransportCallbacks(mInternalTransportCallbacks);
            compositionDataGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = compositionDataGetState;
            compositionDataGetState.executeSend();
        } else if (configurationMessage instanceof ConfigAppKeyAdd) {
            final ConfigAppKeyAddState configAppKeyAddState = new ConfigAppKeyAddState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigAppKeyAdd) configurationMessage, mMeshTransport, this);
            configAppKeyAddState.setTransportCallbacks(mInternalTransportCallbacks);
            configAppKeyAddState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configAppKeyAddState;
            configAppKeyAddState.executeSend();
        } else if (configurationMessage instanceof ConfigModelAppBind) {
            final ConfigModelAppBindState configModelAppBindState = new ConfigModelAppBindState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelAppBind) configurationMessage, mMeshTransport, this);
            configModelAppBindState.setTransportCallbacks(mInternalTransportCallbacks);
            configModelAppBindState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configModelAppBindState;
            configModelAppBindState.executeSend();
        } else if (configurationMessage instanceof ConfigModelAppUnbind) {
            final ConfigModelAppUnbindState modelAppUnbindState = new ConfigModelAppUnbindState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelAppUnbind) configurationMessage, mMeshTransport, this);
            modelAppUnbindState.setTransportCallbacks(mInternalTransportCallbacks);
            modelAppUnbindState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = modelAppUnbindState;
            modelAppUnbindState.executeSend();
        } else if (configurationMessage instanceof ConfigModelPublicationGet) {
            final ConfigModelPublicationGetState configModelPublicationGetState = new ConfigModelPublicationGetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelPublicationGet) configurationMessage, mMeshTransport, this);
            configModelPublicationGetState.setTransportCallbacks(mInternalTransportCallbacks);
            configModelPublicationGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configModelPublicationGetState;
            configModelPublicationGetState.executeSend();
        } else if (configurationMessage instanceof ConfigModelPublicationSet) {
            final ConfigModelPublicationSetState configModelPublicationSetState = new ConfigModelPublicationSetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelPublicationSet) configurationMessage, mMeshTransport, this);
            configModelPublicationSetState.setTransportCallbacks(mInternalTransportCallbacks);
            configModelPublicationSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configModelPublicationSetState;
            configModelPublicationSetState.executeSend();
        } else if (configurationMessage instanceof ConfigModelSubscriptionAdd) {
            final ConfigModelSubscriptionAddState configModelSubscriptionAddState = new ConfigModelSubscriptionAddState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelSubscriptionAdd) configurationMessage, mMeshTransport, this);
            configModelSubscriptionAddState.setTransportCallbacks(mInternalTransportCallbacks);
            configModelSubscriptionAddState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configModelSubscriptionAddState;
            configModelSubscriptionAddState.executeSend();
        } else if (configurationMessage instanceof ConfigModelSubscriptionDelete) {
            final ConfigModelSubscriptionDeleteState configModelSubscriptionDeleteState = new ConfigModelSubscriptionDeleteState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigModelSubscriptionDelete) configurationMessage, mMeshTransport, this);
            configModelSubscriptionDeleteState.setTransportCallbacks(mInternalTransportCallbacks);
            configModelSubscriptionDeleteState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configModelSubscriptionDeleteState;
            configModelSubscriptionDeleteState.executeSend();
        } else if (configurationMessage instanceof ConfigNodeReset) {
            final ConfigNodeResetState configNodeResetState = new ConfigNodeResetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigNodeReset) configurationMessage, mMeshTransport, this);
            configNodeResetState.setTransportCallbacks(mInternalTransportCallbacks);
            configNodeResetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configNodeResetState;
            configNodeResetState.executeSend();
        } else if (configurationMessage instanceof ConfigNetworkTransmitGet) {
            final ConfigNetworkTransmitGetState configNetworkTransmitGetState = new ConfigNetworkTransmitGetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigNetworkTransmitGet) configurationMessage, mMeshTransport, this);
            configNetworkTransmitGetState.setTransportCallbacks(mInternalTransportCallbacks);
            configNetworkTransmitGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configNetworkTransmitGetState;
            configNetworkTransmitGetState.executeSend();
        } else if (configurationMessage instanceof ConfigNetworkTransmitSet) {
            final ConfigNetworkTransmitSetState configNetworkTransmitSetState = new ConfigNetworkTransmitSetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigNetworkTransmitSet) configurationMessage, mMeshTransport, this);
            configNetworkTransmitSetState.setTransportCallbacks(mInternalTransportCallbacks);
            configNetworkTransmitSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configNetworkTransmitSetState;
            configNetworkTransmitSetState.executeSend();
        } else if (configurationMessage instanceof ConfigRelayGet) {
            final ConfigRelayGetState configRelayGetState = new ConfigRelayGetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigRelayGet) configurationMessage, mMeshTransport, this);
            configRelayGetState.setTransportCallbacks(mInternalTransportCallbacks);
            configRelayGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configRelayGetState;
            configRelayGetState.executeSend();
        } else if (configurationMessage instanceof ConfigRelaySet) {
            final ConfigRelaySetState configRelaySetState = new ConfigRelaySetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigRelaySet) configurationMessage, mMeshTransport, this);
            configRelaySetState.setTransportCallbacks(mInternalTransportCallbacks);
            configRelaySetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configRelaySetState;
            configRelaySetState.executeSend();
        } else if (configurationMessage instanceof ConfigProxyGet) {
            final ConfigProxyGetState configProxyGetState = new ConfigProxyGetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigProxyGet) configurationMessage, mMeshTransport, this);
            configProxyGetState.setTransportCallbacks(mInternalTransportCallbacks);
            configProxyGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configProxyGetState;
            configProxyGetState.executeSend();
        } else if (configurationMessage instanceof ConfigProxySet) {
            final ConfigProxySetState configProxySetState = new ConfigProxySetState(mContext, src, dst, node.getDeviceKey(),
                    (ConfigProxySet) configurationMessage, mMeshTransport, this);
            configProxySetState.setTransportCallbacks(mInternalTransportCallbacks);
            configProxySetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = configProxySetState;
            configProxySetState.executeSend();
        }
    }


    /**
     * Sends a mesh message specified within the {@link GenericMessage} object
     * <p> This method can be used specifically when sending an application message with a unicast address or a group address.
     * Application messages currently supported in the library are {@link GenericOnOffGet},{@link GenericOnOffSet}, {@link GenericOnOffSetUnacknowledged},
     * {@link GenericLevelGet},  {@link GenericLevelSet},  {@link GenericLevelSetUnacknowledged},
     * {@link VendorModelMessageAcked} and {@link VendorModelMessageUnacked}</p>
     *
     * @param src            source address where the message is originating from
     * @param dst            Destination to which the message must be sent to, this could be a unicast address or a group address.
     * @param genericMessage Mesh message containing the message opcode and message parameters.
     */
    private void sendAppMeshMessage(final int src, final int dst, @NonNull final GenericMessage genericMessage) {
		if (genericMessage instanceof GenericOnOffGet) {
            final GenericOnOffGetState genericOnOffGetState = new GenericOnOffGetState(mContext, src, dst, (GenericOnOffGet) genericMessage, mMeshTransport, this);
            genericOnOffGetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnOffGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnOffGetState;
            genericOnOffGetState.executeSend();
        } else if (genericMessage instanceof GenericOnOffSet) {
            final GenericOnOffSetState genericOnOffSetState = new GenericOnOffSetState(mContext, src, dst, (GenericOnOffSet) genericMessage, mMeshTransport, this);
            genericOnOffSetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnOffSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnOffSetState;
            genericOnOffSetState.executeSend();
        } else if (genericMessage instanceof GenericOnOffSetUnacknowledged) {
            final GenericOnOffSetUnacknowledgedState genericOnOffSetUnAckedState = new GenericOnOffSetUnacknowledgedState(mContext,
                    src, dst, (GenericOnOffSetUnacknowledged) genericMessage, mMeshTransport, this);
            genericOnOffSetUnAckedState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnOffSetUnAckedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnOffSetUnAckedState;
            genericOnOffSetUnAckedState.executeSend();
        } else if (genericMessage instanceof GenericOnPowerUpGet) {
            final GenericOnPowerUpGetState genericOnPowerUpGetState = new GenericOnPowerUpGetState(mContext, src, dst, (GenericOnPowerUpGet) genericMessage, mMeshTransport, this);
            genericOnPowerUpGetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnPowerUpGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnPowerUpGetState;
            genericOnPowerUpGetState.executeSend();
        } else if (genericMessage instanceof GenericOnPowerUpSet) {
            final GenericOnPowerUpSetState genericOnPowerUpSetState = new GenericOnPowerUpSetState(mContext, src, dst, (GenericOnPowerUpSet) genericMessage, mMeshTransport, this);
            genericOnPowerUpSetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnPowerUpSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnPowerUpSetState;
            genericOnPowerUpSetState.executeSend();
        } else if (genericMessage instanceof GenericOnPowerUpSetUnacknowledged) {
            final GenericOnPowerUpSetUnacknowledgedState genericOnPowerUpSetUnAckedState = new GenericOnPowerUpSetUnacknowledgedState(mContext,
                    src, dst, (GenericOnPowerUpSetUnacknowledged) genericMessage, mMeshTransport, this);
            genericOnPowerUpSetUnAckedState.setTransportCallbacks(mInternalTransportCallbacks);
            genericOnPowerUpSetUnAckedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericOnPowerUpSetUnAckedState;
            genericOnPowerUpSetUnAckedState.executeSend();
        } else if (genericMessage instanceof HealthFaultGet) {
            final HealthFaultGetState state = new HealthFaultGetState(mContext, src, dst, (HealthFaultGet) genericMessage, mMeshTransport, this);
            state.setTransportCallbacks(mInternalTransportCallbacks);
            state.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = state;
            state.executeSend();
        } else if (genericMessage instanceof HealthFaultTest) {
            final HealthFaultTestState state = new HealthFaultTestState(mContext, src, dst, (HealthFaultTest) genericMessage, mMeshTransport, this);
            state.setTransportCallbacks(mInternalTransportCallbacks);
            state.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = state;
            state.executeSend();
        } else if (genericMessage instanceof HealthAttentionGet) {
            final HealthAttentionGetState state = new HealthAttentionGetState(mContext, src, dst, (HealthAttentionGet) genericMessage, mMeshTransport, this);
            state.setTransportCallbacks(mInternalTransportCallbacks);
            state.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = state;
            state.executeSend();
        } else if (genericMessage instanceof HealthAttentionSet) {
            final HealthAttentionSetState state = new HealthAttentionSetState(mContext, src, dst, (HealthAttentionSet) genericMessage, mMeshTransport, this);
            state.setTransportCallbacks(mInternalTransportCallbacks);
            state.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = state;
            state.executeSend();
        } else if (genericMessage instanceof HealthAttentionSetUnacknowledged) {
            final HealthAttentionSetUnacknowledgedState state = new HealthAttentionSetUnacknowledgedState(mContext, src, dst, (HealthAttentionSetUnacknowledged) genericMessage, mMeshTransport, this);
            state.setTransportCallbacks(mInternalTransportCallbacks);
            state.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = state;
            state.executeSend();
        } else if (genericMessage instanceof GenericUserPropertyGet) {
            final GenericUserPropertyGetState genericUserPropertyGetState = new GenericUserPropertyGetState(mContext, src, dst, (GenericUserPropertyGet) genericMessage, mMeshTransport, this);
            genericUserPropertyGetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericUserPropertyGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericUserPropertyGetState;
            genericUserPropertyGetState.executeSend();
        } else if (genericMessage instanceof GenericLevelGet) {
            final GenericLevelGetState genericLevelGetState = new GenericLevelGetState(mContext, src, dst, (GenericLevelGet) genericMessage, mMeshTransport, this);
            genericLevelGetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericLevelGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericLevelGetState;
            genericLevelGetState.executeSend();
        } else if (genericMessage instanceof GenericLevelSet) {
            final GenericLevelSetState genericLevelSetState = new GenericLevelSetState(mContext, src, dst, (GenericLevelSet) genericMessage, mMeshTransport, this);
            genericLevelSetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericLevelSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericLevelSetState;
            genericLevelSetState.executeSend();
        } else if (genericMessage instanceof GenericLevelSetUnacknowledged) {
            final GenericLevelSetUnacknowledgedState genericLevelSetUnackedState = new GenericLevelSetUnacknowledgedState(mContext, src,
                    dst, (GenericLevelSetUnacknowledged) genericMessage, mMeshTransport, this);
            genericLevelSetUnackedState.setTransportCallbacks(mInternalTransportCallbacks);
            genericLevelSetUnackedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericLevelSetUnackedState;
            genericLevelSetUnackedState.executeSend();
        }
        else if (genericMessage instanceof GenericMoveSet) {
            final GenericMoveSetState genericMoveSetState = new GenericMoveSetState(mContext, src, dst, (GenericMoveSet) genericMessage, mMeshTransport, this);
            genericMoveSetState.setTransportCallbacks(mInternalTransportCallbacks);
            genericMoveSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericMoveSetState;
            genericMoveSetState.executeSend();
        } else if (genericMessage instanceof GenericMoveSetUnacknowledged) {
            final GenericMoveSetUnacknowledgedState genericMoveSetUnackedState = new GenericMoveSetUnacknowledgedState(mContext, src,
                    dst, (GenericMoveSetUnacknowledged) genericMessage, mMeshTransport, this);
            genericMoveSetUnackedState.setTransportCallbacks(mInternalTransportCallbacks);
            genericMoveSetUnackedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = genericMoveSetUnackedState;
            genericMoveSetUnackedState.executeSend();
        }
        else if (genericMessage instanceof LightLightnessGet) {
            final LightLightnessGetState lightLightnessGetState = new LightLightnessGetState(mContext, src, dst, (LightLightnessGet) genericMessage, mMeshTransport, this);
            lightLightnessGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessGetState;
            lightLightnessGetState.executeSend();
        } else if (genericMessage instanceof LightLightnessSet) {
            final LightLightnessSetState lightLightnessSetState = new LightLightnessSetState(mContext, src, dst, (LightLightnessSet) genericMessage, mMeshTransport, this);
            lightLightnessSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessSetState;
            lightLightnessSetState.executeSend();
        } else if (genericMessage instanceof LightLightnessSetUnacknowledged) {
            final LightLightnessSetUnacknowledgedState lightLightnessSetUnacknowledgedState = new LightLightnessSetUnacknowledgedState(mContext, src, dst,
                    (LightLightnessSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightLightnessSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessSetUnacknowledgedState;
            lightLightnessSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof LightCtlGet) {
            final LightCtlGetState lightCtlGetState = new LightCtlGetState(mContext, src, dst, (LightCtlGet) genericMessage, mMeshTransport, this);
            lightCtlGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlGetState;
            lightCtlGetState.executeSend();
        } else if (genericMessage instanceof LightCtlSet) {
            final LightCtlSetState lightCtlSetState = new LightCtlSetState(mContext, src, dst, (LightCtlSet) genericMessage, mMeshTransport, this);
            lightCtlSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlSetState;
            lightCtlSetState.executeSend();
        } else if (genericMessage instanceof LightCtlSetUnacknowledged) {
            final LightCtlSetUnacknowledgedState lightCtlSetUnacknowledgedState = new LightCtlSetUnacknowledgedState(mContext, src,
                    dst, (LightCtlSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightCtlSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlSetUnacknowledgedState;
            lightCtlSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof LightHslGet) {
            final LightHslGetState lightHslGetState = new LightHslGetState(mContext, src, dst, (LightHslGet) genericMessage, mMeshTransport, this);
            lightHslGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslGetState;
            lightHslGetState.executeSend();
        } else if (genericMessage instanceof LightHslSet) {
            final LightHslSetState lightHslSetState = new LightHslSetState(mContext, src, dst, (LightHslSet) genericMessage, mMeshTransport, this);
            lightHslSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslSetState;
            lightHslSetState.executeSend();
        } else if (genericMessage instanceof LightHslSetUnacknowledged) {
            final LightHslSetUnacknowledgedState lightHslSetUnacknowledgedState = new LightHslSetUnacknowledgedState(mContext, src,
                    dst, (LightHslSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightHslSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslSetUnacknowledgedState;
            lightHslSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof LightLightnessDefaultGet) {
            final LightLightnessDefaultGetState lightLightnessDefaultGetState = new LightLightnessDefaultGetState(mContext, src, dst, (LightLightnessDefaultGet) genericMessage, mMeshTransport, this);
            lightLightnessDefaultGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessDefaultGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessDefaultGetState;
            lightLightnessDefaultGetState.executeSend();
        } else if (genericMessage instanceof LightLightnessDefaultSet) {
            final LightLightnessDefaultSetState lightLightnessDefaultSetState = new LightLightnessDefaultSetState(mContext, src, dst, (LightLightnessDefaultSet) genericMessage, mMeshTransport, this);
            lightLightnessDefaultSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessDefaultSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessDefaultSetState;
            lightLightnessDefaultSetState.executeSend();
        } else if (genericMessage instanceof LightLightnessDefaultSetUnacknowledged) {
            final LightLightnessDefaultSetUnacknowledgedState lightLightnessDefaultSetUnacknowledgedState = new LightLightnessDefaultSetUnacknowledgedState(mContext, src, dst,
                    (LightLightnessDefaultSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightLightnessDefaultSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightLightnessDefaultSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightLightnessDefaultSetUnacknowledgedState;
            lightLightnessDefaultSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof LightCtlDefaultGet) {
            final LightCtlDefaultGetState lightCtlDefaultGetState = new LightCtlDefaultGetState(mContext, src, dst, (LightCtlDefaultGet) genericMessage, mMeshTransport, this);
            lightCtlDefaultGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlDefaultGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlDefaultGetState;
            lightCtlDefaultGetState.executeSend();
        } else if (genericMessage instanceof LightCtlDefaultSet) {
            final LightCtlDefaultSetState lightCtlDefaultSetState = new LightCtlDefaultSetState(mContext, src, dst, (LightCtlDefaultSet) genericMessage, mMeshTransport, this);
            lightCtlDefaultSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlDefaultSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlDefaultSetState;
            lightCtlDefaultSetState.executeSend();
        } else if (genericMessage instanceof LightCtlDefaultSetUnacknowledged) {
            final LightCtlDefaultSetUnacknowledgedState lightCtlDefaultSetUnacknowledgedState = new LightCtlDefaultSetUnacknowledgedState(mContext, src,
                    dst, (LightCtlDefaultSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightCtlDefaultSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightCtlDefaultSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightCtlDefaultSetUnacknowledgedState;
            lightCtlDefaultSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof LightHslDefaultGet) {
            final LightHslDefaultGetState lightHslDefaultGetState = new LightHslDefaultGetState(mContext, src, dst, (LightHslDefaultGet) genericMessage, mMeshTransport, this);
            lightHslDefaultGetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslDefaultGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslDefaultGetState;
            lightHslDefaultGetState.executeSend();
        } else if (genericMessage instanceof LightHslDefaultSet) {
            final LightHslDefaultSetState lightHslDefaultSetState = new LightHslDefaultSetState(mContext, src, dst, (LightHslDefaultSet) genericMessage, mMeshTransport, this);
            lightHslDefaultSetState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslDefaultSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslDefaultSetState;
            lightHslDefaultSetState.executeSend();
        } else if (genericMessage instanceof LightHslDefaultSetUnacknowledged) {
            final LightHslDefaultSetUnacknowledgedState lightHslDefaultSetUnacknowledgedState = new LightHslDefaultSetUnacknowledgedState(mContext, src,
                    dst, (LightHslDefaultSetUnacknowledged) genericMessage, mMeshTransport, this);
            lightHslDefaultSetUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            lightHslDefaultSetUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = lightHslDefaultSetUnacknowledgedState;
            lightHslDefaultSetUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof VendorModelMessageAcked) {
            final VendorModelMessageAckedState message = new VendorModelMessageAckedState(mContext, src, dst, (VendorModelMessageAcked) genericMessage, mMeshTransport, this);
            message.setTransportCallbacks(mInternalTransportCallbacks);
            message.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = message;
            message.executeSend();
        } else if (genericMessage instanceof VendorModelMessageUnacked) {
            final VendorModelMessageUnackedState vendorModelMessageUnackedState = new VendorModelMessageUnackedState(mContext, src,
                    dst, (VendorModelMessageUnacked) genericMessage, mMeshTransport, this);
            vendorModelMessageUnackedState.setTransportCallbacks(mInternalTransportCallbacks);
            vendorModelMessageUnackedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = vendorModelMessageUnackedState;
            vendorModelMessageUnackedState.executeSend();
        } else if (genericMessage instanceof SceneGet) {
            final SceneGetState sceneGetState = new SceneGetState(mContext, src, dst, (SceneGet) genericMessage, mMeshTransport, this);
            sceneGetState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneGetState;
            sceneGetState.executeSend();
        } else if (genericMessage instanceof SceneRegisterGet) {
            final SceneRegisterGetState sceneGetState = new SceneRegisterGetState(mContext, src, dst, (SceneRegisterGet) genericMessage, mMeshTransport, this);
            sceneGetState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneGetState;
            sceneGetState.executeSend();
        } else if (genericMessage instanceof SceneStore) {
            final SceneStoreState sceneStoreState = new SceneStoreState(mContext, src, dst, (SceneStore) genericMessage, mMeshTransport, this);
            sceneStoreState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneStoreState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneStoreState;
            sceneStoreState.executeSend();
        } else if (genericMessage instanceof SceneStoreUnacknowledged) {
            final SceneStoreUnacknowledgedState sceneStoreUnacknowledgedState = new SceneStoreUnacknowledgedState(mContext, src,
                    dst, (SceneStoreUnacknowledged) genericMessage, mMeshTransport, this);
            sceneStoreUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneStoreUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneStoreUnacknowledgedState;
            sceneStoreUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof SceneDelete) {
            final SceneDeleteState sceneDeleteState = new SceneDeleteState(mContext, src, dst, (SceneDelete) genericMessage, mMeshTransport, this);
            sceneDeleteState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneDeleteState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneDeleteState;
            sceneDeleteState.executeSend();
        } else if (genericMessage instanceof SceneDeleteUnacknowledged) {
            final SceneDeleteUnacknowledgedState sceneDeleteUnacknowledgedState = new SceneDeleteUnacknowledgedState(mContext, src,
                    dst, (SceneDeleteUnacknowledged) genericMessage, mMeshTransport, this);
            sceneDeleteUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneDeleteUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneDeleteUnacknowledgedState;
            sceneDeleteUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof SceneRecall) {
            final SceneRecallState sceneRecallState = new SceneRecallState(mContext, src, dst, (SceneRecall) genericMessage, mMeshTransport, this);
            sceneRecallState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneRecallState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneRecallState;
            sceneRecallState.executeSend();
        } else if (genericMessage instanceof SceneRecallUnacknowledged) {
            final SceneRecallUnacknowledgedState sceneRecallUnacknowledgedState = new SceneRecallUnacknowledgedState(mContext, src, dst,
                    (SceneRecallUnacknowledged) genericMessage, mMeshTransport, this);
            sceneRecallUnacknowledgedState.setTransportCallbacks(mInternalTransportCallbacks);
            sceneRecallUnacknowledgedState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = sceneRecallUnacknowledgedState;
            sceneRecallUnacknowledgedState.executeSend();
        } else if (genericMessage instanceof TimeGet) {
            final TimeGetState timeGetState = new TimeGetState(mContext, src, dst, (TimeGet) genericMessage, mMeshTransport, this);
            timeGetState.setTransportCallbacks(mInternalTransportCallbacks);
            timeGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timeGetState;
            timeGetState.executeSend();
        } else if (genericMessage instanceof TimeSet) {
            final TimeSetState timeSetState = new TimeSetState(mContext, src, dst, (TimeSet) genericMessage, mMeshTransport, this);
            timeSetState.setTransportCallbacks(mInternalTransportCallbacks);
            timeSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timeSetState;
            timeSetState.executeSend();
        } else if (genericMessage instanceof TimezoneGet) {
            final TimezoneGetState timezoneGetState = new TimezoneGetState(mContext, src, dst, (TimezoneGet) genericMessage, mMeshTransport, this);
            timezoneGetState.setTransportCallbacks(mInternalTransportCallbacks);
            timezoneGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timezoneGetState;
            timezoneGetState.executeSend();
        } else if (genericMessage instanceof TimezoneSet) {
            final TimezoneSetState timezoneSetState = new TimezoneSetState(mContext, src, dst, (TimezoneSet) genericMessage, mMeshTransport, this);
            timezoneSetState.setTransportCallbacks(mInternalTransportCallbacks);
            timezoneSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timezoneSetState;
            timezoneSetState.executeSend();
        } else if (genericMessage instanceof TimeRoleGet) {
            final TimeRoleGetState timeRoleGetState = new TimeRoleGetState(mContext, src, dst, (TimeRoleGet) genericMessage, mMeshTransport, this);
            timeRoleGetState.setTransportCallbacks(mInternalTransportCallbacks);
            timeRoleGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timeRoleGetState;
            timeRoleGetState.executeSend();
        } else if (genericMessage instanceof TimeRoleSet) {
            final TimeRoleSetState timeRoleSetState = new TimeRoleSetState(mContext, src, dst, (TimeRoleSet) genericMessage, mMeshTransport, this);
            timeRoleSetState.setTransportCallbacks(mInternalTransportCallbacks);
            timeRoleSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = timeRoleSetState;
            timeRoleSetState.executeSend();
        } else if (genericMessage instanceof TaiUtcDeltaGet) {
            final TaiUtcDeltaGetState taiUtcDeltaGetState = new TaiUtcDeltaGetState(mContext, src, dst, (TaiUtcDeltaGet) genericMessage, mMeshTransport, this);
            taiUtcDeltaGetState.setTransportCallbacks(mInternalTransportCallbacks);
            taiUtcDeltaGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = taiUtcDeltaGetState;
            taiUtcDeltaGetState.executeSend();
        } else if (genericMessage instanceof TaiUtcDeltaSet) {
            final TaiUtcDeltaSetState taiUtcDeltaSetState = new TaiUtcDeltaSetState(mContext, src, dst, (TaiUtcDeltaSet) genericMessage, mMeshTransport, this);
            taiUtcDeltaSetState.setTransportCallbacks(mInternalTransportCallbacks);
            taiUtcDeltaSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = taiUtcDeltaSetState;
            taiUtcDeltaSetState.executeSend();
        } else if (genericMessage instanceof SchedulerGet) {
            final SchedulerGetState schedulerGetState = new SchedulerGetState(mContext, src, dst, (SchedulerGet) genericMessage, mMeshTransport, this);
            schedulerGetState.setTransportCallbacks(mInternalTransportCallbacks);
            schedulerGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = schedulerGetState;
            schedulerGetState.executeSend();
        } else if (genericMessage instanceof SchedulerActionGet) {
            final SchedulerActionGetState schedulerActionGetState = new SchedulerActionGetState(mContext, src, dst, (SchedulerActionGet) genericMessage, mMeshTransport, this);
            schedulerActionGetState.setTransportCallbacks(mInternalTransportCallbacks);
            schedulerActionGetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = schedulerActionGetState;
            schedulerActionGetState.executeSend();
        } else if (genericMessage instanceof SchedulerActionSet) {
            final SchedulerActionSetState schedulerActionSetState = new SchedulerActionSetState(mContext, src, dst, (SchedulerActionSet) genericMessage, mMeshTransport, this);
            schedulerActionSetState.setTransportCallbacks(mInternalTransportCallbacks);
            schedulerActionSetState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = schedulerActionSetState;
            schedulerActionSetState.executeSend();
        } else if (genericMessage instanceof  SchedulerActionSetUnacknowledged) {
            final SchedulerActionSetUnacknowledgedState schedulerActionSetUnacknowledgedStateState = new SchedulerActionSetUnacknowledgedState(mContext, src, dst, (SchedulerActionSet) genericMessage, mMeshTransport, this);
            schedulerActionSetUnacknowledgedStateState.setTransportCallbacks(mInternalTransportCallbacks);
            schedulerActionSetUnacknowledgedStateState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = schedulerActionSetUnacknowledgedStateState;
            schedulerActionSetUnacknowledgedStateState.executeSend();
        } else if (genericMessage instanceof BLOBTransferStart) {
            final BLOBTransferStartState blobTransferStartState = new BLOBTransferStartState(mContext, src, dst, (BLOBTransferStart) genericMessage, mMeshTransport, this);
            blobTransferStartState.setTransportCallbacks(mInternalTransportCallbacks);
            blobTransferStartState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = blobTransferStartState;
            blobTransferStartState.executeSend();
        } else if (genericMessage instanceof BLOBBlockStart) {
            final BLOBBlockStartState blobBlockStartState = new BLOBBlockStartState(mContext, src, dst, (BLOBBlockStart) genericMessage, mMeshTransport, this);
            blobBlockStartState.setTransportCallbacks(mInternalTransportCallbacks);
            blobBlockStartState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = blobBlockStartState;
            blobBlockStartState.executeSend();
        } else if (genericMessage instanceof BLOBBlockGet) {
			final BLOBBlockGetState blobBlockGetState = new BLOBBlockGetState(mContext, src, dst, (BLOBBlockGet) genericMessage, mMeshTransport, this);
			blobBlockGetState.setTransportCallbacks(mInternalTransportCallbacks);
			blobBlockGetState.setStatusCallbacks(mStatusCallbacks);
			mMeshMessageState = blobBlockGetState;
			blobBlockGetState.executeSend();
		} else if (genericMessage instanceof BLOBChunkTransfer) {
            final BLOBChunkTransferState blobChunkTransferState = new BLOBChunkTransferState(mContext, src, dst, (BLOBChunkTransfer) genericMessage, mMeshTransport, this);
            blobChunkTransferState.setTransportCallbacks(mInternalTransportCallbacks);
            blobChunkTransferState.setStatusCallbacks(mStatusCallbacks);
            mMeshMessageState = blobChunkTransferState;
            blobChunkTransferState.executeSend();
        }
        // TIMING
         else {
            //TODO
        }
    }
}
