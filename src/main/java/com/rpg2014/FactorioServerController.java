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
import com.rpg2014.tasks.UpdateDNSTask;
import com.rpg2014.tasks.WaitForServerToBeUp;
import com.rpg2014.tasks.WaitForServerToShutdown;
import com.rpg2014.wrappers.DynamoDB.FactorioDyanmoWrapper;
import com.rpg2014.wrappers.EC2.FactorioEC2Wrapper;
import com.rpg2014.wrappers.DynamoDB.MinecraftDynamoWrapper;
import com.rpg2014.wrappers.Route53Wrapper;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("/factorio/server")
@Slf4j
public class FactorioServerController implements ServerControllerInterface {
    TaskRunner taskRunner = new TaskRunner(FactorioEC2Wrapper.getInstance());
    UpdateDNSTask updateDNSTask = UpdateDNSTask.builder()
            .url(System.getenv("factorioURL"))
            .ec2Wrapper(FactorioEC2Wrapper.getInstance())
            .route53Wrapper(Route53Wrapper.getInstance())
            .serverDetails(FactorioDyanmoWrapper.getInstance())
            .build();

    WaitForServerToBeUp waitForServerToBeUpTask = WaitForServerToBeUp.builder()
            .ec2Wrapper(FactorioEC2Wrapper.getInstance())
            .build();
    WaitForServerToShutdown waitForServerToShutDownTask = WaitForServerToShutdown.builder()
            .ec2Wrapper(FactorioEC2Wrapper.getInstance()).build();

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
        String url = System.getenv("factorioURL");
        return DetailsResponse.builder().domainName(url).build();
    }

    @Override
    @POST
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @RequiresAccess
    public StartResponse serverStart() {
        // launch instance with userdata
        taskRunner.runEC2Task(Ec2MethodNames.StartInstance);
        taskRunner.runAsyncTask(waitForServerToBeUpTask);
        //update the dns asap
        taskRunner.runAsyncTask(updateDNSTask);
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
        taskRunner.runAsyncTask(waitForServerToShutDownTask);
        taskRunner.runAsyncTask(updateDNSTask);
        return StopResponse.builder().serverStopping(true).build();
    }


}
