package com.fwe.flyingwhiteelephant.service.crypto;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.cert.X509v3CertificateBuilder;


@Component
@Getter
@Slf4j
public class CryptoSuite implements ICryptoSuite, InitializingBean {
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    private static final String SHA_256_WITH_ECDSA = "SHA256withECDSA";
    private final static String EC_NAME = "secp256r1";
    private static final String ALGORITHM = "EC";
    private static final long ONE_YEAR = 365L * 24 * 60 * 60 * 1000;


    @Value("${blockchain.crypto.privatePath:/fwe}/${node.id}/keys/")
    private String keysPath;

    private Map<IdentityType, Identity> walletBundle;

    /***
     * Initialize the crypto suit, it will generate root CA and private key,
     * - generate node signature key and crt
     * - generate plugin server signature key and crt
     * - (TODO) generate Raft server signature key and crt
     */
    @Override
    @SneakyThrows
    public void initCryptoSuit() {
        assert keysPath != null;
        // load root ca and private key
        Identity rootCAIdentity = generateSelfSignedRootCert();
        //load plugin server key pair
        Identity pluginServerIdentity = generatePluginServerCertificate(rootCAIdentity);
        //load node key pair
        Identity nodeServerIdentity = generateNodeCertificate(rootCAIdentity);

        this.walletBundle = Map.of(
                IdentityType.ROOT_CA, rootCAIdentity,
                IdentityType.PLUGIN, pluginServerIdentity,
                IdentityType.NODE, nodeServerIdentity
        );
    }
    @SneakyThrows
    private KeyPair generateKeyPairInternal() {
        // generate one EC key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, "BC");

        ECGenParameterSpec ecSpec = new ECGenParameterSpec(EC_NAME);
        keyPairGenerator.initialize(ecSpec, new SecureRandom());

        return keyPairGenerator.generateKeyPair();
    }

    @SneakyThrows
    public Identity generateNodeCertificate(Identity rootCABundle) {
        Path privateKeySavePath = Paths.get( keysPath,"node_private.key");
        Path publicCertSavePath = Paths.get(keysPath, "node_cert.crt");

        if (Files.exists(privateKeySavePath) && Files.exists(publicCertSavePath)) {
            return readCertificateBundle("node_cert.crt", "node_private.key");
        }
        KeyPair keyPair = this.generateKeyPairInternal();
        // save the key pair private key to one hex string
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        X509Certificate certificate = generateCertificate("CN=Node Server", keyPair,
                rootCABundle.getCertificate(), rootCABundle.getPrivateKey(), BigInteger.valueOf(System.currentTimeMillis()));
        // save the private key to one file and create it if not exists
        Files.createDirectories(privateKeySavePath.getParent());
        Files.writeString(privateKeySavePath, convertBase64ToPEM(privateKeyBase64, KeyFileFormat.PRIVATE_KEY), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.writeString(publicCertSavePath, convertBase64ToPEM(Base64.getEncoder().encodeToString(certificate.getEncoded()),
                KeyFileFormat.CERTIFICATE), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        return new Identity(certificate, keyPair.getPrivate());
    }
    // generate one self-signed certificate and private key

    @SneakyThrows
    private Identity generatePluginServerCertificate(Identity rootCABundle) {
        Path pluginServerPrivateKeySavePath = Paths.get(keysPath, "plugin_server_private.key");
        Path pluginServerCertPath = Paths.get(keysPath, "plugin_server_cert.crt");

        if (Files.exists(pluginServerPrivateKeySavePath) && Files.exists(pluginServerCertPath)) {
            return readCertificateBundle("plugin_server_cert.crt", "plugin_server_private.key");
        }
        KeyPair certKeyPair = this.generateKeyPairInternal();
        X509Certificate certificate = generateCertificate("CN=Plugin Server", certKeyPair,
                rootCABundle.getCertificate(), rootCABundle.getPrivateKey(), BigInteger.valueOf(System.currentTimeMillis()));
        // save the certificate to one file
        Files.createDirectories(pluginServerCertPath.getParent());
        Files.writeString(pluginServerCertPath,
                convertBase64ToPEM(Base64.getEncoder().encodeToString(certificate.getEncoded()), KeyFileFormat.CERTIFICATE),
                StandardOpenOption.CREATE);
        // save the private key to one file
        Files.writeString(pluginServerPrivateKeySavePath,
                convertBase64ToPEM(Base64.getEncoder().encodeToString(certKeyPair.getPrivate().getEncoded()), KeyFileFormat.PRIVATE_KEY),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        return new Identity(certificate, certKeyPair.getPrivate());
    }

    @SneakyThrows
    private Identity readCertificateBundle(String certFileName, String keyFileName) {
        // load the root CA certificate and private key
        byte[] privateKeyBytes = Base64.getDecoder().decode(
                Files.readString(Paths.get(keysPath, keyFileName))
                        .replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "")
                        .replaceAll("\\s", "")
        );
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        X509Certificate rootCertificate = readCertificatePEM(Files.readString(Paths.get(keysPath, certFileName)));
        return new Identity(rootCertificate, privateKey);
    }

    @Override
    public X509Certificate readCertificatePEM(String certPEM) {
        byte[] certificateBytes = Base64.getDecoder()
                .decode(certPEM
                        .replaceAll("-----(BEGIN|END) CERTIFICATE-----", "")
                        .replaceAll("\\s", ""));
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(certificateBytes));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read certificate from PEM", e);
        }
    }

