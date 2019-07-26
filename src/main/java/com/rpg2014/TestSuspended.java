package com.rpg2014;
import javax.ejb.Asynchronous;

public class TestSuspended {


    public static void start(){
        try {
            Thread.sleep(10000);
            System.out.println("Thread end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
