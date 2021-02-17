package com.rpg2014.tasks;

import com.rpg2014.wrappers.DynamoDB.ServerDynamoWrapper;
import com.rpg2014.wrappers.EC2.EC2Wrapper;
import com.rpg2014.wrappers.Route53Wrapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class UpdateDNSTask implements Runnable {
    private Route53Wrapper route53Wrapper;
    private ServerDynamoWrapper serverDetails;
    private EC2Wrapper ec2Wrapper;
    private String url;

    @Override
    public void run() {
        String ipAddress;
        if (serverDetails.isServerRunning()) {
            ipAddress = ec2Wrapper.getInstanceIp();
        } else {
            ipAddress = "8.8.8.8";
        }
        route53Wrapper.updateDNSForURL(url, ipAddress);

    }
}
