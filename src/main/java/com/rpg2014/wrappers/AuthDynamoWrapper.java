package com.rpg2014.wrappers;

import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import javax.ws.rs.InternalServerErrorException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuthDynamoWrapper {

    private static final String USER_NAME = "username";
    private static final String VALUE_KEY = "hasAccess";
    private static final String TABLE_NAME = "MinecraftAuthZTable";
    private static AuthDynamoWrapper ourInstance = new AuthDynamoWrapper();

    public static AuthDynamoWrapper getInstance() {
        return ourInstance;
    }
    private DynamoDbClient client;


    private AuthDynamoWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public boolean isAuthorized(final String userName) {
        Map<String, AttributeValue> itemMap =  getItem(userName);
        if(null == itemMap || itemMap.isEmpty()){
            createEntryForUser(userName);
            return false;
        }
        return itemMap.get(VALUE_KEY).bool();
    }




    private Map<String, AttributeValue> getItem(final String username) {
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(USER_NAME, AttributeValue.builder().s(username).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName(TABLE_NAME).build();
        try{
            return client.getItem(request).item();
        }catch(ResourceNotFoundException e){
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }catch(DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private void createEntryForUser(final String username){
        Map<String,AttributeValue> item = new HashMap<>();
        item.put(USER_NAME, AttributeValue.builder().s(username).build());
        item.put(VALUE_KEY, AttributeValue.builder().bool(Boolean.FALSE).build());

        PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();

        try{
            client.putItem(request);
        }catch(ResourceNotFoundException e){
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }catch(DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
