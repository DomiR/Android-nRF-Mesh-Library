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
 * To be used as a wrapper class for when creating the GenericOnOffStatus Message.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class BLOBBlockStatus extends GenericStatusMessage implements Parcelable {

	private static final String TAG = BLOBBlockStatus.class.getSimpleName();
	private static final int OP_CODE = ApplicationMessageOpCodes.BLOB_BLOCK_STATUS;


	private int status;
	private int format;
	private int blockNumber;
	private int chunkSize;
	private byte[] missingChunks;

	private static final Creator<BLOBBlockStatus> CREATOR = new Creator<BLOBBlockStatus>() {
		@Override
		public BLOBBlockStatus createFromParcel(Parcel in) {
			final AccessMessage message = in.readParcelable(AccessMessage.class.getClassLoader());
			//noinspection ConstantConditions
			return new BLOBBlockStatus(message);
		}

		@Override
		public BLOBBlockStatus[] newArray(int size) {
			return new BLOBBlockStatus[size];
		}
	};

	/**
	 * Constructs the GenericOnOffStatus mMessage.
	 *
	 * @param message Access Message
	 */
	public BLOBBlockStatus(@NonNull final AccessMessage message) {
		super(message);
		this.mParameters = message.getParameters();
		parseStatusParameters();
	}

	@Override
	void parseStatusParameters() {

		final ByteBuffer buffer = ByteBuffer.wrap(mParameters).order(ByteOrder.LITTLE_ENDIAN);
		int statusFormat = (buffer.get() & 0xFF);
		this.status = statusFormat >> 4;
		// 2 bits for the format
		this.format = statusFormat & 0x03;

		this.blockNumber = buffer.getShort();
		this.chunkSize = buffer.getShort();

		// missing chunks are an array
		this.missingChunks = new byte[buffer.remaining()];
		buffer.get(this.missingChunks);

		Log.v(TAG, "Received block status from: " + MeshAddress.formatAddress(mMessage.getSrc(), true) + " status: " + status + " format: " + format + " block number: " + blockNumber + " chunk size: " + chunkSize + " missing chunks: " + MeshParserUtils.bytesToHex(missingChunks, false));
	}

	@Override
	int getOpCode() {
		return OP_CODE;
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

	public final int getStatus() {
		return status;
	}

	public final int getFormat() {
		return format;
	}

	public final int getBlockNumber() {
		return blockNumber;
	}

	public final int getChunkSize() {
		return chunkSize;
	}

	public final byte[] getMissingChunks() {
		return missingChunks;
	}
}
