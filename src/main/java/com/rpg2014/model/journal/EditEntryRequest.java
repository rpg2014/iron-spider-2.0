package com.rpg2014.model.journal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class EditEntryRequest {
    @NonNull
    String text;
    String title;
    boolean isMarkdown;
    @NonNull LocalDateTime dateTime;

    @JsonCreator
    public EditEntryRequest(@NonNull @JsonProperty("text") String text,
                            @JsonProperty("title") String title,
                            @NonNull @JsonProperty("dateTime") LocalDateTime dateTime,
                            @JsonProperty("isMarkdown") boolean isMarkdown) {
        this.text = text;
        this.title = title;
        this.dateTime = dateTime;
        this.isMarkdown = isMarkdown;
    }
}
