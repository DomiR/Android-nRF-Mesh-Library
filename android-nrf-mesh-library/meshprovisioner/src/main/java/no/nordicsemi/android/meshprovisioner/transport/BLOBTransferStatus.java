/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.meshprovisioner.transport;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.meshprovisioner.opcodes.ConfigMessageOpCodes;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

/**
 * To be used as a wrapper class for when creating the BLOBTransferStatus Message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class BLOBTransferStatus extends ConfigStatusMessage implements Parcelable {

    private static final String TAG = BLOBTransferStatus.class.getSimpleName();
    private static final int OP_CODE = ConfigMessageOpCodes.CONFIG_APPKEY_STATUS;

    private int mStatus;
    private int mTransferMode;
    private int mTransferPhase;
    private byte[] blobId;
    private int mBlobSize;
    private int mBlockSizeLog;
    private int mTransferMTUSize;


    public static final Creator<BLOBTransferStatus> CREATOR = new Creator<BLOBTransferStatus>() {
        @Override
        public BLOBTransferStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            //noinspection ConstantConditions
            return new BLOBTransferStatus(message);
        }

        @Override
        public BLOBTransferStatus[] newArray(int size) {
            return new BLOBTransferStatus[size];
        }
    };

    /**
     * Constructs the BLOBTransferStatus mMessage.
     *
     * @param message Access Message
     */
    public BLOBTransferStatus(@NonNull final AccessMessage message) {
        super(message);
        this.mParameters = message.getParameters();
        parseStatusParameters();
    }

    @Override
    final void parseStatusParameters() {
        // mStatusCode = mParameters[0];
        // mStatusCodeName = getStatusCodeName(mStatusCode);

        // final byte[] netKeyIndex = new byte[]{(byte) (mParameters[2] & 0x0F), mParameters[1]};
        // mNetKeyIndex = ByteBuffer.wrap(netKeyIndex).order(ByteOrder.BIG_ENDIAN).getShort();

        // final byte[] appKeyIndex = new byte[]{(byte) ((mParameters[3] & 0xF0) >> 4), (byte) (mParameters[3] << 4 | ((mParameters[2] & 0xF0) >> 4))};
        // mAppKeyIndex = ByteBuffer.wrap(appKeyIndex).order(ByteOrder.BIG_ENDIAN).getShort();

        // Log.v(TAG, "Status code: " + mStatusCode);
        // Log.v(TAG, "Status message: " + mStatusCodeName);
        // Log.v(TAG, "Net key index: " + MeshParserUtils.bytesToHex(netKeyIndex, false));
        // Log.v(TAG, "App key index: " + MeshParserUtils.bytesToHex(appKeyIndex, false));
    }

    @Override
    public final int getOpCode() {
        return OP_CODE;
    }

    // /**
    //  * Returns the global index of the net key.
    //  *
    //  * @return netkey index
    //  */
    // public final int getNetKeyIndex() {
    //     return mNetKeyIndex;
    // }

    // /**
    //  * Returns the global app key index.
    //  *
    //  * @return appkey index
    //  */
    // public final int getAppKeyIndex() {
    //     return mAppKeyIndex;
    // }

    // /**
    //  * Returns if the message was successful
    //  *
    //  * @return true if the message was successful or false otherwise
    //  */
    // public final boolean isSuccessful() {
    //     return mStatusCode == 0x00;
    // }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        final AccessMessage message = (AccessMessage) mMessage;
        dest.writeParcelable(message, flags);
    }
}
