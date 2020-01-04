package com.rpg2014.model.journal;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Journal {
    static Map<String,Journal> journalMap;
    @Getter
    List<JournalEntry> entryList;

    public Journal(){
        Journal j = new Journal();
        this.entryList = new ArrayList<JournalEntry>();

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
            JournalEntry entry = JournalEntry.builder().id(UUID.randomUUID().toString())
                    .title(title)
                    .text(text)
                    .isMarkdown(false)
                    .dateTime(LocalDateTime.now().minusDays(i))
                    .build();

            entryList.add(entry);
        }
    }

    static public Journal getJournalForUser(final String username) {
        if (journalMap.containsKey(username)) {
            return journalMap.get(username);
        }else {
            journalMap.put(username, new Journal());
            return journalMap.get(username);
        }
    }
}
