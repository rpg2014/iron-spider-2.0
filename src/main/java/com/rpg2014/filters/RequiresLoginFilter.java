package com.rpg2014.filters;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

@RequiresLogin
public class RequiresLoginFilter implements ContainerResponseFilter {

    @Override public void filter(ContainerRequestContext containerRequestContext,
        ContainerResponseContext containerResponseContext) throws IOException {
        String authToken = containerRequestContext.getHeaderString("spider-access-token");
        if (authToken == null)
            throw new NotAuthorizedException("No Auth Header");
        if (!verifyToken(authToken)){
            throw new NotAuthorizedException("Auth Token not valid");

        }

        //        if (verifyToken(token) == false) {
        //            throw new NotAuthorizedException("Bearer error=\"invalid_token\"");
        //        }
    }


    private boolean verifyToken(final String authToken){
        return true;
    }
}
