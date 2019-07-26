package com.rpg2014;

import com.rpg2014.filters.RequiresLogin.RequiresLogin;
import com.rpg2014.model.DetailsResponse;
import com.rpg2014.model.ServerControllerInterface;
import com.rpg2014.model.Status;
import com.rpg2014.model.StatusResponse;

import com.rpg2014.model.start.StartResponse;
import com.rpg2014.model.stop.StopResponse;

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
    @RequiresLogin
    public StatusResponse serverStatus() {
//        Status status = EC2Wrapper.getServerStatus();
        return StatusResponse.builder().status(Status.Terminated).build();
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details")
    public DetailsResponse serverDetails() {
        return null;
    }

    @Override
    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public StartResponse serverStart() {
        new Thread(()-> {
            TestSuspended.start();
        }).start();

        return StartResponse.builder().serverStarted(true).build();
    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public StopResponse serverStop() {
        return null;
    }


}
