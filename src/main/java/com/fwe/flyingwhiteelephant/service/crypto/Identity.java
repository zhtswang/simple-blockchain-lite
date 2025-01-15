package com.fwe.flyingwhiteelephant.service.crypto;

import lombok.*;

import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HexFormat;

@Getter
@Setter
@AllArgsConstructor
public class Identity {
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private static final String SHA_256_WITH_ECDSA = "SHA256withECDSA";

    @SneakyThrows
    public String sign(String data) {
        Signature signature = Signature.getInstance(SHA_256_WITH_ECDSA);
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return HexFormat.of().formatHex(signature.sign());
    }

    @SneakyThrows
    public boolean verify(String data, String signature) {
        // Decode the public key
        Signature sign = Signature.getInstance(SHA_256_WITH_ECDSA);
        sign.initVerify(certificate.getPublicKey());
        sign.update(data.getBytes());
        return sign.verify(HexFormat.of().parseHex(signature));
    }

    @SneakyThrows
    public String toString() {
        return "-----BEGIN CERTIFICATE-----\n" + Base64.getEncoder().encodeToString(certificate.getEncoded()) + "\n-----END CERTIFICATE-----";
    }
}


