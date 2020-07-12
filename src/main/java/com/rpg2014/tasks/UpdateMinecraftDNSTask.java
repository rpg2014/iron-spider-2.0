package com.rpg2014.tasks;

import com.rpg2014.wrappers.MinecraftDynamoWrapper;
import com.rpg2014.wrappers.Route53Wrapper;
import com.rpg2014.wrappers.SpidermanEC2Wrapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class UpdateMinecraftDNSTask implements Runnable {
    private Route53Wrapper route53Wrapper;
    private MinecraftDynamoWrapper serverDetails;
    private SpidermanEC2Wrapper ec2Wrapper;

    @Override
    public void run() {
        String ipAddress;
        if (serverDetails.isServerRunning()) {
            ipAddress = ec2Wrapper.getInstanceIp();
        } else {
            ipAddress = "8.8.8.8";
        }
        log.info("Updating Minecraft dns to IP: " + ipAddress);
        route53Wrapper.updateMinecraftDNS(ipAddress);

    }
}
