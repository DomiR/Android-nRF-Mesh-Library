package no.nordicsemi.android.meshprovisioner.transport;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

/**
 * State class for handling LightCtlSetState messages.
 */
@SuppressWarnings("unused")
class LightHslDefaultSetState extends GenericMessageState implements LowerTransportLayerCallbacks {

    private static final String TAG = LightHslDefaultSetState.class.getSimpleName();

    /**
     * Constructs {@link LightHslDefaultSetState}
     *
     * @param context     Context of the application
     * @param src         Source address
     * @param dst         Destination address to which the message must be sent to
     * @param lightHslDefaultSet Wrapper class {@link LightLightnessSet} containing the opcode and parameters for {@link GenericLevelSet} message
     * @param callbacks   {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException for any illegal arguments provided.
     */
    @Deprecated
    LightHslDefaultSetState(@NonNull final Context context,
                     @NonNull final byte[] src,
                     @NonNull final byte[] dst,
                     @NonNull final LightHslDefaultSet lightHslDefaultSet,
                     @NonNull final MeshTransport meshTransport,
                     @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(context, MeshParserUtils.bytesToInt(src), MeshParserUtils.bytesToInt(dst), lightHslDefaultSet, meshTransport, callbacks);
    }

    /**
     * Constructs {@link LightHslDefaultSetState}
     *
     * @param context     Context of the application
     * @param src         Source address
     * @param dst         Destination address to which the message must be sent to
     * @param lightHslDefaultSet Wrapper class {@link LightLightnessSet} containing the opcode and parameters for {@link GenericLevelSet} message
     * @param callbacks   {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException for any illegal arguments provided.
     */
    LightHslDefaultSetState(@NonNull final Context context,
                     final int src,
                     final int dst,
                     @NonNull final LightHslDefaultSet lightHslDefaultSet,
                     @NonNull final MeshTransport meshTransport,
                     @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(context, src, dst, lightHslDefaultSet, meshTransport, callbacks);
        createAccessMessage();
    }

    @Override
    public MessageState getState() {
        return MessageState.LIGHT_HSL_DEFAULT_SET_STATE;
    }

    /**
     * Creates the access message to be sent to the node
     */
    private void createAccessMessage() {
        final LightHslDefaultSet lightHslDefaultSet = (LightHslDefaultSet) mMeshMessage;
        final byte[] key = lightHslDefaultSet.getAppKey();
        final int akf = lightHslDefaultSet.getAkf();
        final int aid = lightHslDefaultSet.getAid();
        final int aszmic = lightHslDefaultSet.getAszmic();
        final int opCode = lightHslDefaultSet.getOpCode();
        final byte[] parameters = lightHslDefaultSet.getParameters();
        message = mMeshTransport.createMeshMessage(mSrc, mDst, key, akf, aid, aszmic, opCode, parameters);
        lightHslDefaultSet.setMessage(message);
    }

    @Override
    public final void executeSend() {
        Log.v(TAG, "Sending light hsl set acknowledged ");
        super.executeSend();
        if (message.getNetworkPdu().size() > 0) {
            if (mMeshStatusCallbacks != null)
                mMeshStatusCallbacks.onMeshMessageSent(mDst, mMeshMessage);
        }
    }
}
