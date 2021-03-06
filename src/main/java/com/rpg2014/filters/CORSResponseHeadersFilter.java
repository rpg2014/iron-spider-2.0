package com.rpg2014.filters;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CORSResponseHeadersFilter implements ContainerResponseFilter {


    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "https://pwa.parkergiven.com");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "spider-access-token");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "DELETE");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "POST");
    }
}
