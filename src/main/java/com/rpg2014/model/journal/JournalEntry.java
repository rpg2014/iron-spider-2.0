package com.rpg2014.model.journal;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.var;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Data
@Builder
public class JournalEntry {

    private static final String ID = "id";
    private static final String TEXT = "text";
    private static final String IS_MARKDOWN = "isMarkdown";
    private static final String TITLE = "title";
    private static final String DATE_TIME = "dateTime";
    @NonNull
    String id;

    @NonNull
    String text;

    boolean isMarkdown;

    String title;

    @NonNull
    LocalDateTime dateTime;

    public static JournalEntry from(CreateEntryRequest request) {
        LocalDateTime date = LocalDateTime.now();
        if(request.getDateTime()!=null){
            date = request.getDateTime();
        }
        return JournalEntry.builder().title(request.getTitle())
                .text(request.getText())
                .isMarkdown(request.isMarkdown())
                .dateTime(date)
                .id(UUID.randomUUID().toString()).build();
    }

    public static JournalEntry from(EditEntryRequest request) {
        LocalDateTime date = LocalDateTime.now();
        if(request.getDateTime()!=null){
            date = request.getDateTime();
        }
        return JournalEntry.builder().title(request.getTitle())
                .text(request.getText())
                .isMarkdown(request.isMarkdown())
                .dateTime(date)
                .id(request.getId()).build();
    }
    public static JournalEntry from(AttributeValue attributeValue) {
        var attributeMap = attributeValue.m();
        String title = attributeMap.get(TITLE).s();
        String id = attributeMap.get(ID).s();
        LocalDateTime date = LocalDateTime.parse(attributeMap.get(DATE_TIME).s());
        boolean isMarkdown = attributeMap.get(IS_MARKDOWN).bool();
        String text = attributeMap.get(TEXT).s();
        return JournalEntry.builder().id(id).text(text).title(title).dateTime(date).isMarkdown(isMarkdown).build();
    }

    public AttributeValue toAttributeValue() {
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(ID, AttributeValue.builder().s(this.getId()).build());
        valueMap.put(TITLE, AttributeValue.builder().s(this.getTitle()).build());
        valueMap.put(TEXT, AttributeValue.builder().s(this.getText()).build()   );
        valueMap.put(IS_MARKDOWN, AttributeValue.builder().bool(this.isMarkdown()).build());
        valueMap.put(DATE_TIME, AttributeValue.builder().s(this.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).build());

        return AttributeValue.builder().m(valueMap).build();
    }
}
