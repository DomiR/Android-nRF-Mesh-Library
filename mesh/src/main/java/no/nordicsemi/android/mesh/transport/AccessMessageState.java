package no.nordicsemi.android.mesh.transport;

import java.util.UUID;

import androidx.annotation.NonNull;
import no.nordicsemi.android.mesh.InternalTransportCallbacks;
import no.nordicsemi.android.mesh.MeshStatusCallbacks;
import no.nordicsemi.android.mesh.utils.MeshAddress;

/**
 * Abstract state class that handles Application Message States
 */
class AccessMessageState extends MeshMessageState {

    UUID mLabel;

    /**
     * Constructs the application message state
     *
     * @param src                Source address
     * @param dst                Destination address
     * @param meshMessage        {@link MeshMessage} to be sent
     * @param meshTransport      {@link MeshTransport} transport
     * @param handlerCallbacks   {@link InternalMeshMsgHandlerCallbacks} callbacks
     * @param transportCallbacks {@link InternalTransportCallbacks} callbacks
     * @param statusCallbacks    {@link MeshStatusCallbacks} callbacks
     * @throws IllegalArgumentException if src or dst address is invalid
     */
    AccessMessageState(final int src,
                            final int dst,
                            @NonNull final GenericAccessMessage meshMessage,
                            @NonNull final MeshTransport meshTransport,
                            @NonNull final InternalMeshMsgHandlerCallbacks handlerCallbacks,
                            @NonNull final InternalTransportCallbacks transportCallbacks,
                            @NonNull  final MeshStatusCallbacks statusCallbacks) throws IllegalArgumentException {
        super(meshMessage, meshTransport, handlerCallbacks, transportCallbacks, statusCallbacks);
        this.mSrc = src;
        if (!MeshAddress.isAddressInRange(src)) {
            throw new IllegalArgumentException("Invalid address, a source address must be a valid 16-bit value!");
        }
        this.mDst = dst;
        if (!MeshAddress.isAddressInRange(dst)) {
            throw new IllegalArgumentException("Invalid address, a destination address must be a valid 16-bit value");
        }
        createAccessMessage();
    }


    /**
     * Creates the access message to be sent
     */
    protected void createAccessMessage() {
        final GenericAccessMessage accessMessage = (GenericAccessMessage) mMeshMessage;
        final byte[] key = accessMessage.getKey();
        final int akf = accessMessage.getAkf();
        final int aid = accessMessage.getAid();
        final int aszmic = accessMessage.getAszmic();
        final int opCode = accessMessage.getOpCode();
        final byte[] parameters = accessMessage.getParameters();
        message = mMeshTransport.createMeshMessage(mSrc, mDst, accessMessage.messageTtl, key, akf, aid, aszmic, opCode, parameters);
        accessMessage.setMessage(message);
    }

    @Override
    public MessageState getState() {
        return MessageState.GENERIC_ACCESS_MESSAGE_STATE;
    }
}
