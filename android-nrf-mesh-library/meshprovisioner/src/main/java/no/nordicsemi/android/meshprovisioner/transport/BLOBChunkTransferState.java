package no.nordicsemi.android.meshprovisioner.transport;

	import android.content.Context;
	import android.util.Log;

	import androidx.annotation.NonNull;

	import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;


/**
 * State class for handling BLOBChunkTransfer messages.
 */
@SuppressWarnings("unused")
class BLOBChunkTransferState extends GenericMessageState {

	private static final String TAG = BLOBChunkTransferState.class.getSimpleName();

	/**
	 * Constructs BLOBTransferStartStateState
	 */
	BLOBChunkTransferState(@NonNull final Context context,
						   final int src,
						   final int dst,
						   @NonNull final BLOBChunkTransfer blobTransferStart,
						   @NonNull final MeshTransport meshTransport,
						   @NonNull final InternalMeshMsgHandlerCallbacks callbacks) throws IllegalArgumentException {
		super(context, src, dst, blobTransferStart, meshTransport, callbacks);
		createAccessMessage();
	}

	@Override
	public MessageState getState() {
		return MessageState.BLOB_CHUNK_TRANSFER_STATE;
	}

	/**
	 * Creates the access message to be sent to the node
	 */
	private void createAccessMessage() {
		final BLOBChunkTransfer blobTransferStart = (BLOBChunkTransfer) mMeshMessage;
		final byte[] key = blobTransferStart.getAppKey();
		final int akf = blobTransferStart.getAkf();
		final int aid = blobTransferStart.getAid();
		final int aszmic = blobTransferStart.getAszmic();
		final int opCode = blobTransferStart.getOpCode();
		final byte[] parameters = blobTransferStart.getParameters();
		message = mMeshTransport.createMeshMessage(mSrc, mDst, key, akf, aid, aszmic, opCode, parameters);
		blobTransferStart.setMessage(message);
	}

	@Override
	public void executeSend() {
		Log.v(TAG, "Sending chunk transfer");
		super.executeSend();

		if (message.getNetworkPdu().size() > 0) {
			if (mMeshStatusCallbacks != null) mMeshStatusCallbacks.onMeshMessageSent(mDst, mMeshMessage);
		}
	}
}
