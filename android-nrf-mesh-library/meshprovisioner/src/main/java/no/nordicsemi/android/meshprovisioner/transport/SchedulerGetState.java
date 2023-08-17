package no.nordicsemi.android.meshprovisioner.transport;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;


/**
 * State class for handling SchedulerGet messages.
 */
@SuppressWarnings("unused")
class SchedulerGetState extends GenericMessageState {

    private static final String TAG = SchedulerGetState.class.getSimpleName();

    /**
     * Constructs SchedulerGetState
     *
     * @param context         Context of the application
     * @param src             Source address
     * @param dst             Destination address to which the message must be sent to
     * @param schedulerGet Wrapper class {@link SchedulerGet} containing the opcode and parameters for {@link SchedulerGet} message
     * @param callbacks       {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException for any illegal arguments provided.
     */
    @Deprecated
    SchedulerGetState(@NonNull final Context context,
                         @NonNull final byte[] src,
                         @NonNull final byte[] dst,
                         @NonNull final SchedulerGet schedulerGet,
                         @NonNull final MeshTransport meshTransport,
                         @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(context, MeshParserUtils.bytesToInt(src), MeshParserUtils.bytesToInt(dst), schedulerGet, meshTransport, callbacks);        createAccessMessage();
    }

    /**
     * Constructs SchedulerGetState
     *
     * @param context         Context of the application
     * @param src             Source address
     * @param dst             Destination address to which the message must be sent to
     * @param schedulerGet Wrapper class {@link SchedulerGet} containing the opcode and parameters for {@link SchedulerGet} message
     * @param callbacks       {@link InternalMeshMsgHandlerCallbacks} for internal callbacks
     * @throws IllegalArgumentException for any illegal arguments provided.
     */
    SchedulerGetState(@NonNull final Context context,
                         final int src,
                         final int dst,
                         @NonNull final SchedulerGet schedulerGet,
                         @NonNull final MeshTransport meshTransport,
                         @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
        super(context, src, dst, schedulerGet, meshTransport, callbacks);
        createAccessMessage();
    }

    @Override
    public MessageState getState() {
        return MessageState.SCHEDULER_GET_STATE;
    }

    /**
     * Creates the access message to be sent to the node
     */
    private void createAccessMessage() {
        final SchedulerGet m = (SchedulerGet) mMeshMessage;
        final byte[] key = m.getAppKey();
        final int akf = m.getAkf();
        final int aid = m.getAid();
        final int aszmic = m.getAszmic();
        final int opCode = m.getOpCode();
        final byte[] parameters = m.getParameters();
        message = mMeshTransport.createMeshMessage(mSrc, mDst, key, akf, aid, aszmic, opCode, parameters);
    }

    @Override
    public void executeSend() {
        Log.v(TAG, "Sending time get");
        super.executeSend();

        if (message.getNetworkPdu().size() > 0) {
            if (mMeshStatusCallbacks != null)
                mMeshStatusCallbacks.onMeshMessageSent(mDst, mMeshMessage);
        }
    }
}
