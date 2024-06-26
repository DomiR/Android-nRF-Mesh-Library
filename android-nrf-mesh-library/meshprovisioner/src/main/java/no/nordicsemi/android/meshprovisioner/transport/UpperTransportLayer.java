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

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.Log;

import org.spongycastle.crypto.InvalidCipherTextException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.utils.ExtendedInvalidCipherTextException;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;
import no.nordicsemi.android.meshprovisioner.utils.SecureUtils;

abstract class UpperTransportLayer extends AccessLayer {
    private static final int PROXY_CONFIG_OPCODE_LENGTH = 1;
    static final int MAX_UNSEGMENTED_ACCESS_PAYLOAD_LENGTH = 15;
    static final int MAX_SEGMENTED_ACCESS_PAYLOAD_LENGTH = 12;
    static final int MAX_UNSEGMENTED_CONTROL_PAYLOAD_LENGTH = 11;
    static final int MAX_SEGMENTED_CONTROL_PAYLOAD_LENGTH = 8;
    /**
     * Nonce types
     **/
    static final int NONCE_TYPE_NETWORK = 0x00;
    static final int NONCE_TYPE_PROXY = 0x03;
    /**
     * Nonce paddings
     **/
    static final int PAD_NETWORK_NONCE = 0x00;
    static final int PAD_PROXY_NONCE = 0x00;
    private static final int APPLICATION_KEY_IDENTIFIER = 0; //Identifies that the device key is to be used
    private static final int NONCE_TYPE_APPLICATION = 0x01;
    private static final int NONCE_TYPE_DEVICE = 0x02;
    private static final int PAD_APPLICATION_DEVICE_NONCE = 0b0000000;
    private static final String TAG = UpperTransportLayer.class.getSimpleName();
    private static final int SZMIC = 1; //Transmic becomes 8 bytes
    private static final int TRANSPORT_SAR_SEQZERO_MASK = 8191;
    private static final int DEFAULT_UNSEGMENTED_MIC_LENGTH = 4; //octets
    private static final int MINIMUM_TRANSMIC_LENGTH = 4; // bytes
    private static final int MAXIMUM_TRANSMIC_LENGTH = 8; // bytes

    UpperTransportLayerCallbacks mUpperTransportLayerCallbacks;

    /**
     * Creates a mesh message containing an upper transport access pdu
     *
     * @param message The access message required to create the encrypted upper transport pdu
     */
    void createMeshMessage(final Message message) { //Access message
        if (message instanceof AccessMessage) {
            super.createMeshMessage(message);
            final AccessMessage accessMessage = (AccessMessage) message;
            final byte[] encryptedTransportPDU = encryptUpperTransportPDU(accessMessage);
            Log.v(TAG, "Encrypted upper transport pdu: " + MeshParserUtils.bytesToHex(encryptedTransportPDU, false));
            accessMessage.setUpperTransportPdu(encryptedTransportPDU);
        } else {
            createUpperTransportPDU(message);
        }
    }

    /**
     * Creates a vendor model mesh message containing an upper transport access pdu
     *
     * @param message The access message required to create the encrypted upper transport pdu
     */
    void createVendorMeshMessage(final Message message) { //Access message
        super.createVendorMeshMessage(message);
        final AccessMessage accessMessage = (AccessMessage) message;
        final byte[] encryptedTransportPDU = encryptUpperTransportPDU(accessMessage);
        Log.v(TAG, "Encrypted upper transport pdu: " + MeshParserUtils.bytesToHex(encryptedTransportPDU, false));
        accessMessage.setUpperTransportPdu(encryptedTransportPDU);
    }

    /**
     * Creates the upper transport access pdu
     *
     * @param message The message required to create the encrypted upper transport pdu
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    void createUpperTransportPDU(final Message message) {
        if (message instanceof AccessMessage) {
            //Access message
            final AccessMessage accessMessage = (AccessMessage) message;
            final byte[] encryptedTransportPDU = encryptUpperTransportPDU(accessMessage);
            Log.v(TAG, "Encrypted upper transport pdu: " + MeshParserUtils.bytesToHex(encryptedTransportPDU, false));
            accessMessage.setUpperTransportPdu(encryptedTransportPDU);
        } else {
            final ControlMessage controlMessage = (ControlMessage) message;
            final int opCode = controlMessage.getOpCode();
            final byte[] parameters = controlMessage.getParameters();
            final ByteBuffer accessMessageBuffer;
            if (parameters != null) {
                accessMessageBuffer = ByteBuffer.allocate(PROXY_CONFIG_OPCODE_LENGTH + parameters.length)
                        .order(ByteOrder.BIG_ENDIAN)
                        .put((byte) opCode)
                        .put(parameters);
            } else {
                accessMessageBuffer = ByteBuffer.allocate(PROXY_CONFIG_OPCODE_LENGTH);
                accessMessageBuffer.put((byte) opCode);
            }
            final byte[] accessPdu = accessMessageBuffer.array();

            Log.v(TAG, "Created Transport Control PDU " + MeshParserUtils.bytesToHex(accessPdu, false));
            controlMessage.setTransportControlPdu(accessPdu);
        }
    }

    /**
     * Creates lower transport pdu
     */
    abstract void createLowerTransportAccessPDU(final AccessMessage message);

