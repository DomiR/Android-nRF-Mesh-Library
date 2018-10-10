package no.nordicsemi.android.meshprovisioner.messages;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import no.nordicsemi.android.meshprovisioner.meshmessagestates.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a unacknowledged VendorModel message.
 */
@SuppressWarnings("unused")
public class VendorModelMessageUnacked extends GenericMessage {

    private static final String TAG = VendorModelMessageUnacked.class.getSimpleName();

    private final int mCompanyIdentifier;
    private final int opCode;

    /**
     * Constructs VendorModelMessageAcked message.
     *
     * @param node                 Mesh node this message is to be sent to
     * @param appKey               Application key for this message
     * @param companyIdentifier    Company identifier of the vendor model
     * @param aszmic               Size of message integrity check
     * @throws IllegalArgumentException if any illegal arguments are passed
     */
    public VendorModelMessageUnacked(@NonNull final ProvisionedMeshNode node,
                                     @NonNull final byte[] appKey,
                                     final int companyIdentifier,
                                     final int opCode,
                                     @Nullable final byte[] parameters,
                                     final int aszmic) {
        super(node, appKey, aszmic);
        this.mCompanyIdentifier = companyIdentifier;
        this.opCode = opCode;
        mParameters = parameters;
        assembleMessageParameters();
    }

    @Override
    final void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey);
    }

    @Override
    public int getOpCode() {
        return opCode;
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
