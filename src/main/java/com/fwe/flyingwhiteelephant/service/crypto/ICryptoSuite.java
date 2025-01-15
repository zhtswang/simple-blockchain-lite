package com.fwe.flyingwhiteelephant.service.crypto;

import java.security.cert.X509Certificate;
import java.util.Map;

public interface ICryptoSuite {
    void initCryptoSuit();

    Map<IdentityType, Identity> getWalletBundle();
    X509Certificate readCertificatePEM(String certPEM);
}
