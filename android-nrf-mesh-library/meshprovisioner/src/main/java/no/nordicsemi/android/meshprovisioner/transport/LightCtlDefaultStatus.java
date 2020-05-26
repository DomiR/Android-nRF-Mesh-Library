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

import no.nordicsemi.android.meshprovisioner.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.meshprovisioner.utils.MeshAddress;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

/**
 * To be used as a wrapper class to create generic level status message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class LightCtlDefaultStatus extends GenericStatusMessage implements Parcelable {

    private static final String TAG = LightCtlDefaultStatus.class.getSimpleName();
    private static final int LIGHT_CTL_STATUS_MANDATORY_LENGTH = 6;
    private static final int OP_CODE = ApplicationMessageOpCodes.LIGHT_CTL_DEFAULT_STATUS;
    private int mCtlLightness;
    private int mCtlTemperature;
    private int mCtlDeltaUV;

    private static final Creator<LightCtlDefaultStatus> CREATOR = new Creator<LightCtlDefaultStatus>() {
        @Override
        public LightCtlDefaultStatus createFromParcel(Parcel in) {
            final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
            //noinspection ConstantConditions
            return new LightCtlDefaultStatus(message);
        }

        @Override
        public LightCtlDefaultStatus[] newArray(int size) {
            return new LightCtlDefaultStatus[size];
        }
    };

    /**
     * Constructs LightCtlDefaultStatus message
     *
     * @param message access message
     */
    public LightCtlDefaultStatus(@NonNull final AccessMessage message) {
        super(message);
        this.mMessage = message;
        this.mParameters = message.getParameters();
        parseStatusParameters();
    }

    @Override
    void parseStatusParameters() {
        Log.v(TAG, "Received light ctl default status from: " + MeshAddress.formatAddress(mMessage.getSrc(), true));
        final ByteBuffer buffer = ByteBuffer.wrap(mParameters).order(ByteOrder.LITTLE_ENDIAN);
        mCtlLightness = buffer.getShort() & 0xFFFF;
        mCtlTemperature = buffer.getShort() & 0xFFFF;
        mCtlDeltaUV = buffer.getShort() & 0xFFFF;
        Log.v(TAG, "Default lightness: " + mCtlLightness);
        Log.v(TAG, "Default temperature: " + mCtlTemperature);
        Log.v(TAG, "Default delta uv: " + mCtlDeltaUV);
    }

    @Override
    int getOpCode() {
        return OP_CODE;
    }

    /**
     * Returns the Default level of the GenericOnOffModel
     *
     * @return Default level
     */
    public final int getDefaultLightness() {
        return mCtlLightness;
    }


    /**
     * Returns the Default level of the GenericOnOffModel
     *
     * @return Default level
     */
    public final int getDefaultTemperature() {
        return mCtlTemperature;
    }

    /**
     * Returns the Default level of the GenericOnOffModel
     *
     * @return Default level
     */
    public final int getDefaultDeltaUV() {
        return mCtlDeltaUV;
    }

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
