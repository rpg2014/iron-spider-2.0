package com.rpg2014;
import javax.ejb.Asynchronous;

public class TestSuspended {

    @Asynchronous
    public static void start(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
