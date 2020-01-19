package com.rpg2014.model.journal;

import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@Builder
public class GetEntriesResponse {
    @NonNull
    List<JournalEntry> entries;
}