    /**
     * Creates lower transport pdu
     */
    abstract void createLowerTransportControlPDU(final ControlMessage message);

    /**
     * Removes the lower transport layer header and reassembles a segented lower transport access pdu in to one message
     *
     * @param accessMessage access message containing the lower transport pdus
     */
    abstract void reassembleLowerTransportAccessPDU(final AccessMessage accessMessage);


    /**
     * Removes the lower transport layer header and reassembles a segented lower transport control pdu in to one message
     *
     * @param controlMessage control message containing the lower transport pdus
     */
    abstract void reassembleLowerTransportControlPDU(final ControlMessage controlMessage);

    /**
     * Parse upper transport pdu
     *
     * @param message access message containing the upper transport pdu
     */
    final void parseUpperTransportPDU(@NonNull final Message message) throws ExtendedInvalidCipherTextException {
        try {

            switch (message.getPduType()) {
                case MeshManagerApi.PDU_TYPE_NETWORK:
                    if (message.getCtl() == 0) { //Access message
                        final AccessMessage accessMessage = (AccessMessage) message;
                        reassembleLowerTransportAccessPDU(accessMessage);
                        final byte[] decryptedUpperTransportControlPdu = decryptUpperTransportPDU(accessMessage);
                        accessMessage.setAccessPdu(decryptedUpperTransportControlPdu);
                    } else {
                        //TODO
                        //this where control messages such as heartbeat and friendship messages are to be implemented
                    }
                    break;
                case MeshManagerApi.PDU_TYPE_PROXY_CONFIGURATION:
                    final ControlMessage controlMessage = (ControlMessage) message;
                    if (controlMessage.getLowerTransportControlPdu().size() == 1) {
                        final byte[] lowerTransportControlPdu = controlMessage.getLowerTransportControlPdu().get(0);
                        final ByteBuffer buffer = ByteBuffer.wrap(lowerTransportControlPdu)
                                .order(ByteOrder.BIG_ENDIAN);
                        message.setOpCode(buffer.get());
                        final byte[] parameters = new byte[buffer.capacity() - 1];
                        buffer.get(parameters);
                        message.setParameters(parameters);
                    }
                    break;
            }
        } catch (InvalidCipherTextException ex) {
            throw new ExtendedInvalidCipherTextException(ex.getMessage(), ex.getCause(), TAG);
        }
    }

    /**
     * Encrypts upper transport pdu
     *
     * @param message access message object containing the upper transport pdu
     * @return encrypted upper transport pdu
     */
    private byte[] encryptUpperTransportPDU(final AccessMessage message) {
        final byte[] accessPDU = message.getAccessPdu();
        final int akf = message.getAkf();
        final int aszmic = message.getAszmic(); // upper transport layer will alaways have the aszmic as 0 because the mic is always 32bit

        final byte[] sequenceNumber = message.getSequenceNumber();
        final int src = message.getSrc();
        final int dst = message.getDst();
        final byte[] ivIndex = message.getIvIndex();
        final byte[] key = message.getKey();

        byte[] nonce;
        if (akf == APPLICATION_KEY_IDENTIFIER) {
            nonce = createDeviceNonce(aszmic, sequenceNumber, src, dst, ivIndex);
            Log.v(TAG, "Device nonce: " + MeshParserUtils.bytesToHex(nonce, false));
        } else {
            nonce = createApplicationNonce(aszmic, sequenceNumber, src, dst, ivIndex);
            Log.v(TAG, "Application nonce: " + MeshParserUtils.bytesToHex(nonce, false));
        }

        int transMicLength;
        final int encryptedPduLength = accessPDU.length + MINIMUM_TRANSMIC_LENGTH;

        if (encryptedPduLength <= MAX_UNSEGMENTED_ACCESS_PAYLOAD_LENGTH) {
            transMicLength = SecureUtils.getTransMicLength(message.getCtl());
        } else {
            transMicLength = SecureUtils.getTransMicLength(message.getAszmic());
        }

        return SecureUtils.encryptCCM(accessPDU, key, nonce, transMicLength);
    }

