package no.nordicsemi.android.meshprovisioner.messages;

import android.support.annotation.NonNull;

import no.nordicsemi.android.meshprovisioner.meshmessagestates.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating an acknowledged VendorMode message.
 */
@SuppressWarnings("unused")
public class VendorModelMessageAcked extends GenericMessage {

    private static final String TAG = VendorModelMessageAcked.class.getSimpleName();
    private static final int VENDOR_MODEL_OPCODE_LENGTH = 4;

    private final int mCompanyIdentifier;
    private final int mOpCode;

    /**
     * Constructs VendorModelMessageAcked message.
     *
     * @param node                 Mesh node this message is to be sent to
     * @param appKey               Application key for this message
     * @param companyIdentifier    Company identifier of the vendor model
     * @param aszmic               Size of message integrity check
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public VendorModelMessageAcked(@NonNull final ProvisionedMeshNode node,
                                   @NonNull final byte[] appKey,
                                   final int companyIdentifier,
                                   final int opCode,
                                   @NonNull final byte[] parameters,
                                   final int aszmic) {
        super(node, appKey, aszmic);
        this.mCompanyIdentifier = companyIdentifier;
        this.mOpCode = opCode;
        mParameters = parameters;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return mOpCode;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey);
    }

    /**
     * Returns the company identifier of the model
     *
     * @return 16-bit company identifier
     */
    public final int getCompanyIdentifier() {
        return mCompanyIdentifier;
    }
}
