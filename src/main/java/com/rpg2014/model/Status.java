package com.rpg2014.model;

public enum Status {
    Pending(0),
    Running(16),
    ShuttingDown(32),
    Terminated(48),
    Stopping(64),
    Stopped(80);


    int statusCode;

    Status(int code) {
        this.statusCode = code;
    }

    public static Status of(int code) {
        for (Status s : Status.values()) {
            if (s.statusCode == code)
                return s;
        }
        return null;
    }
}
