package com.rpg2014;

import com.rpg2014.filters.RequiresLogin.RequiresLogin;
import com.rpg2014.model.JournalControllerInterface;
import com.rpg2014.model.journal.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        log.info("Username="+username);
        Journal journal = Journal.getJournalForUser(username);
        journal.getEntryList().add(JournalEntry.of(request));
        return CreateEntryResponse.builder().success(true).build();
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
        log.info("Username="+username);
        List<JournalEntry> list = Journal.getJournalForUser(username).getEntryList();
        boolean success = list.removeIf((journalEntry -> journalEntry.getId().equals(request.getEntryId())));
        return DeleteEntryResponse.builder().success(success).build();
    }

    @Override
    @POST
    @Path("/{entryId}")
    @RequiresLogin
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EditEntryResponse editEntry(EditEntryRequest request,
                                       @HeaderParam(USERNAME_HEADER_NAME) String username) {
        log.info("EditEntryRequest Received");
        log.info(request.toString());
        log.info("Username="+username);

        return EditEntryResponse.builder().success(false).build();
    }
}
