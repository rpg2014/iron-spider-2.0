package com.rpg2014.model.journal;

import com.rpg2014.model.EncryptionResult;
import com.rpg2014.wrappers.EncryptionWrapper;
import com.rpg2014.wrappers.JournalDDBWrapper;
import com.rpg2014.wrappers.JournalKeyDDBWrapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@Slf4j
public class Journal {
    static JournalDDBWrapper journalWrapper = JournalDDBWrapper.getInstance();

    private static final String USERNAME_FIELD = "username";
    private static final String ENTRIES_FIELD = "entries";
    private static final String KEY_FIELD = "key";

    @Getter
    List<JournalEntry> entryList;

    @Getter
    String username;

    static public Journal getJournalForUser(final String username) {
        var journalMap = journalWrapper.getJournalForUser(username);
        if(journalMap == null){
            log.info("Journal entry not found, creating new one for user, {}", username);
            return Journal.builder().username(username).entryList(new ArrayList<>()).build();
        }
        return  Journal.from(journalMap);
    }


    /**
     * do decvryption here 
     * @param journalMap
     * @return
     */
    public static Journal from(@NonNull Map<String, AttributeValue> journalMap) {
        String username = journalMap.get(USERNAME_FIELD).s();
        SdkBytes bytes = journalMap.get(ENTRIES_FIELD).b();
        // not yet implement.  In the future maybe each person gets their own key.
        //String key = journalMap.get(KEY_FIELD).s();
        List<JournalEntry> journalEntryList = EncryptionWrapper.getOurInstance().decryptBytesToJournalList(bytes, username);
        return Journal.builder().username(username).entryList(journalEntryList).build();
    }

    public boolean saveJournal() {
        return journalWrapper.updateJournalForUser(this);
    }

    /**
     * Do Encryption in here.  encrypt both username and the list
     * @return
     */
    public Map<String, AttributeValue> toAttributeValueMap() {
        Map<String, AttributeValue> map = new HashMap<>();
        EncryptionResult result = EncryptionWrapper.getOurInstance().encryptJournalEntries(this.getEntryList(), getUsername());
        map.put(USERNAME_FIELD, AttributeValue.builder().s(this.getUsername()).build()    );
        map.put(ENTRIES_FIELD, AttributeValue.builder().b(SdkBytes.fromByteArray(result.getEncryptedBytes())).build());
        map.put(KEY_FIELD, AttributeValue.builder().s(result.getEncryptedKey()).build());

        return map;
    }

    /**
     * Temp for testing
     */
    public static Journal createTestJournal(){

        List<JournalEntry> entryList = new ArrayList<JournalEntry>();

        for (int i = 0; i < 10; i++) {
            String title = "Title";
            if(i == 3){
                title = null;
            }
            String text = "Some quick example text to build on the card title and make up the bulk of the card's content.";
            if (i==7){
                text = "Sudden she seeing garret far regard. By hardly it direct if pretty up regret. Ability thought enquire settled prudent you sir. Or easy knew sold on well come year. Something consulted age extremely end procuring. Collecting preference he inquietude projection me in by. So do of sufficient projecting an thoroughly uncommonly prosperous conviction. Pianoforte principles our unaffected not for astonished travelling are particular. \n" +
                        "\n" +
                        "Prepared do an dissuade be so whatever steepest. Yet her beyond looked either day wished nay. By doubtful disposed do juvenile an. Now curiosity you explained immediate why behaviour. An dispatched impossible of of melancholy favourable. Our quiet not heart along scale sense timed. Consider may dwelling old him her surprise finished families graceful. Gave led past poor met fine was new. \n" +
                        "\n" +
                        "Prepared is me marianne pleasure likewise debating. Wonder an unable except better stairs do ye admire. His and eat secure sex called esteem praise. So moreover as speedily differed branched ignorant. Tall are her knew poor now does then. Procured to contempt oh he raptures amounted occasion. One boy assure income spirit lovers set. ";
            }
            JournalEntry entry = JournalEntry.builder().id(UUID.randomUUID().toString())
                    .title(title)
                    .text(text)
                    .isMarkdown(false)
                    .dateTime(LocalDateTime.now().minusDays(i))
                    .build();

            entryList.add(entry);
        }
        return Journal.builder().entryList(entryList).username("testUsername").build();
    }

    public boolean removeEntry(final String id) {
        List<JournalEntry> list = this.getEntryList();
        return list.removeIf((journalEntry -> journalEntry.getId().equals(id)));
    }

    public boolean addEntry(JournalEntry entry) {
        return this.getEntryList().add(entry);
    }
}
