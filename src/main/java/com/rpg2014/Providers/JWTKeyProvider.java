package com.rpg2014.Providers;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ext.Provider;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Provider
public class JWTKeyProvider implements com.auth0.jwt.interfaces.RSAKeyProvider {

    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");

    JwkProvider keyProvider = new JwkProviderBuilder("https://cognito-idp.us-east-1.amazonaws.com/"+ USER_POOL_ID +"/.well-known/jwks.json").cached(true).build();
    final RSAPrivateKey privateKey = null;
    final String privateKeyId = "notUsed";

    @Override
    public RSAPublicKey getPublicKeyById(String s) {
        try {
            if(keyProvider.get(s).getType() == "RSA" ){
                RSAPublicKey key = (RSAPublicKey) keyProvider.get(s).getPublicKey();
                return key;
            }
        } catch (JwkException e) {
            throw new InternalServerErrorException("Unable to get jwt key");
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
