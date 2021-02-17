package com.rpg2014.wrappers.DynamoDB;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class JournalKeyDDBWrapper {
    private static JournalKeyDDBWrapper ourInstance = new JournalKeyDDBWrapper();
    DynamoDbClient client;

    private JournalKeyDDBWrapper() {
        this.client = DynamoDbClient.builder().region(US_EAST_1).build();
    }

    public static JournalKeyDDBWrapper getInstance() {
        return ourInstance;
    }


}
