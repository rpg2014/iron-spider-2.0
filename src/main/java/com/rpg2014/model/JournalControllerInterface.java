package com.rpg2014.model;

import com.rpg2014.model.journal.CreateEntryRequest;
import com.rpg2014.model.journal.CreateEntryResponse;
import com.rpg2014.model.journal.DeleteEntryResponse;
import com.rpg2014.model.journal.EditEntryRequest;
import com.rpg2014.model.journal.EditEntryResponse;
import com.rpg2014.model.journal.GetEntriesResponse;

public interface JournalControllerInterface {

    GetEntriesResponse getEntries(String username);

    CreateEntryResponse createEntry(CreateEntryRequest request, String username);

    DeleteEntryResponse deleteEntry(String entryId, String username);

    EditEntryResponse editEntry(EditEntryRequest request, String id, String username);

}
