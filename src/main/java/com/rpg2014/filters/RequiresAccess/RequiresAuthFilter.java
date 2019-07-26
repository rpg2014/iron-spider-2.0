package com.rpg2014.filters.RequiresAccess;

import com.rpg2014.model.AuthenticationProvider;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

@Priority(Priorities.AUTHORIZATION)
public class RequiresAuthFilter implements ContainerRequestFilter {

    AuthenticationProvider authenticationProvider = new AuthenticationProvider();

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String username = containerRequestContext.getHeaders().get("spider-username").get(0);
        //TODO if in dynamo / cache and access is true
        if (!authenticationProvider.hasAccess(username)) {
            throw new ForbiddenException("You don't have access to control the server, ask Parker for access");
        }
    }
}
