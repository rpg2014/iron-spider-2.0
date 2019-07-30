package com.rpg2014;

import com.rpg2014.filters.RequiresAccess.RequiresAccess;
import com.rpg2014.filters.RequiresLogin.RequiresLogin;
import com.rpg2014.invokers.EC2Invoker;
import com.rpg2014.model.DetailsResponse;
import com.rpg2014.model.Ec2MethodNames;
import com.rpg2014.model.ServerControllerInterface;
import com.rpg2014.model.Status;
import com.rpg2014.model.StatusResponse;

import com.rpg2014.model.start.StartResponse;
import com.rpg2014.model.stop.StopResponse;

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
    EC2Invoker ec2Invoker = new EC2Invoker();
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
        Status status = (Status) ec2Invoker.invoke(Ec2MethodNames.Status).get();
        return StatusResponse.builder().status(status).build();
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @Path("/details")
    public DetailsResponse serverDetails() {
        String domainName = (String) ec2Invoker.invoke(Ec2MethodNames.DomainName).get();
        return DetailsResponse.builder().domainName(domainName).build();
    }

    @Override
    @GET
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @RequiresAccess
    public StartResponse serverStart() {
        ec2Invoker.invoke(Ec2MethodNames.StartInstance);
        return StartResponse.builder().serverStarted(true).build();
    }

    @Override
    @POST
    @Path("/stop")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresLogin
    @RequiresAccess
    public StopResponse serverStop() {
        ec2Invoker.invoke(Ec2MethodNames.StartInstance);
        return StopResponse.builder().serverStopping(true).build();
    }


}
