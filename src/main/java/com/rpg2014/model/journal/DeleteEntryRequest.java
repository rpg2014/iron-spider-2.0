package com.rpg2014.model.journal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import java.time.LocalDateTime;

@Data
public class DeleteEntryRequest {
    @NonNull
    @PathParam("entryId")
    @JsonProperty("id")
    String entryId;
    @JsonCreator
    public DeleteEntryRequest(@NonNull @JsonProperty("id") String entryId) {
        this.entryId = entryId;
    }
}
