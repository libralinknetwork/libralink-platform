package io.libralink.platform.scripts;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Base64;

public class JWKGeneratePrivateKey {

    public static void main(String args[]) throws Exception {

        // Generate a secret key for HS256 using MacProvider (which generates HMAC keys)
        Key key = MacProvider.generateKey(SignatureAlgorithm.HS256);
        String jwk = Base64.getEncoder().encodeToString(key.getEncoded());

        // Print the JWK
        System.out.println(jwk);
    }
}
