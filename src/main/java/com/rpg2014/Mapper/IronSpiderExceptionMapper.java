package com.rpg2014.Mapper;

import com.rpg2014.model.ErrorResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IronSpiderExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {
        if (e instanceof NotAuthorizedException) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity(ErrorResponse.builder().errorMessage(e.getMessage()).build()).build();
            return response;
        }
        if (e instanceof ForbiddenException) {
            Response response = Response.status(Response.Status.FORBIDDEN).entity(ErrorResponse.builder().errorMessage(e.getMessage()).build()).build();
            return response;
        }
        if (e instanceof InternalServerErrorException) {
            Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorResponse.builder().errorMessage(e.getMessage())).build();
            return response;
        }
        if (e instanceof BadRequestException) {
            Response response = Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder().errorMessage(e.getMessage())).build();
            return response;
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ErrorResponse.builder().errorMessage(e.getMessage())).build();
    }
}
