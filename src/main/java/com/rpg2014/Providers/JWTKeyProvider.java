package com.rpg2014.Providers;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ext.Provider;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Provider
public class JWTKeyProvider implements com.auth0.jwt.interfaces.RSAKeyProvider {

    final RSAPrivateKey privateKey = null;
    final String privateKeyId = "notUsed";
    private final String USER_POOL_ID = System.getenv("USER_POOL_ID");
    private final URL uri = new URL("https://cognito-idp.us-east-1.amazonaws.com/" + USER_POOL_ID + "/.well-known/jwks.json");
    JwkProvider keyProvider = new JwkProviderBuilder(uri).cached(true).build();

    public JWTKeyProvider() throws MalformedURLException {
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        try {
            Jwk key = keyProvider.get(keyId);
            if (key.getType().equals("RSA")) {
                return (RSAPublicKey) key.getPublicKey();
            }
        } catch (JwkException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
        return null;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public String getPrivateKeyId() {
        return null;
    }
}
