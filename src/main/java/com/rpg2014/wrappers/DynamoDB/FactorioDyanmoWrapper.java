package com.rpg2014.wrappers.DynamoDB;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class FactorioDyanmoWrapper  extends ServerDynamoWrapper  {
    private static final String SERVER_RUNNING = "serverRunning";
    private static final String INSTANCE_ID = "instanceId";
    private static final String AMI_ID = "amiId";
    private static final String TABLE_NAME = "factorioServerDetails";

    private static FactorioDyanmoWrapper ourInstance = new FactorioDyanmoWrapper(
            DynamoDbClient.builder().region(Region.US_EAST_2).build()
    );
    private FactorioDyanmoWrapper(DynamoDbClient client) {
        super(TABLE_NAME, client);

    }

    public static FactorioDyanmoWrapper getInstance() {
        return ourInstance;
    }

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

    public void setAmiId(final String amiId) {
        setItem(AMI_ID, amiId);
    }
    public String getAmiID() {
        return getItem(AMI_ID).get(VALUE).s();
    }
}
