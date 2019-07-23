package com.rpg2014.wrappers;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.ws.rs.InternalServerErrorException;
import java.util.HashMap;
import java.util.Map;

public class MinecraftDynamoWrapper {
    private static MinecraftDynamoWrapper ourInstance = new MinecraftDynamoWrapper();

    public static MinecraftDynamoWrapper getInstance() {
        return ourInstance;
    }

    private static final String ITEM_ID = "itemId";
    private static final String AMI_ID = "amiId";
    private static final String INSTANCE_ID = "instanceId";
    private static final String SERVER_RUNNING = "serverRunning";
    private static final String SNAPSHOT_ID = "snapshotId";
    private static final String VALUE = "value";
    private static HashMap<String, AttributeValue> key;
    private static HashMap<String, AttributeValueUpdate> updatedValues;
    private DynamoDbClient client;

    private MinecraftDynamoWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public boolean isServerRunning() {
        try {
            key = new HashMap<>();
            return getItem(key, SERVER_RUNNING).get(VALUE).bool();
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to get isServerRunning");
        }
    }

    public void setServerRunning() {
        try {
            key = new HashMap<>();
            updatedValues = new HashMap<>();
            setItem(key, updatedValues, SERVER_RUNNING, true);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setServerRunning");
        }
    }

    public void setServerStopped() {
        try {
            key = new HashMap<>();
            updatedValues = new HashMap<>();
            setItem(key, updatedValues, SERVER_RUNNING, false);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setServerStopped");
        }
    }

    public String getSnapshotId() {
        try {
            key = new HashMap<>();
            return getItem(key, SNAPSHOT_ID).get(VALUE).toString();
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to getSnapshotId");
        }
    }

    public void setSnapshotId(final String snapshotId) {
        try {
            key = new HashMap<>();
            updatedValues = new HashMap<>();
            setItem(key, updatedValues, SNAPSHOT_ID, snapshotId);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setSnapshotId");
        }
    }

    public String getInstanceId() {
        try {
            key = new HashMap<>();
            return getItem(key, INSTANCE_ID).get(VALUE).toString();
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to getInstanceId");
        }
    }


    public void setInstanceId(final String instanceId) {
        try {
            key = new HashMap<>();
            updatedValues = new HashMap<>();
            setItem(key, updatedValues, INSTANCE_ID, instanceId);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setInstanceId");
        }
    }

    public String getAmiID() {
        try {
            key = new HashMap<>();
            return getItem(key, AMI_ID).get(VALUE).toString();
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to getAmiId");
        }
    }

    public void setAmiId(final String amiId) {
        try {
            key = new HashMap<>();
            updatedValues = new HashMap<>();
            setItem(key, updatedValues, AMI_ID, amiId);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setAmiId");
        }
    }

    private Map<String, AttributeValue> getItem(HashMap<String, AttributeValue> map, final String str) {
        map.put(ITEM_ID, AttributeValue.builder().s(str).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName("spencerIsDumb").build();
        return client.getItem(request).item();
    }

    private void setItem(HashMap<String, AttributeValue> itemKey, HashMap<String, AttributeValueUpdate> updatedValues,
                         final String str, boolean val) {
        itemKey.put(ITEM_ID, AttributeValue.builder().s(str).build());
        updatedValues.put(VALUE, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().bool(val).build())
                .action(AttributeAction.PUT)
                .build());
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("spencerIsDumb")
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();
        try {
            client.updateItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.println("failed on client.updateItem in setItem() (boolean)");
        }
    }

    private void setItem(HashMap<String, AttributeValue> itemKey, HashMap<String, AttributeValueUpdate> updatedValues,
                         final String str, String val) {
        itemKey.put(ITEM_ID, AttributeValue.builder().s(str).build());
        updatedValues.put(VALUE, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(val).build())
                .action(AttributeAction.PUT)
                .build());
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName("spencerIsDumb")
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();
        try {
            client.updateItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.println("failed on client.updateItem in setItem() (string)");
        }
    }
}