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

import android.content.Context;
import android.os.Handler;
import androidx.annotation.VisibleForTesting;
import android.util.Log;

import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.Provisioner;
import no.nordicsemi.android.meshprovisioner.utils.ExtendedInvalidCipherTextException;
import no.nordicsemi.android.meshprovisioner.utils.MeshAddress;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

public final class MeshTransport extends NetworkLayer {

    private static final String TAG = MeshTransport.class.getSimpleName();
    private static final int PROXY_CONFIGURATION_TTL = 0;

    MeshTransport(final Context context) {
        this.mContext = context;
        initHandler();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    MeshTransport(final Context context, final ProvisionedMeshNode node) {
        super();
        this.mContext = context;
        this.mMeshNode = node;
        initHandler();
    }

    @Override
    protected final void initHandler() {
        this.mHandler = new Handler(mContext.getMainLooper());
    }

    @Override
    public final void setLowerTransportLayerCallbacks(final LowerTransportLayerCallbacks callbacks) {
        super.setLowerTransportLayerCallbacks(callbacks);
    }

    public final void setNetworkLayerCallbacks(final NetworkLayerCallbacks callbacks) {
        this.mNetworkLayerCallbacks = callbacks;
    }


    public final void setUpperTransportLayerCallbacks(final UpperTransportLayerCallbacks callbacks) {
        this.mUpperTransportLayerCallbacks = callbacks;
    }

    @Override
    protected final int incrementSequenceNumber(final int src) {
        final Provisioner provisioner = mNetworkLayerCallbacks.getProvisioner(src);
        return provisioner.incrementSequenceNumber();
    }

    @Override
    protected final int incrementSequenceNumber(final int src, final byte[] sequenceNumber) {
        final Provisioner provisioner = mNetworkLayerCallbacks.getProvisioner(src);
        final int seqNumber = MeshParserUtils.getSequenceNumber(sequenceNumber);
        provisioner.setSequenceNumber(seqNumber);
        return provisioner.incrementSequenceNumber();
    }

    /**
     * Creates the an acknowledgement message for the received segmented messages
     *
     * @param controlMessage Control message containing the required opcodes and parameters to create the message
     * @return Control message containing the acknowledgement message pdu
     */
    final ControlMessage createSegmentBlockAcknowledgementMessage(final ControlMessage controlMessage) {
        createLowerTransportControlPDU(controlMessage);
        createNetworkLayerPDU(controlMessage);
        return controlMessage;
    }

    /**
     * Creates an access message to be sent to the peripheral node
     * <p>
     * This method will create the access message and propagate the message through the transport layers to create the final mesh pdu.
     * </p>
     *
     * @param src                     Source address of the provisioner/configurator.
     * @param dst                     destination address to be sent to
     * @param key                     Key could be application key or device key.
     * @param akf                     Application key flag defines which key to be used to decrypt the message i.e device key or application key.
     * @param aid                     Identifier of the application key.
     * @param aszmic                  Defines the length of the transport mic length where 1 will encrypt withn 64 bit and 0 with 32 bit encryption.
     * @param accessOpCode            Operation code for the access message.
     * @param accessMessageParameters Parameters for the access message.
     * @return access message containing the mesh pdu
     */
    final AccessMessage createMeshMessage(final int src, final int dst,
                                          final byte[] key, final int akf, final int aid, final int aszmic,
                                          final int accessOpCode, final byte[] accessMessageParameters) {
        return createMeshMessage(src, dst, key, akf, aid, aszmic, accessOpCode, accessMessageParameters, 8);
    }

    final AccessMessage createMeshMessage(final int src, final int dst,
                                          final byte[] key, final int akf, final int aid, final int aszmic,
                                          final int accessOpCode, final byte[] accessMessageParameters, final int ttl) {
        final int sequenceNumber = incrementSequenceNumber(src);
        final byte[] sequenceNum = MeshParserUtils.getSequenceNumberBytes(sequenceNumber);

        Log.v(TAG, "Access Message:");
        Log.v(TAG, "    Src address: " + MeshAddress.formatAddress(src, false));
        Log.v(TAG, "    Dst address: " + MeshAddress.formatAddress(dst, false));
        Log.v(TAG, "    Key: " + MeshParserUtils.bytesToHex(key, false));
        Log.v(TAG, "    Akf: " + akf);
        Log.v(TAG, "    TTL: " + ttl);
        Log.v(TAG, "    Aid: " + aid);
        Log.v(TAG, "    Aszmic: " + aszmic);
        Log.v(TAG, "    Sequence number: " + sequenceNumber);
        Log.v(TAG, "    Access message opcode: " + Integer.toHexString(accessOpCode));
        Log.v(TAG, "    Access message parameters: " + MeshParserUtils.bytesToHex(accessMessageParameters, false));

        final AccessMessage message = new AccessMessage();
        message.setSrc(src);
        message.setDst(dst);
        message.setIvIndex(mUpperTransportLayerCallbacks.getIvIndex());
        message.setSequenceNumber(sequenceNum);
        message.setKey(key);
        message.setAkf(akf);
        message.setAid(aid);
        message.setAszmic(aszmic);
        message.setOpCode(accessOpCode);
        message.setParameters(accessMessageParameters);
        message.setPduType(MeshManagerApi.PDU_TYPE_NETWORK);
        message.setTtl(ttl);

        super.createMeshMessage(message);
        return message;
    }

    /**
     * Creates a vendor model access message to be sent to the peripheral node
     * <p>
     * This method will create the access message and propagate the message through the transport layers to create the final mesh pdu.
     * </p>
     *
     * @param src                     Source address of the provisioner/configurator.
     * @param dst                     destination address to be sent to
     * @param key                     Key could be application key or device key.
     * @param akf                     Application key flag defines which key to be used to decrypt the message i.e device key or application key.
     * @param aid                     Identifier of the application key.
     * @param aszmic                  Defines the length of the transport mic length where 1 will encrypt withn 64 bit and 0 with 32 bit encryption.
     * @param accessOpCode            Operation code for the access message.
     * @param accessMessageParameters Parameters for the access message.
     * @return access message containing the mesh pdu
     */
    final AccessMessage createVendorMeshMessage(final int companyIdentifier, final int src, final int dst,
                                                final byte[] key, final int akf, final int aid, final int aszmic,
                                                final int accessOpCode, final byte[] accessMessageParameters) {
        final int sequenceNumber = incrementSequenceNumber(src);
        final byte[] sequenceNum = MeshParserUtils.getSequenceNumberBytes(sequenceNumber);

        Log.v(TAG, "Src address: " + MeshAddress.formatAddress(src, false));
        Log.v(TAG, "Dst address: " + MeshAddress.formatAddress(dst, false));
        Log.v(TAG, "Key: " + MeshParserUtils.bytesToHex(key, false));
        Log.v(TAG, "akf: " + akf);
        Log.v(TAG, "aid: " + aid);
        Log.v(TAG, "aszmic: " + aszmic);
        Log.v(TAG, "Sequence number: " + sequenceNumber);
        Log.v(TAG, "Access message opcode: " + Integer.toHexString(accessOpCode));
        Log.v(TAG, "Access message parameters: " + MeshParserUtils.bytesToHex(accessMessageParameters, false));

        final AccessMessage message = new AccessMessage();
        message.setCompanyIdentifier(companyIdentifier);
        message.setSrc(src);
        message.setDst(dst);
        message.setIvIndex(mUpperTransportLayerCallbacks.getIvIndex());
        message.setSequenceNumber(sequenceNum);
        message.setKey(key);
        message.setAkf(akf);
        message.setAid(aid);
        message.setAszmic(aszmic);
        message.setOpCode(accessOpCode);
        message.setParameters(accessMessageParameters);
        message.setPduType(MeshManagerApi.PDU_TYPE_NETWORK);

        super.createVendorMeshMessage(message);
        return message;
    }

    /**
     * Creates a proxy configuration message to be sent to the peripheral node
     *
     * @param src        Source address of the provisioner/configurator.
     * @param dst        destination address to be sent to
     * @param opcode     Operation code for the access message.
     * @param parameters Parameters for the access message.
     * @return Control message containing the proxy configuration pdu
     */
    final ControlMessage createProxyConfigurationMessage(final int src,
                                                         final int dst,
                                                         final int opcode, final byte[] parameters) {
        final int sequenceNumber = incrementSequenceNumber(src);
        final byte[] sequenceNum = MeshParserUtils.getSequenceNumberBytes(sequenceNumber);

        Log.v(TAG, "Src address: " + MeshAddress.formatAddress(src, false));
        Log.v(TAG, "Dst address: " + MeshAddress.formatAddress(dst, false));
        Log.v(TAG, "Sequence number: " + sequenceNumber);
        Log.v(TAG, "Control message opcode: " + Integer.toHexString(opcode));
        Log.v(TAG, "Control message parameters: " + MeshParserUtils.bytesToHex(parameters, false));

        final ControlMessage message = new ControlMessage();
        message.setSrc(src);
        message.setDst(dst);
        message.setTtl(PROXY_CONFIGURATION_TTL); //TTL for proxy configuration messages are set to 0
        message.setIvIndex(mUpperTransportLayerCallbacks.getIvIndex());
        message.setSequenceNumber(sequenceNum);
        message.setOpCode(opcode);
        message.setParameters(parameters);
        message.setPduType(MeshManagerApi.PDU_TYPE_PROXY_CONFIGURATION);

        super.createMeshMessage(message);
        return message;
    }

    final Message createRetransmitMeshMessage(final Message message, final int segment) {
        return createRetransmitNetworkLayerPDU(message, segment);
    }

    /**
     * Parses the received pdu
     *
     * @param pdu pdu received
     * @return Message
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    final Message parsePdu(final byte[] pdu) throws ExtendedInvalidCipherTextException {
        return parseMeshMessage(pdu);
    }
}
