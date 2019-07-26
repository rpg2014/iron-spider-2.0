package com.rpg2014.model;

import com.google.common.cache.*;
import com.rpg2014.wrappers.AuthDynamoWrapper;

import java.util.concurrent.TimeUnit;

public class AuthenticationProvider {
    Cache<String, Boolean> trueCache = CacheBuilder.newBuilder().maximumSize(200).build();
    Cache<String, Boolean> falseCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    AuthDynamoWrapper dynamoWrapper = AuthDynamoWrapper.getInstance();

    public boolean hasAccess(final String username){
        Boolean hasAccess = trueCache.getIfPresent(username);
        if (null != hasAccess) {
            return hasAccess.booleanValue();
        }
        hasAccess = falseCache.getIfPresent(username);
        if(null != hasAccess){
            return hasAccess.booleanValue();
        }
        hasAccess = dynamoWrapper.isAuthorized(username);
        put(username, hasAccess);
        return hasAccess.booleanValue();
    }

    private void put(final String username, final Boolean hasAccess){
        if(hasAccess){
            trueCache.put(username, hasAccess);
        }else {
            falseCache.put(username,hasAccess);
        }
    }



}