    /**
     * Decrypts upper transport pdu
     *
     * @param accessMessage access message object containing the upper transport pdu
     * @return decrypted upper transport pdu
     */
    private byte[] decryptUpperTransportPDU(final AccessMessage accessMessage) throws InvalidCipherTextException {
        byte[] decryptedUpperTransportPDU;
        final byte[] key;
        //Check if the key used for encryption is an application key or a device key
        final byte[] nonce;
        if (APPLICATION_KEY_IDENTIFIER == accessMessage.getAkf()) {
            key = mMeshNode.getDeviceKey();
            Log.v(TAG, "Decrypt with device key: " + MeshParserUtils.bytesToHex(key, false));

            //If its a device key that was used to encrypt the message we need to create a device nonce to decrypt it
            nonce = createDeviceNonce(accessMessage.getAszmic(), accessMessage.getSequenceNumber(), accessMessage.getSrc(), accessMessage.getDst(), accessMessage.getIvIndex());
            Log.v(TAG, "Decrypt with nonce: " + MeshParserUtils.bytesToHex(nonce, false));

        } else {
            key = mUpperTransportLayerCallbacks.getApplicationKey(accessMessage.getAid());
            if (key == null)
                throw new IllegalArgumentException("Unable to find the app key to decrypt the message for aid: " + accessMessage.getAid());

            final int aid = SecureUtils.calculateK4(key);
            if (aid != accessMessage.getAid()) {
                throw new IllegalArgumentException("Unable to decrypt the message, invalid application key identifier");
            }
            //If its an application key that was used to encrypt the message we need to create a application nonce to decrypt it
            nonce = createApplicationNonce(accessMessage.getAszmic(), accessMessage.getSequenceNumber(), accessMessage.getSrc(), accessMessage.getDst(), accessMessage.getIvIndex());
        }

        if (accessMessage.getAszmic() == SZMIC) {
            decryptedUpperTransportPDU = SecureUtils.decryptCCM(accessMessage.getUpperTransportPdu(), key, nonce, MAXIMUM_TRANSMIC_LENGTH);
        } else {
            decryptedUpperTransportPDU = SecureUtils.decryptCCM(accessMessage.getUpperTransportPdu(), key, nonce, MINIMUM_TRANSMIC_LENGTH);
        }

        final byte[] tempBytes = new byte[decryptedUpperTransportPDU.length];
        ByteBuffer decryptedBuffer = ByteBuffer.wrap(tempBytes);
        decryptedBuffer.order(ByteOrder.LITTLE_ENDIAN);
        decryptedBuffer.put(decryptedUpperTransportPDU);
        decryptedUpperTransportPDU = decryptedBuffer.array();
        return decryptedUpperTransportPDU;
    }

    /**
     * Creates the application nonce
     *
     * @param aszmic         aszmic (szmic if a segmented access message)
     * @param sequenceNumber sequence number of the message
     * @param src            source address
     * @param dst            destination address
     * @return Application nonce
     */
    private byte[] createApplicationNonce(final int aszmic, final byte[] sequenceNumber, final int src, final int dst, final byte[] ivIndex) {
        final ByteBuffer applicationNonceBuffer = ByteBuffer.allocate(13);
        applicationNonceBuffer.put((byte) NONCE_TYPE_APPLICATION); //Nonce type
        applicationNonceBuffer.put((byte) ((aszmic << 7) | PAD_APPLICATION_DEVICE_NONCE)); //ASZMIC (SZMIC if a segmented access message) and PAD
        applicationNonceBuffer.put(sequenceNumber);
        applicationNonceBuffer.putShort((short) src);
        applicationNonceBuffer.putShort((short) dst);
        applicationNonceBuffer.put(ivIndex);
        return applicationNonceBuffer.array();
    }

    /**
     * Creates the device nonce
     *
     * @param aszmic         aszmic (szmic if a segmented access message)
     * @param sequenceNumber sequence number of the message
     * @param src            source address
     * @param dst            destination address
     * @return Device  nonce
     */
    private byte[] createDeviceNonce(final int aszmic, final byte[] sequenceNumber, final int src, final int dst, final byte[] ivIndex) {
        final ByteBuffer deviceNonceBuffer = ByteBuffer.allocate(13);
        deviceNonceBuffer.put((byte) NONCE_TYPE_DEVICE); //Nonce type
        deviceNonceBuffer.put((byte) ((aszmic << 7) | PAD_APPLICATION_DEVICE_NONCE)); //ASZMIC (SZMIC if a segmented access message) and PAD
        deviceNonceBuffer.put(sequenceNumber);
        deviceNonceBuffer.putShort((short) src);
        deviceNonceBuffer.putShort((short) dst);
        deviceNonceBuffer.put(ivIndex);
        return deviceNonceBuffer.array();
    }

    /**
     * Derives the original transport layer sequence number from the network layer sequence number that was received with every segment
     *
     * @param networkLayerSequenceNumber sequence number on network layer which is a part of the original pdu received
     * @param seqZero                    the lower 13 bits of the sequence number. This is a part of the lower transport pdu header and is the same value for all segments
     * @return original transport layer sequence number that was used to encrypt the transport layer pdu
     */
    final int getTransportLayerSequenceNumber(final int networkLayerSequenceNumber, final int seqZero) {
        if ((networkLayerSequenceNumber & TRANSPORT_SAR_SEQZERO_MASK) < seqZero) {
            return ((networkLayerSequenceNumber - ((networkLayerSequenceNumber & TRANSPORT_SAR_SEQZERO_MASK) - seqZero) - (TRANSPORT_SAR_SEQZERO_MASK + 1)));
        } else {
            return ((networkLayerSequenceNumber - ((networkLayerSequenceNumber & TRANSPORT_SAR_SEQZERO_MASK) - seqZero)));
        }
    }

}
