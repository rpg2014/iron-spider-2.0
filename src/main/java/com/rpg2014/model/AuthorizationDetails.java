package com.rpg2014.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Builder
@Getter
public class AuthorizationDetails {

    @Setter
    @NonNull
    private long numberOfStarts;

    @NonNull
    private boolean allowedToStartServer;

    @NonNull
    private String username;
}
