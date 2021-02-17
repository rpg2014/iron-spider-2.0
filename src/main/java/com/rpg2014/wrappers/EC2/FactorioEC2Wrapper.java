package com.rpg2014.wrappers.EC2;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.encryptionsdk.model.ContentType;
import com.amazonaws.regions.Regions;
import com.rpg2014.model.Status;
import com.rpg2014.wrappers.DynamoDB.FactorioDyanmoWrapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import software.amazon.awssdk.services.ec2.model.IamInstanceProfileSpecification;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FactorioEC2Wrapper implements EC2Wrapper {
    private Ec2Client ec2Client;
    private AmazonS3 s3Client;
    private SsmClient ssmClient;
    private FactorioDyanmoWrapper serverDetails;
    public static FactorioEC2Wrapper getInstance() {
        return ourInstance;
    }

    private static final String S3_UPLOAD_COMMAND = "aws s3 putobject --bucket 'factoriosavegame' -key 'savegame.zip' --body savegame";

    private static final String USER_DATA_PREAMBLE= "#!/usr/bin/env bash\n" +
            "cd /tmp/ || exit\n" +
            "\n" +
            "curl -L -o /tmp/factorio.tar.xz https://factorio.com/get-download/stable/headless/linux64\n" +
            "#get save file from s3\n" +
            "mkdir -p /home/factorio/factorio/saves\n" +
            "cd /home/factorio || exit\n" +
            "curl -o /home/factorio/factorio/saves/savegame '";

    private static final String USER_DATA_END = "'\n" +
            "tar -xJf /tmp/factorio.tar.xz\n" +
            "\n" +
            "/home/factorio/factorio/bin/x64/factorio --start-server 'savegame' &";

    private static final String SECURITY_GROUP_ID = "sg-06cbac0bbe596d753";

    private static FactorioEC2Wrapper ourInstance = new FactorioEC2Wrapper();
    private FactorioEC2Wrapper() {
        this.ec2Client = Ec2Client.builder().region(Region.US_EAST_2).build();
        this.serverDetails = FactorioDyanmoWrapper.getInstance();
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new ProfileCredentialsProvider())
                .build();

        this.ssmClient = SsmClient.builder().region(Region.US_EAST_2).build();
    }
    @Override
    public boolean isInstanceUp() {
        String instanceId = serverDetails.getInstanceId();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        if (response.reservations().size() == 0 || response.reservations().get(0).instances().size() == 0)
            return false;
        boolean isUp = response.reservations().get(0).instances().get(0).state().code() == 16;
        if (isUp)
            log.info("Server instance " + instanceId + " is up");
        else
            log.info("Server instance " + instanceId + " is down");
        return isUp;
    }

    @Override
    public boolean startInstance() {
        //steps
        //start the server, AL2, small size, yadda yadda yadda
        // wget the factorio server,
        // wget the save from s3
        //start server

        if (!serverDetails.isServerRunning() || !isInstanceUp()) {
            String userDataString = USER_DATA_PREAMBLE+getS3URL()+USER_DATA_END;
            String userData = Base64.getEncoder().encodeToString(userDataString.getBytes());

            RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                    .imageId("ami-047a51fa27710816e") // AL2 ami id
                    .instanceType(System.getenv("FACTORIO_EC2_INSTANCE_TYPE"))
                    .maxCount(1)
                    .iamInstanceProfile(IamInstanceProfileSpecification.builder().arn("arn:aws:iam::593242635608:instance-profile/FactorioEC2Role").build())
                    .minCount(1)
                    .userData(userData)
                    .securityGroupIds(SECURITY_GROUP_ID)
                    .keyName("Factorio Server")
                    .build();

            RunInstancesResponse runInstancesResponse = ec2Client.runInstances(runInstancesRequest);
            String instanceId = getInstanceId(runInstancesResponse);
            serverDetails.setInstanceId(instanceId);

            StartInstancesRequest request = StartInstancesRequest.builder().instanceIds(instanceId).build();
            StartInstancesResponse response = ec2Client.startInstances(request);
            InstanceState state = response.startingInstances().get(0).currentState();
            boolean success = state.code() < 32;
            if (success) {
                log.info("Started server");
                serverDetails.setServerRunning();
            }
                return success;
            } else
                return false;
    }


    @Override
    public boolean stopInstance() {
        // use aws ssm to execute

        if (serverDetails.isServerRunning() || isInstanceUp()) {
            //somehow get save file to s3
            //want to use ssm to sendcommand to run below command.
            // aws s3 putobject --bucket 'factoriosavegame' -key 'savegame.zip' --body savegame
            Map<String, List<String>> commandMap = new HashMap<>();
            List<String> commands = new ArrayList<>();
            commands.add(S3_UPLOAD_COMMAND);
            commandMap.put("commands", commands);
            SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
                    .instanceIds(serverDetails.getInstanceId())
                    .documentName("AWS-RunShellScript")
                    .parameters(commandMap)
                    .build();
            try {
                SendCommandResponse response = ssmClient.sendCommand(sendCommandRequest);


                //if command was successfull  then turn off server
                if (response.command().completedCount() > 1) {
                    String instanceId = serverDetails.getInstanceId();
                    StopInstancesRequest request = StopInstancesRequest.builder().instanceIds(instanceId).build();
                    ec2Client.stopInstances(request);
                    TerminateInstancesRequest terminateInstancesRequest =
                            TerminateInstancesRequest.builder().instanceIds(instanceId).build();
                    TerminateInstancesResponse terminateInstancesResult =
                            ec2Client.terminateInstances(terminateInstancesRequest);
                    boolean success =
                            terminateInstancesResult.terminatingInstances().get(0).currentState().code() > 32;
                    if (success) {
                        log.info("Terminated Server");
                    }

                    serverDetails.setServerStopped();
                    return success;
                } else {
                    log.error("failed to back up factorio save");
                    return false;
                }
            }catch(Exception e){
                e.printStackTrace();
                throw new InternalServerErrorException("Failed  to send backup command to factorio server", e);
            }
            }else{
                log.warn("server is already running");
                return false;
            }


    }

    @Override
    public void rebootInstance() {
        throw new InternalServerErrorException("This operation is not supported");
    }

    public Status getInstanceStatus() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .instanceIds(serverDetails.getInstanceId()).build();
        try {
            DescribeInstancesResponse response = ec2Client.describeInstances(request);
            Status status;
            if (response.reservations().size() == 0) {
                status = Status.Terminated;
            } else {
                status = Status.of(response.reservations().get(0).instances().get(0).state().code());
            }
            return status;
        } catch (Ec2Exception e) {
            if (e.getMessage().contains("Invalid id") || e.getMessage().contains("does not exist")) {
                return Status.Terminated;
            }
            e.printStackTrace();
        }
        throw new InternalServerErrorException("Unable to Fetch server status");
    }

    @Override
    public boolean isInstanceStopped() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .instanceIds(serverDetails.getInstanceId()).build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        if (response.reservations().size() == 0 || response.reservations().get(0).instances().size() == 0)
            return true;
        boolean isDown = response.reservations().get(0).instances().get(0).state().code() == 80;
        if (isDown)
            log.info("Server Instance " + serverDetails.getInstanceId() + "is down");
        return isDown;
    }

    public String getInstanceIp() {
        String instanceId = serverDetails.getInstanceId();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        return response.reservations().get(0).instances().get(0).publicIpAddress();
    }

    @Override
    public String getInstanceDomainName() {
        String instanceId = serverDetails.getInstanceId();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();
        try {
            DescribeInstancesResponse response = ec2Client.describeInstances(request);
            return response.reservations().get(0).instances().get(0).publicDnsName();
        } catch (Ec2Exception e) {
            if (e.getMessage().contains("Invalid id")) {
                throw new BadRequestException("Server is not running");
            }
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    private URL getS3URL(){
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5 mins
        expiration.setTime(expTimeMillis);

        System.out.println("Generating pre-signed URL.");
        try {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest("factoriosavegame", "savegame.zip")
                        .withMethod(HttpMethod.GET)
                        .withContentType("application/zip")
                        .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url;
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new InternalServerErrorException("Problem with s3",e);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw new InternalServerErrorException("Problem with s3",e);
        }
    }

    private String getInstanceId(RunInstancesResponse runInstancesResponse) {
        List<Instance> instanceList = runInstancesResponse.instances();
        List<String> idList = new ArrayList<>();
        for (Instance instance : instanceList)
            idList.add(instance.instanceId());

        if (idList.size() == 1)
            return idList.get(0);
        else
            return null;
    }
}
