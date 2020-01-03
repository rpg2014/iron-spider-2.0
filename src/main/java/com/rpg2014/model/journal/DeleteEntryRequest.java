package com.rpg2014.model.journal;

import lombok.Data;
import lombok.NonNull;

import javax.ws.rs.PathParam;

@Data
public class DeleteEntryRequest {
    @NonNull
    @PathParam("entryId")
    String entryId;
    @NonNull
    String username;
}
