package CertificateAuthority;


import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.x509.X509V1CertificateGenerator;

/*
import static serverchatclient.EncryptionUtil.ALGORITHM;
import static serverchatclient.EncryptionUtil.PRIVATE_KEY_FILE;
import static serverchatclient.EncryptionUtil.PUBLIC_KEY_FILE;
import static serverchatclient.EncryptionUtil.areKeysPresent;
import static serverchatclient.EncryptionUtil.decrypt;
import static serverchatclient.EncryptionUtil.encrypt;
import static serverchatclient.EncryptionUtil.generateKey;
*/

/**
 * Basic X.509 V1 Certificate creation.
 */

public class X509V1CreateExample {

    public static X509Certificate generateV1Certificate(PublicKey PK, PrivateKey PVK, String Name)
            throws InvalidKeyException, SignatureException {
        // generate the certificate
        Security.addProvider(new BouncyCastleProvider());
        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X509Principal("CN=SERVER"));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 50000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 50000));
        certGen.setSubjectDN(new X509Name("CN=" + Name));
        certGen.setPublicKey(PK);

// i get error here
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        return certGen.generateX509Certificate(PVK);
    }

    public static void main(String[] args) throws Exception {
        // create the keys
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        KeyPair pair2 = keyGen.generateKeyPair();

        // generate the certificate
        Security.addProvider(new BouncyCastleProvider());
        X509Certificate cert = generateV1Certificate(pair2.getPublic(), pair.getPrivate(), "eid");
        
        
        cert.verify(pair.getPublic());
        // show some basic validation
        cert.checkValidity(new Date());

//        cert.verify(cert.getPublicKey());

        // System.out.println("valid certificate generated");*/
    }
}
