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

    private DynamoDbClient client;

    private MinecraftDynamoWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }

    public boolean isServerRunning() {
        try {
            HashMap<String, AttributeValue> key = new HashMap<>();
            return getItem(key, SERVER_RUNNING).get(VALUE).bool();
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to get isServerRunning caught a DynamoDbException");
        }
    }

    public void setServerRunning() {
        try {
            HashMap<String, AttributeValue> key = new HashMap<>();
            HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
            setItem(key, updatedValues, SERVER_RUNNING, true);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setServerRunning");
        }
    }

    public void setServerStopped() {
        try {
            HashMap<String, AttributeValue> key = new HashMap<>();
            HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
            setItem(key, updatedValues, SERVER_RUNNING, false);
        } catch (DynamoDbException e) {
            throw new InternalServerErrorException("Failed to setServerStopped");
        }
    }

    /*
        public String getSnapshotId() {
            return table.getItem(ITEM_ID, SNAPSHOT_ID).getString(VALUE);
        }

        public void setSnapshotId(final String snapshotId){
            table.putItem(new Item().with(ITEM_ID, SNAPSHOT_ID).with(VALUE, snapshotId));
        }


        public String getInstanceId() {
            return table.getItem(ITEM_ID, INSTANCE_ID).getString(VALUE);
        }

        public void setInstanceId(final String instanceId) {
            table.putItem(new Item().with(ITEM_ID, INSTANCE_ID).with(VALUE, instanceId));
        }

        public String getAmiID() {
            return table.getItem(ITEM_ID, AMI_ID).getString(VALUE);
        }

        public void setAmiId(final String amiId) {
            table.putItem(new Item().with(ITEM_ID, AMI_ID).with(VALUE, amiId));
        }
    */
    private Map<String, AttributeValue> getItem(HashMap<String, AttributeValue> map, final String str) {
        map.put(ITEM_ID, AttributeValue.builder().s(str).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName("minecraftServerDetails").build();
        return client.getItem(request).item();
    }

    private void setItem(HashMap<String, AttributeValue> itemKey, HashMap<String, AttributeValueUpdate> updatedValues,
                         final String str, boolean val) {
        itemKey.put(ITEM_ID, AttributeValue.builder().s(str).build());
        updatedValues.put(VALUE, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(Boolean.toString(val)).build())
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
            System.err.println("failed on client.updateItem in setItem()");
        }
    }
}