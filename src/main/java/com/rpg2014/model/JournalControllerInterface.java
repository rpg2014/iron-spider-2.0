package com.rpg2014.model;

import com.rpg2014.model.journal.*;

public interface JournalControllerInterface {

    GetEntriesResponse getEntries(String username);

    CreateEntryResponse createEntry(CreateEntryRequest request, String username);

    DeleteEntryResponse deleteEntry(DeleteEntryRequest request, String username);

    EditEntryResponse editEntry(EditEntryRequest request, String username);

}
