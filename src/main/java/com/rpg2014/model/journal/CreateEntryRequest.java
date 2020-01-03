package com.rpg2014.model.journal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class CreateEntryRequest {
    @NonNull
    String text;
    String title;
    @NonNull LocalDateTime dateTime;
    boolean isMarkdown;

    @JsonCreator
    public CreateEntryRequest(@NonNull @JsonProperty("text") String text,
                              @JsonProperty("title") String title,
                              @NonNull @JsonProperty("dateTime") LocalDateTime dateTime,
                              @JsonProperty("isMarkdown") boolean isMarkdown) {
        this.text = text;
        this.title = title;
        this.dateTime = dateTime;
        this.isMarkdown = isMarkdown;
    }
}
