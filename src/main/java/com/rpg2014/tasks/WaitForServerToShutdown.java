package com.rpg2014.tasks;

import com.rpg2014.model.Status;
import com.rpg2014.wrappers.EC2.EC2Wrapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class WaitForServerToShutdown implements Runnable {
    private EC2Wrapper ec2Wrapper;
    @Override
    public void run() {
        Status status = ec2Wrapper.getInstanceStatus();
        if(status == Status.Stopping || status == Status.ShuttingDown || status == Status.Stopped) {
            while (ec2Wrapper.getInstanceStatus() != Status.Terminated) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.warn("Sleep interrupted");
                }
            }
        }else {
            log.warn("Server might not make it to running so ending task now.");
        }

    }
}
