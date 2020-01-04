package com.rpg2014.model.journal;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class JournalEntry {
    @NonNull
    String id;

    @NonNull
    String text;

    boolean isMarkdown;

    String title;

    @NonNull
    LocalDateTime dateTime;

    public static JournalEntry of(CreateEntryRequest request) {
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
}
