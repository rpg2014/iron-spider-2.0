package com.rpg2014.wrappers.DynamoDB;

import com.rpg2014.model.AuthorizationDetails;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import javax.ws.rs.InternalServerErrorException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuthDynamoWrapper {

    private static final String USER_NAME = "username";
    private static final String HAS_ACCESS_VALUE_KEY = "hasAccess";
    private static final String NUM_OF_STARTS_VALUE_KEY = "numberOfStarts";
    private static final String TABLE_NAME = "MinecraftAuthZTable";
    private static AuthDynamoWrapper ourInstance = new AuthDynamoWrapper();
    private DynamoDbClient client;

    private AuthDynamoWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public static AuthDynamoWrapper getInstance() {
        return ourInstance;
    }

    public AuthorizationDetails isAuthorized(final String userName) {
        Map<String, AttributeValue> itemMap = getItem(userName);
        if (null == itemMap || itemMap.isEmpty()) {
            AuthorizationDetails authDetails = createEntryForUser(userName);
            return authDetails;
        }
        log.info("User " + userName + ", Has access: " + itemMap.get(HAS_ACCESS_VALUE_KEY).bool());
        return createAuthDetails(itemMap);
    }


    private Map<String, AttributeValue> getItem(final String username) {
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(USER_NAME, AttributeValue.builder().s(username).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName(TABLE_NAME).build();
        try {
            return client.getItem(request).item();
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new InternalServerErrorException("Unable to get item");
    }

    private AuthorizationDetails createEntryForUser(final String username) {
        log.info("Creating dynamo entry for user: " + username);
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_NAME, AttributeValue.builder().s(username).build());
        item.put(HAS_ACCESS_VALUE_KEY, AttributeValue.builder().bool(Boolean.FALSE).build());
        item.put(NUM_OF_STARTS_VALUE_KEY, AttributeValue.builder().n("0").build());

        PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();

        try {
            PutItemResponse response = client.putItem(request);
            return AuthorizationDetails.builder().username(username).allowedToStartServer(false).numberOfStarts(0).build();
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private AuthorizationDetails createAuthDetails(Map<String, AttributeValue> itemMap) {
        return AuthorizationDetails.builder()
                .username(itemMap.get(USER_NAME).s())
                .allowedToStartServer(itemMap.get(HAS_ACCESS_VALUE_KEY).bool())
                .numberOfStarts(Long.valueOf(itemMap.get(NUM_OF_STARTS_VALUE_KEY).n()))
                .build();
    }

    public void startedServer(AuthorizationDetails authDetails) {
        authDetails.setNumberOfStarts(authDetails.getNumberOfStarts() + 1);
        log.info("Updating dynamo entry for user: {}, number of server starts = {}", authDetails.getUsername(), authDetails.getNumberOfStarts());

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_NAME, AttributeValue.builder().s(authDetails.getUsername()).build());
        item.put(HAS_ACCESS_VALUE_KEY, AttributeValue.builder().bool(authDetails.isAllowedToStartServer()).build());
        item.put(NUM_OF_STARTS_VALUE_KEY, AttributeValue.builder().n(Long.toString(authDetails.getNumberOfStarts())).build());

        PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();

        try {
            PutItemResponse response = client.putItem(request);
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
