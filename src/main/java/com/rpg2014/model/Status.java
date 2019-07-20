package com.rpg2014.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    Pending ("pending"),
    Running ("running"),
    ShuttingDown("shutting-down"),
    Terminated("terminated"),
    Stopping("stopping"),
    Stopped("stopped");

    @Getter
    String status;
    Status(String status){
        this.status = status;
    }
}
