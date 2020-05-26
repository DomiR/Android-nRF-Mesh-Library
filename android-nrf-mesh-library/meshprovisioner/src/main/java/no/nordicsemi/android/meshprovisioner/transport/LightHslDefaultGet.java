package no.nordicsemi.android.meshprovisioner.transport;


import androidx.annotation.NonNull;

import no.nordicsemi.android.meshprovisioner.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;

/**
 * To be used as a wrapper class to create light hsl get message.
 */
@SuppressWarnings("unused")
public class LightHslDefaultGet extends GenericMessage {

    private static final String TAG = LightHslDefaultGet.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.LIGHT_HSL_DEFAULT_GET;


    /**
     * Constructs LightHslDefaultGet message.
     *
     * @param appKey application key for this message
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public LightHslDefaultGet(@NonNull final byte[] appKey) throws IllegalArgumentException {
        super(appKey);
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey);
    }
}
