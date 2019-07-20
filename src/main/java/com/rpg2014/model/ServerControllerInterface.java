package com.rpg2014.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

public interface ServerControllerInterface {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    StatusResponse serverStatus();
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    boolean testStart();
//    StartResponse serverStart(StartRequest startRequest);

//
//    @Path("/stop")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    StopResponse serverStop(StopRequest stopRequest);
//
//    @Path("/reboot")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    RebootRequest serverReboot(Reboot rebootRequest);
}
