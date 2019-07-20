package com.rpg2014;

import com.rpg2014.model.ServerControllerInterface;
import com.rpg2014.model.Status;
import com.rpg2014.model.StatusResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
public class ServerController implements ServerControllerInterface {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "json" media type.
     *
     * @return json response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse serverStatus() {
        Status status = EC2Wrapper.getServerStatus();
        return StatusResponse.builder().status(Status.Terminated).build();
    }
}
