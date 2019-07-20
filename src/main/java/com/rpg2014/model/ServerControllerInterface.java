package com.rpg2014.model;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/server")
public interface ServerControllerInterface {

    @Path("/status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    StatusResponse serverStatus();

//    @Path("/start")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
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
