package com.rpg2014.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Ec2MethodNames {
    StartInstance("startInstance"),
    StopInstance("stopInstance"),
    DomainName("getInstanceDomainName"),
    Status("getInstanceStatus");

    @Getter
    @NonNull
    String methodName;
}
