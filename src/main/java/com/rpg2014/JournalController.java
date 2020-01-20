package com.rpg2014;

import com.rpg2014.filters.RequiresLogin.RequiresLogin;
import com.rpg2014.model.JournalControllerInterface;
import com.rpg2014.model.journal.CreateEntryRequest;
import com.rpg2014.model.journal.CreateEntryResponse;
import com.rpg2014.model.journal.DeleteEntryRequest;
import com.rpg2014.model.journal.DeleteEntryResponse;
import com.rpg2014.model.journal.EditEntryRequest;
import com.rpg2014.model.journal.EditEntryResponse;
import com.rpg2014.model.journal.GetEntriesResponse;
import com.rpg2014.model.journal.Journal;
import com.rpg2014.model.journal.JournalEntry;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.rpg2014.filters.RequiresLogin.RequiresLoginFilter.USERNAME_HEADER_NAME;

@Path("/journal")
@Slf4j
public class JournalController implements JournalControllerInterface {


    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    public GetEntriesResponse getEntries(@HeaderParam(USERNAME_HEADER_NAME) String username) {

        log.info("Get Entries request from " + username);
        Journal list = Journal.getJournalForUser(username);
        return GetEntriesResponse.builder().entries(list.getEntryList()).build();
    }

    @Override
    @POST
    @Path("/new")
    @RequiresLogin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateEntryResponse createEntry(CreateEntryRequest request,
                                           @HeaderParam(USERNAME_HEADER_NAME) String username) {
        log.info("CreateEntryRequest Received");
        log.info(request.toString());
        log.info("Username=" + username);
        Journal journal = Journal.getJournalForUser(username);
        boolean success = journal.addEntry(JournalEntry.from(request));
        success = success && journal.saveJournal();
        return CreateEntryResponse.builder().success(success).build();
    }

    @Override
    @DELETE
    @RequiresLogin
    @Path("/{entryId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DeleteEntryResponse deleteEntry(@BeanParam DeleteEntryRequest request,
                                           @HeaderParam(USERNAME_HEADER_NAME) String username) {
        log.info("DeleteEntryRequest Received");
        log.info(request.toString());
        log.info("Username=" + username);
        Journal journal = Journal.getJournalForUser(username);
        boolean success = journal.removeEntry(request.getEntryId());
        success = success && journal.saveJournal();
        return DeleteEntryResponse.builder().success(success).build();
    }

    @Override
    @POST
    @Path("/{entryId}")
    @RequiresLogin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EditEntryResponse editEntry(@BeanParam EditEntryRequest request,
                                       @HeaderParam(USERNAME_HEADER_NAME) String username) {
        log.info("EditEntryRequest Received");
        log.info(request.toString());
        log.info("Username=" + username);
        Journal journal = Journal.getJournalForUser(username);
        //remove old entry
        boolean success = journal.removeEntry(request.getId());
        //add new
        success = success && journal.addEntry(JournalEntry.from(request));
        success = success && journal.saveJournal();
        return EditEntryResponse.builder().success(success).build();
    }
}
