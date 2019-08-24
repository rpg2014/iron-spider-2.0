package com.rpg2014.wrappers;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.ws.rs.InternalServerErrorException;
import java.util.HashMap;
import java.util.Map;

public class MinecraftDynamoWrapper {
    private static MinecraftDynamoWrapper ourInstance = new MinecraftDynamoWrapper();
    private static final String ITEM_ID = "itemId";
    private static final String AMI_ID = "amiId";
    private static final String INSTANCE_ID = "instanceId";
    private static final String SERVER_RUNNING = "serverRunning";
    private static final String SNAPSHOT_ID = "snapshotId";
    private static final String TABLE_NAME = "minecraftServerDetails";
    private static final String VALUE = "value";
    private DynamoDbClient client;

    private MinecraftDynamoWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public static MinecraftDynamoWrapper getInstance() {
        return ourInstance;
    }

    public boolean isServerRunning() { return getItem(SERVER_RUNNING).get(VALUE).bool(); }

    public void setServerRunning() {
        setItem(SERVER_RUNNING, true);
    }

    public void setServerStopped() {
        setItem(SERVER_RUNNING, false);
    }

    public String getSnapshotId() {
        return getItem(SNAPSHOT_ID).get(VALUE).s();
    }

    public void setSnapshotId(final String snapshotId) {
        setItem(SNAPSHOT_ID, snapshotId);
    }

    public String getInstanceId() {
        return getItem(INSTANCE_ID).get(VALUE).s();
    }

    public void setInstanceId(final String instanceId) {
        setItem(INSTANCE_ID, instanceId);
    }

    public String getAmiID() {
        return getItem(AMI_ID).get(VALUE).s();
    }

    public void setAmiId(final String amiId) {
        setItem(AMI_ID, amiId);
    }

    private Map<String, AttributeValue> getItem(final String itemId) {
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(ITEM_ID, AttributeValue.builder().s(itemId).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName(TABLE_NAME).build();
        return client.getItem(request).item();
    }

    private void setItem(final String itemId, boolean val) {
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
            throw new InternalServerErrorException("Failed to get "+ itemId +" from dynamo");
        }
    }

    private void setItem(final String itemId, String val) {
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
            throw new InternalServerErrorException("Failed to get "+itemId +" from dynamo");
        }
    }
}