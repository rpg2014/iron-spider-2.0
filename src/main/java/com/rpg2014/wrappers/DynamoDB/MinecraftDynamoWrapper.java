package com.rpg2014.wrappers.DynamoDB;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class MinecraftDynamoWrapper extends ServerDynamoWrapper {
    private static final String AMI_ID = "amiId";


    private static final String SNAPSHOT_ID = "snapshotId";
    private static final String TABLE_NAME = "minecraftServerDetails";
    private static final String VALUE = "value";
    private static MinecraftDynamoWrapper ourInstance = new MinecraftDynamoWrapper(DynamoDbClient.builder().region(Region.US_EAST_1).build());
//    private DynamoDbClient client =  ;;

    private MinecraftDynamoWrapper(DynamoDbClient client) {
        super(TABLE_NAME, client);

    }

    public static MinecraftDynamoWrapper getInstance() {
        return ourInstance;
    }



    public String getSnapshotId() {
        return getItem(SNAPSHOT_ID).get(VALUE).s();
    }

    public void setSnapshotId(final String snapshotId) {
        setItem(SNAPSHOT_ID, snapshotId);
    }



    public String getAmiID() {
        return getItem(AMI_ID).get(VALUE).s();
    }

    public void setAmiId(final String amiId) {
        setItem(AMI_ID, amiId);
    }


}