package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * Allows sending arbitrary access messages
 */
public class GenericAccessMessage extends MeshMessage {

    private static final String TAG = GenericAccessMessage.class.getSimpleName();
	private final int mOpcode;
	private final byte[] mParameters;
	private final byte[] mKey;
	private final boolean mIsConfigMessage;


    public GenericAccessMessage(final int opcode, @NonNull final byte[] payload, final boolean isConfigMessage, @NonNull final byte[] key, int ttl) {
        super();
        mOpcode = opcode;
        mParameters = payload;
		mKey = key;
		mIsConfigMessage = isConfigMessage;
		messageTtl = ttl;
    }

	public final int getAkf() {
		return this.mIsConfigMessage ? 0 : 1;
	}

	public final int getAid() {
		if (mIsConfigMessage) {
			return 0;
		} else {
			return SecureUtils.calculateK4(this.mKey);
		}
	}

	@Override
    public int getOpCode() {
        return mOpcode;
    }

	public final byte[] getParameters() {
		return mParameters;
	}

	public final byte[] getKey(){
		return mKey;
	}

}


