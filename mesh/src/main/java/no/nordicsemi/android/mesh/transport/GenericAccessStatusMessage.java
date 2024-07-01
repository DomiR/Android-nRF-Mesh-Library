package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;

/**
 * Allows sending arbitrary access messages
 */
public class GenericAccessStatusMessage extends ApplicationStatusMessage {

    private static final String TAG = GenericAccessStatusMessage.class.getSimpleName();
	private final int mOpcode;

    public GenericAccessStatusMessage(AccessMessage message) {
        super(message);
		this.mParameters = message.getParameters();
		this.mOpcode = message.getOpCode();
    }


	@Override
	void parseStatusParameters() {

	}

	@Override
    public int getOpCode() {
        return mOpcode;
    }
}


