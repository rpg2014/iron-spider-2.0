package com.rpg2014.wrappers;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.Change;
import software.amazon.awssdk.services.route53.model.ChangeAction;
import software.amazon.awssdk.services.route53.model.ChangeBatch;
import software.amazon.awssdk.services.route53.model.ChangeResourceRecordSetsRequest;
import software.amazon.awssdk.services.route53.model.ChangeResourceRecordSetsResponse;
import software.amazon.awssdk.services.route53.model.ChangeStatus;
import software.amazon.awssdk.services.route53.model.GetChangeRequest;
import software.amazon.awssdk.services.route53.model.GetChangeResponse;
import software.amazon.awssdk.services.route53.model.InvalidChangeBatchException;
import software.amazon.awssdk.services.route53.model.InvalidInputException;
import software.amazon.awssdk.services.route53.model.NoSuchHealthCheckException;
import software.amazon.awssdk.services.route53.model.NoSuchHostedZoneException;
import software.amazon.awssdk.services.route53.model.PriorRequestNotCompleteException;
import software.amazon.awssdk.services.route53.model.RRType;
import software.amazon.awssdk.services.route53.model.ResourceRecord;
import software.amazon.awssdk.services.route53.model.ResourceRecordSet;

@Slf4j
public class Route53Wrapper {

    private static Route53Wrapper ourInstance = new Route53Wrapper();

    private static final String HOSTED_ZONE_ID = "ZSXXJQ44AUHG2";

    private Route53Client client;


    public static Route53Wrapper getInstance() {
        return ourInstance;
    }

    private Route53Wrapper() {
        this.client = Route53Client.builder().region(Region.US_EAST_1).build();
    }

    public boolean updateMinecraftDNS(String ipAddress) {
        ChangeResourceRecordSetsRequest request = ChangeResourceRecordSetsRequest.builder().hostedZoneId(HOSTED_ZONE_ID).changeBatch(
                ChangeBatch.builder()
                        .changes(
                                Change.builder()
                                        .action(ChangeAction.UPSERT)
                                        .resourceRecordSet(
                                                ResourceRecordSet.builder()
                                                        .name("minecraft.parkergiven.com")
                                                        .resourceRecords(
                                                                ResourceRecord.builder().value(ipAddress).build())
                                                        .ttl(300L)
                                                        .type(RRType.A)
                                                        .build()
                                        )
                                        .build()
                        ).build()
        ).build();


        try {
            ChangeResourceRecordSetsResponse response = client.changeResourceRecordSets(request);
            String id = response.changeInfo().id();

            ChangeStatus status = response.changeInfo().status();
            GetChangeResponse changeResponse;
            while (status == ChangeStatus.PENDING) {
                try {
                    log.info("Waiting for route53 to update...");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.warn("Route53 sleep interrupted");
                }

                GetChangeRequest request1 = GetChangeRequest.builder().id(id).build();
                changeResponse = client.getChange(request1);

                status = changeResponse.changeInfo().status();
            }
        } catch(NoSuchHostedZoneException | NoSuchHealthCheckException e) {
            log.error("Hosted Zone or Health Check is incorrect");
            log.error(e.getMessage());
        } catch (InvalidChangeBatchException e) {
            log.error("Change batch is incorrect");
            log.error(e.getMessage());
        } catch (InvalidInputException e) {
            log.error(e.getMessage());
        } catch(PriorRequestNotCompleteException e) {
            log.error("Prior Request is not Complete");
            log.error(e.getMessage());
        }

        return true;
    }


}
