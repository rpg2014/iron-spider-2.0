package com.rpg2014;

import com.rpg2014.filters.RequiresAccess.RequiresAccess;
import com.rpg2014.filters.RequiresLogin.RequiresLogin;
import com.rpg2014.model.DetailsResponse;
import com.rpg2014.model.Ec2MethodNames;
import com.rpg2014.model.ServerControllerInterface;
import com.rpg2014.model.Status;
import com.rpg2014.model.StatusResponse;
import com.rpg2014.model.start.StartResponse;
import com.rpg2014.model.stop.StopResponse;
import com.rpg2014.tasks.UpdateMinecraftDNSTask;
import com.rpg2014.wrappers.MinecraftDynamoWrapper;
import com.rpg2014.wrappers.Route53Wrapper;
import com.rpg2014.wrappers.SpidermanEC2Wrapper;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("/server")
public class ServerController implements ServerControllerInterface {
    TaskRunner taskRunner = new TaskRunner();
    UpdateMinecraftDNSTask updateMinecraftDNSTask = UpdateMinecraftDNSTask.builder()
            .ec2Wrapper(SpidermanEC2Wrapper.getInstance())
            .route53Wrapper(Route53Wrapper.getInstance())
            .serverDetails(MinecraftDynamoWrapper.getInstance())
            .build();


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
        Status status = (Status) taskRunner.runEC2Task(Ec2MethodNames.Status).get();
        return StatusResponse.builder().status(status).build();
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @Path("/details")
    public DetailsResponse serverDetails() {
        String domainName = (String) taskRunner.runEC2Task(Ec2MethodNames.DomainName).get();
        return DetailsResponse.builder().domainName(domainName).build();
    }

    @Override
    @POST
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @RequiresAccess
    public StartResponse serverStart() {
        taskRunner.runEC2Task(Ec2MethodNames.StartInstance);
        taskRunner.runAsyncTask(updateMinecraftDNSTask);
        return StartResponse.builder().serverStarted(true).build();
    }

    @Override
    @POST
    @Path("/stop")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @RequiresAccess
    public StopResponse serverStop() {
        taskRunner.runEC2Task(Ec2MethodNames.StopInstance);
        taskRunner.runAsyncTask(updateMinecraftDNSTask);
        return StopResponse.builder().serverStopping(true).build();
    }


}
