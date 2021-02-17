package com.rpg2014.wrappers.EC2;

import com.rpg2014.model.Status;

public interface EC2Wrapper {
    boolean isInstanceUp();
    boolean startInstance();
    boolean stopInstance();
    void rebootInstance();
    Status getInstanceStatus();
    boolean isInstanceStopped();
    String getInstanceIp();
    String getInstanceDomainName();


}
