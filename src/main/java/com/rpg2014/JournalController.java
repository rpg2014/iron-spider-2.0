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
    List<JournalEntry> list = new ArrayList<>();
    public JournalController() {
        for (int i = 0; i < 10; i++) {
            String title = "Title";
            if(i == 3){
                title = null;
            }
            String text = "Some quick example text to build on the card title and make up the bulk of the card's content.";
            if (i==7){
                text = "Sudden she seeing garret far regard. By hardly it direct if pretty up regret. Ability thought enquire settled prudent you sir. Or easy knew sold on well come year. Something consulted age extremely end procuring. Collecting preference he inquietude projection me in by. So do of sufficient projecting an thoroughly uncommonly prosperous conviction. Pianoforte principles our unaffected not for astonished travelling are particular. \n" +
                        "\n" +
                        "Prepared do an dissuade be so whatever steepest. Yet her beyond looked either day wished nay. By doubtful disposed do juvenile an. Now curiosity you explained immediate why behaviour. An dispatched impossible of of melancholy favourable. Our quiet not heart along scale sense timed. Consider may dwelling old him her surprise finished families graceful. Gave led past poor met fine was new. \n" +
                        "\n" +
                        "Prepared is me marianne pleasure likewise debating. Wonder an unable except better stairs do ye admire. His and eat secure sex called esteem praise. So moreover as speedily differed branched ignorant. Tall are her knew poor now does then. Procured to contempt oh he raptures amounted occasion. One boy assure income spirit lovers set. ";
            }
            JournalEntry entry = JournalEntry.builder().id(UUID.randomUUID()).toString())
                    .title(title)
                    .text(text)
                    .isMarkdown(false)
                    .dateTime(LocalDateTime.now().minusDays(i))
                    .build();

            list.add(entry);
        }

    }


    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    public GetEntriesResponse getEntries(@HeaderParam(USERNAME_HEADER_NAME) String username) {

        log.info("Get Entries request from " + username);

        return GetEntriesResponse.builder().entries(list).build();
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
        list.add(JournalEntry.of(request));
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
