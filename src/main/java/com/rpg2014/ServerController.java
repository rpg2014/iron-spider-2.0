package com.rpg2014;

import com.rpg2014.model.ServerControllerInterface;
import com.rpg2014.model.Status;
import com.rpg2014.model.StatusResponse;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("/server")
public class ServerController implements ServerControllerInterface {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "json" media type.
     *
     * @return json response
     */
    @Override
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse serverStatus() {
//        Status status = EC2Wrapper.getServerStatus();
        return StatusResponse.builder().status(Status.Terminated).build();
    }

    @Override
    @POST
    @Path("/start")
    public boolean testStart() {
        TestSuspended.start();
        return false;
    }
}