    @Override
    @SneakyThrows
    public Identity newClientIdentity() {
        // Node server will issue one identity to the client
        KeyPair keyPair = this.generateKeyPairInternal();
        // save the key pair private key to one hex string
        X509Certificate certificate = generateCertificate("CN=Client", keyPair,
                this.walletBundle.get(IdentityType.NODE).getCertificate(),  this.walletBundle.get(IdentityType.NODE).getPrivateKey(), BigInteger.valueOf(System.currentTimeMillis()));
        return new Identity(certificate, keyPair.getPrivate(), "did:fwe:client"+ UUID.randomUUID());
    }

    @SneakyThrows
    private Identity generateSelfSignedRootCert() {
        // only generate the root CA once
        Path rootCAPath = Paths.get( keysPath,"root_ca.crt");
        Path rootCAKeyPath = Paths.get(keysPath, "root_ca.key");
        if (Files.exists(rootCAPath) && Files.exists(rootCAKeyPath)) {
           return readCertificateBundle("root_ca.crt", "root_ca.key");
        }
        KeyPair keyPair = this.generateKeyPairInternal();
        // save the key pair private key to one hex string
        X509Certificate rootCertificate = generateCertificate("CN=Root CA", keyPair, null, keyPair.getPrivate(), BigInteger.ONE);

        Files.createDirectories(rootCAPath.getParent());
        // save the private key to one file and create it if not exists
        Files.writeString(rootCAKeyPath,
                convertBase64ToPEM(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()), KeyFileFormat.PRIVATE_KEY),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        // generate one self-signed certificate and save it as PEM format to one file
        Files.writeString(rootCAPath,
                convertBase64ToPEM(Base64.getEncoder().encodeToString(rootCertificate.getEncoded()),KeyFileFormat.CERTIFICATE),
                StandardOpenOption.CREATE);
        return new Identity(rootCertificate, keyPair.getPrivate());
    }

    // convert one Base64 string to one PEM format string
    private String convertBase64ToPEM(String base64String, KeyFileFormat type) {
        String pemFormatted = base64String.replaceAll("(.{64})", "$1\n");
        return String.format("-----BEGIN %s-----\n%s\n-----END %s-----\n", type.getDescription(), pemFormatted, type.getDescription());
    }

    private X509Certificate generateCertificate(String dn, KeyPair keyPair, X509Certificate issuerCert, PrivateKey issuerPrivateKey, BigInteger serialNumber) throws Exception {
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + ONE_YEAR);

        X500Name issuerDN = issuerCert != null ? new X500Name(issuerCert.getSubjectX500Principal().getName()) : new X500Name(dn);
        X500Name subjectDN = new X500Name(dn);
        PublicKey publicKey = keyPair.getPublic();

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerDN,
                serialNumber,
                notBefore,
                notAfter,
                subjectDN,
                publicKey
        );
        GeneralName dnsName = new GeneralName(GeneralName.dNSName, "localhost");
        GeneralNames subjectAltNames = new GeneralNames(dnsName);
        certBuilder.addExtension(Extension.subjectAlternativeName, false,
                subjectAltNames);
        // generate the certificate
        return new JcaX509CertificateConverter()
                .getCertificate(certBuilder.build(new JcaContentSignerBuilder(SHA_256_WITH_ECDSA)
                        .build(issuerPrivateKey)));

    }

    @Override
    public void afterPropertiesSet() {
        initCryptoSuit();
        log.info("Crypto suit initialized");
    }

    @Override
    public Map<IdentityType, Identity> getWalletBundle() {
        return walletBundle;
    }
}
