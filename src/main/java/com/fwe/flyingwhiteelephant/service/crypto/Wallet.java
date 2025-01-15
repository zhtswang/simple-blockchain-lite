package com.fwe.flyingwhiteelephant.service.crypto;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Component
public class Wallet implements IWallet {
    @Resource
    private ICryptoSuite cryptoSuite;

    @Override
    public Identity getIdentity(IdentityType identityType) {
        return cryptoSuite.getWalletBundle().get(identityType);
    }

    /**
     * Get the identity from the certificate and private key, if only want to verify the signature, the private key can be null
     * @param certificate
     * @param privateKey
     * @return Identity
     */
    public Identity getIdentity(X509Certificate certificate, PrivateKey privateKey) {
        return new Identity(certificate, privateKey);
    }

    /*verify the signature, no need private key as Identity*/
    public Identity getIdentity(X509Certificate certificate) {
        return getIdentity(certificate, null);
    }

    public Identity getIdentity(String certificate) {
        return getIdentity(cryptoSuite.readCertificatePEM(certificate));
    }

    @Override
    public void saveIdentity(Identity identity) {
       throw new UnsupportedOperationException("Not implemented yet");
    }
}
