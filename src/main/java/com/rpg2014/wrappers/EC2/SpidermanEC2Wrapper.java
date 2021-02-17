package com.rpg2014.wrappers.EC2;

import com.rpg2014.model.Status;
import com.rpg2014.wrappers.DynamoDB.MinecraftDynamoWrapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateImageRequest;
import software.amazon.awssdk.services.ec2.model.CreateImageResponse;
import software.amazon.awssdk.services.ec2.model.DeleteSnapshotRequest;
import software.amazon.awssdk.services.ec2.model.DeregisterImageRequest;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.ImageState;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RebootInstancesResponse;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Snapshot;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesResponse;
import software.amazon.awssdk.services.ec2.model.StopInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SpidermanEC2Wrapper  implements EC2Wrapper{
    private static final String AMI_NAME = "Minecraft_Server";
    private static final String SECURITY_GROUP_ID = "sg-0bcf97234db49f1d4";
    private static final String AWS_ACCOUNT_ID = System.getenv("AWS_ACCOUNT_ID");
    private static final String INSTANCE_TYPE = System.getenv("EC2_INSTANCE_TYPE");
    private static final String USER_DATA =
            "KGNyb250YWIgLWwgMj4vZGV2L251bGw7IGVjaG8gIiovNSAqICAgKiAgICogICAqICAgd2dldCAtcSAtTyAtICJodHRwczovL2lyb24tc3BpZGVyLmhlcm9rdWFwcC5jb20iID4vZGV2L251bGwgMj4mMSIpIHwgY3JvbnRhYiAtCnNoIG1pbmVjcmFmdC9ydW5fc2VydmVyLnNo";
    //"(crontab -l 2>/dev/null; echo \"*/5 *   *   *   *   wget -q -O - \"url.com\" >/dev/null 2>&1\") | crontab -\nsh minecraft/run_server.sh";
    private static SpidermanEC2Wrapper ourInstance = new SpidermanEC2Wrapper();
    private Ec2Client ec2Client;
    private MinecraftDynamoWrapper serverDetails;
    private String oldAMIid;
    private String oldSnapshotId;

    private SpidermanEC2Wrapper() {
        this.ec2Client = Ec2Client.builder().region(Region.US_EAST_1).build();
        serverDetails = MinecraftDynamoWrapper.getInstance();
        oldAMIid = serverDetails.getAmiID();
        oldSnapshotId = serverDetails.getSnapshotId();
    }

    public static SpidermanEC2Wrapper getInstance() {
        return ourInstance;
    }

    public boolean startInstance() {
        if (!serverDetails.isServerRunning() || !isInstanceUp()) {
            String amiId = serverDetails.getAmiID();

            RunInstancesRequest runInstancesRequest = RunInstancesRequest.builder()
                    .imageId(amiId)
                    .instanceType(INSTANCE_TYPE)
                    .maxCount(1)
                    .minCount(1)
                    .userData(USER_DATA)
                    .securityGroupIds(SECURITY_GROUP_ID)
                    .keyName("Minecraft Server")
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

    public boolean stopInstance() {
        if (serverDetails.isServerRunning() || isInstanceUp()) {
            String instanceId = serverDetails.getInstanceId();
            String currentAMIId = serverDetails.getAmiID();
            String currentSnapshot = serverDetails.getSnapshotId();
            if (!oldAMIid.equals(currentAMIId)) {
                log.error("OLD AMI IS OUT OF DATE, oldami: {} , dynamoAMI: {}", oldAMIid, currentAMIId);
            }
            StopInstancesRequest request = StopInstancesRequest.builder().instanceIds(instanceId).build();
            ec2Client.stopInstances(request);

            waitForServerStop(instanceId);
            serverDetails.setServerStopped();
            String amiId = makeAMI(instanceId);
            waitForSnapshotToBeCreated();
            waitForAMIToBeCreated();

            serverDetails.setAmiId(amiId);
            serverDetails.setSnapshotId(getNewestSnapshot());

            log.info("Server Stopped");
            if (!serverDetails.getAmiID().equals(currentAMIId) && !serverDetails.getSnapshotId().equals(currentSnapshot)) {
                log.info("Deleting old snapshot_id ");
                deleteOldAmi(currentAMIId, currentSnapshot);
            }

            TerminateInstancesRequest terminateInstancesRequest =
                    TerminateInstancesRequest.builder().instanceIds(instanceId).build();
            TerminateInstancesResponse terminateInstancesResult =
                    ec2Client.terminateInstances(terminateInstancesRequest);
            boolean success =
                    terminateInstancesResult.terminatingInstances().get(0).currentState().code() > 32;
            if (success)
                log.info("Terminated Server");

            serverDetails.setServerStopped();
            return success;
        } else {
            return false;
        }
    }

    private void waitForAMIToBeCreated() {
        DescribeImagesResponse response;

        do {
            log.info("Waiting for ami to be created");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new InternalServerErrorException(e.getMessage());
            }
            DescribeImagesRequest request = DescribeImagesRequest.builder()
                    .executableUsers("self")
                    .filters(
                            Filter.builder().name("state").values("pending", "failed", "error").build()
                    )
                    .build();
            response = ec2Client.describeImages(request);
            log.info(response.toString());
            if (response.images().stream().anyMatch(image -> image.state().equals(ImageState.FAILED) || image.state().equals(ImageState.ERROR))) {
                log.error("There is a failed ami");
                throw new InternalServerErrorException("There is a failed AMI, Contact Parker");
            }

        } while (response.images().stream().anyMatch(image -> image.state().equals(ImageState.PENDING)));
    }

    private void waitForSnapshotToBeCreated() {
        DescribeSnapshotsResponse response;
        List<Snapshot> finishedSnapshots = new ArrayList<>();
        do {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new InternalServerErrorException(e.getMessage());
            }
            DescribeSnapshotsRequest request = DescribeSnapshotsRequest.builder()
                    .ownerIds(AWS_ACCOUNT_ID.replaceAll("-", "")).build();
            response = ec2Client.describeSnapshots(request);
            if (response.snapshots().size() > 1)
                finishedSnapshots = response.snapshots().stream()
                        .filter(snapshot -> snapshot.progress().contains("100"))
                        .collect(Collectors.toList());
        } while (finishedSnapshots.size() != response.snapshots().size() && response.snapshots().size() == 1);
    }

    private void waitForServerStop(String instanceId) {
        log.info("Waiting for instance " + instanceId + "to stop");
        do {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!isInstanceStopped());
    }

    private String getNewestSnapshot() {
        DescribeSnapshotsRequest request = DescribeSnapshotsRequest.builder()
                .ownerIds(AWS_ACCOUNT_ID.replaceAll("-", "")).build();
        DescribeSnapshotsResponse response = ec2Client.describeSnapshots(request);
        Snapshot newestSnap = Snapshot.builder().startTime(new Date(Long.MIN_VALUE).toInstant()).build();

        for (Snapshot snapshot : response.snapshots()) {
            if (newestSnap.startTime().isBefore(snapshot.startTime()))
                newestSnap = snapshot;
        }
        log.info("Newest Snapshot is " + newestSnap.snapshotId());
        return newestSnap.snapshotId();
    }

    private void deleteOldAmi(String oldAMIid, final String oldSnapshotId) {
        DeregisterImageRequest deregisterImageRequest = DeregisterImageRequest.builder().imageId(oldAMIid).build();
        ec2Client.deregisterImage(deregisterImageRequest);
        DeleteSnapshotRequest deleteSnapshotRequest = DeleteSnapshotRequest.builder().snapshotId(oldSnapshotId).build();
        ec2Client.deleteSnapshot(deleteSnapshotRequest);
    }

    private String makeAMI(String instanceId) {
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .instanceId(instanceId)
                .name(AMI_NAME + "-" + Instant.now().hashCode())
                .build();
        CreateImageResponse createImageResponse = ec2Client.createImage(createImageRequest);
        String amiId = createImageResponse.imageId();
        log.info("Created AMI, image id: " + amiId);
        return amiId;
    }

    public void rebootInstance() {
        if (serverDetails.isServerRunning()) {
            RebootInstancesRequest request = RebootInstancesRequest.builder()
                    .instanceIds(serverDetails.getInstanceId()).build();
            RebootInstancesResponse result = ec2Client.rebootInstances(request);
            log.info(result.toString(), this.getClass().getSimpleName());
        } else {
            log.info("Server isn't up to be rebooted");
        }
    }

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

    public String getInstanceIp() {
        String instanceId = serverDetails.getInstanceId();
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().instanceIds(instanceId).build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        return response.reservations().get(0).instances().get(0).publicIpAddress();
    }

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
}
