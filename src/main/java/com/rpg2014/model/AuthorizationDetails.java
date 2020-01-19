package com.rpg2014.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter
public class AuthorizationDetails {

    @Setter
    private long numberOfStarts;

    private boolean allowedToStartServer;

    @NonNull
    private String username;
}
