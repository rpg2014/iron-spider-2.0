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

    JwkProvider keyProvider = new JwkProviderBuilder("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_mX9fI3lzt/.well-known/jwks.json").cached(true).build();
    final RSAPrivateKey privateKey = null;
    final String privateKeyId = "notUsed";

    @Override
    public RSAPublicKey getPublicKeyById(String s) {

        try {
            System.out.println(keyProvider.get(s).getType());
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
