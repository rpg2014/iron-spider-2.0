package com.rpg2014.wrappers;

import com.rpg2014.model.journal.Journal;
import com.rpg2014.model.journal.JournalEntry;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.ws.rs.InternalServerErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JournalDDBWrapper {
    private static JournalDDBWrapper ourInstance = new JournalDDBWrapper();
    private static final String USERNAME_FIELD = "username";
    private static final String ENTRIES_FIELD = "entries";
    private static final String KEY_FIELD = "key";
    private static final String TABLE_NAME = "journalTable";


    private DynamoDbClient client;
    private JournalDDBWrapper() {
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }
    public static JournalDDBWrapper getInstance() {
        return ourInstance;
    }

    public Map<String, AttributeValue> getJournalForUser(final String username) {
        return getItem(username);

    }

    public boolean updateJournalForUser(final Journal journal) {
        return putItem(journal.toAttributeValueMap());
    }



    private Map<String, AttributeValue> getItem(final String username) {
        HashMap<String, AttributeValue> map = new HashMap<>();
        map.put(USERNAME_FIELD, AttributeValue.builder().s(username).build());
        GetItemRequest request = GetItemRequest.builder().key(map).tableName(TABLE_NAME).build();

        try {
            return client.getItem(request).item();
        } catch (ResourceNotFoundException e) {
            log.info(e.getMessage());
            return null;
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new InternalServerErrorException("Unable to get item");
    }


    private boolean putItem(Map<String, AttributeValue> itemMap) {

        PutItemRequest request = PutItemRequest.builder().tableName(TABLE_NAME).item(itemMap).build();

        try {
            PutItemResponse response = client.putItem(request);
            return true;
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

