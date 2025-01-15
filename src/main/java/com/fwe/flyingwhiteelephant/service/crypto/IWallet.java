package com.fwe.flyingwhiteelephant.service.crypto;

public interface IWallet {
    Identity getIdentity(IdentityType identityType);
    void saveIdentity(Identity identity);
}
