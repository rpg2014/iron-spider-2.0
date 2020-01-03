package com.rpg2014.filters.RequiresLogin;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.rpg2014.Providers.JWTKeyProvider;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Priority(Priorities.AUTHENTICATION)
public class RequiresLoginFilter implements ContainerRequestFilter {

    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");
    private static final String ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/" + USER_POOL_ID;
    private static final String AUTH_HEADER_NAME = "spider-access-token";
    public static final String USERNAME_HEADER_NAME = "spider-username";

    Algorithm algorithm = Algorithm.RSA256(new JWTKeyProvider());
    JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).withClaim("token_use", "access").acceptLeeway(1).build();

    public RequiresLoginFilter() throws MalformedURLException {
    }

    @Override public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authToken = containerRequestContext.getHeaderString(AUTH_HEADER_NAME);
        String existingUserNameHeader = containerRequestContext.getHeaderString(USERNAME_HEADER_NAME);
        if (null != existingUserNameHeader) {
            throw new NotAuthorizedException("Faking username header, go away");
        }
        if (authToken == null) {
            throw new NotAuthorizedException("No Auth Header");
        }
        String username = "";
        try {
            username = verifyToken(authToken);
        }catch (Exception e){
            e.printStackTrace();
            throw new NotAuthorizedException("Unable to verify access token");
        }
        containerRequestContext.getHeaders().add(USERNAME_HEADER_NAME, username);
    }

    private String verifyToken(final String authToken){
        DecodedJWT key = verifier.verify(authToken);
        Claim claim = key.getClaim("username");
        return claim.asString();
    }
}
