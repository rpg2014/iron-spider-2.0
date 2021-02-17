package com.rpg2014.wrappers.DynamoDB;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import javax.ws.rs.InternalServerErrorException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public abstract class ServerDynamoWrapper {

     static final String ITEM_ID = "itemId";
    static final String VALUE = "value";
    private static final String SERVER_RUNNING = "serverRunning";
    private static final String INSTANCE_ID = "instanceId";
    String TABLE_NAME;

     static ServerDynamoWrapper ourInstance= null;

     DynamoDbClient client= null;

    public boolean isServerRunning() {
        return getItem(SERVER_RUNNING).get(VALUE).bool();
    }

    public void setServerRunning() {
        setItem(SERVER_RUNNING, true);
    }

    public void setServerStopped() {
        setItem(SERVER_RUNNING, false);
    }
    public String getInstanceId() {
        return getItem(INSTANCE_ID).get(VALUE).s();
    }

    public void setInstanceId(final String instanceId) {
        setItem(INSTANCE_ID, instanceId);
    }

     Map<String, AttributeValue> getItem(final String itemId) {
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(ITEM_ID, AttributeValue.builder().s(itemId).build());

         GetItemRequest request = GetItemRequest.builder().key(map).tableName(TABLE_NAME).build();
        return client.getItem(request).item();
    }

     void setItem(final String itemId, boolean val) {
        HashMap<String, AttributeValue> itemKey = new HashMap<>();
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        itemKey.put(ITEM_ID, AttributeValue.builder().s(itemId).build());
        updatedValues.put(VALUE, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().bool(val).build())
                .action(AttributeAction.PUT)
                .build());
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();
        try {
            client.updateItem(request);
        } catch (ResourceNotFoundException e) {
            throw new InternalServerErrorException("failed on client.updateItem in setItem() (boolean)");
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to get " + itemId + " from dynamo");
        }
    }

    void setItem(final String itemId, String val) {
        HashMap<String, AttributeValue> itemKey = new HashMap<>();
        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        itemKey.put(ITEM_ID, AttributeValue.builder().s(itemId).build());
        updatedValues.put(VALUE, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(val).build())
                .action(AttributeAction.PUT)
                .build());
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();
        try {
            client.updateItem(request);
        } catch (ResourceNotFoundException e) {
            throw new InternalServerErrorException("failed on client.updateItem in setItem() (string)");
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to get " + itemId + " from dynamo");
        }
    }
}
