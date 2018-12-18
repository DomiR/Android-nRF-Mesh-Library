package no.nordicsemi.android.meshprovisioner.transport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * State class for handling GenericLevelSet messages.
 */
class LightLightnessSetUnacknowledgedState extends GenericMessageState implements LowerTransportLayerCallbacks {

    private static final String TAG = LightLightnessSetUnacknowledgedState.class.getSimpleName();

    /**
     * Constructs {@link LightLightnessSetUnacknowledgedState}
     *
     * @param context         Context of the application
     * @param dstAddress      Destination address to which the message must be sent to
     * @param lightLightnessSetUnacknowledged Wrapper class {@link LightLightnessSetUnacknowledged} containing the opcode and parameters for {@link GenericLevelSet} message
     * @param callbacks       {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException for any illegal arguments provided.
     */
    LightLightnessSetUnacknowledgedState(@NonNull final Context context,
										 @NonNull final byte[] dstAddress,
										 @NonNull final LightLightnessSetUnacknowledged lightLightnessSetUnacknowledged,
										 @NonNull final MeshTransport meshTransport,
										 @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(context, dstAddress, lightLightnessSetUnacknowledged, meshTransport, callbacks);
        createAccessMessage();
    }

    @Override
    public MessageState getState() {
        return MessageState.LIGHT_LIGHTNESS_SET_UNACKNOWLEDGED_STATE;
    }

    /**
     * Creates the access message to be sent to the node
     */
    private void createAccessMessage() {
        final LightLightnessSetUnacknowledged lightLightnessSetUnacknowledged = (LightLightnessSetUnacknowledged) mMeshMessage;
        final byte[] key = lightLightnessSetUnacknowledged.getAppKey();
        final int akf = lightLightnessSetUnacknowledged.getAkf();
        final int aid = lightLightnessSetUnacknowledged.getAid();
        final int aszmic = lightLightnessSetUnacknowledged.getAszmic();
        final int opCode = lightLightnessSetUnacknowledged.getOpCode();
        final byte[] parameters = lightLightnessSetUnacknowledged.getParameters();
        message = mMeshTransport.createMeshMessage(mNode, mSrc, mDstAddress, key, akf, aid, aszmic, opCode, parameters);
        lightLightnessSetUnacknowledged.setMessage(message);
    }

    @Override
    public final void executeSend() {
        Log.v(TAG, "Sending Generic Level set acknowledged ");
        super.executeSend();
        if (message.getNetworkPdu().size() > 0) {
            if (mMeshStatusCallbacks != null) {
                mMeshStatusCallbacks.onMeshMessageSent(mMeshMessage);
                //We must update update the mesh network state here for unacknowledged messages
                //If not the sequence numbers would be invalid for unacknowledged messages and will be dropped by the node.
                //Mesh network state for acknowledged messages are updated in the DefaultNoOperationState once the status is received.
                mInternalTransportCallbacks.updateMeshNetwork(mMeshMessage);
            }
        }
    }
}