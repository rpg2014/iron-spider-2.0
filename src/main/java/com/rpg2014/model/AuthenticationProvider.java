package com.rpg2014.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rpg2014.wrappers.DynamoDB.AuthDynamoWrapper;

import java.util.concurrent.TimeUnit;

public class AuthenticationProvider {
    Cache<String, AuthorizationDetails> trueCache = CacheBuilder.newBuilder().maximumSize(200).build();
    Cache<String, AuthorizationDetails> falseCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    AuthDynamoWrapper dynamoWrapper = AuthDynamoWrapper.getInstance();

    public boolean hasAccess(final String username) {
        AuthorizationDetails authDetails = trueCache.getIfPresent(username);
        if (null != authDetails) {
            dynamoWrapper.startedServer(authDetails);
            return authDetails.isAllowedToStartServer();
        }
        authDetails = falseCache.getIfPresent(username);
        if (null != authDetails) {
            return authDetails.isAllowedToStartServer();
        }
        authDetails = dynamoWrapper.isAuthorized(username);
        put(username, authDetails);
        return authDetails.isAllowedToStartServer();
    }

    private void put(final String username, final AuthorizationDetails authDetails) {
        if (authDetails.isAllowedToStartServer()) {
            dynamoWrapper.startedServer(authDetails);
            trueCache.put(username, authDetails);
        } else {
            falseCache.put(username, authDetails);
        }
    }
}
