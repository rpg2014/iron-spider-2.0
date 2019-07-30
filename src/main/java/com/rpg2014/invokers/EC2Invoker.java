package com.rpg2014.invokers;

import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EC2Invoker {

    public void EC2Invoker (Object obj, String methodName) throws InterruptedException {
        Thread t = new Thread(setRunner(obj, methodName));
        t.start();
        t.join();
    }

    public void EC2Invoker (Object obj, String methodName, RunInstancesResponse runInstancesResponse) throws InterruptedException {
        Thread t = new Thread(setRunner(obj, methodName, runInstancesResponse));
        t.start();
        t.join();
    }

    public void EC2Invoker (Object obj, String methodName, String instanceId) throws InterruptedException {
        Thread t = new Thread(setRunner(obj, methodName, instanceId));
        t.start();
        t.join();
    }

    public void EC2Invoker (Object obj, String methodName, String oldAMIid, final String oldSnapshotId) throws InterruptedException {
        Thread t = new Thread(setRunner(obj, methodName, oldAMIid, oldSnapshotId));
        t.start();
        t.join();
    }

    private Runnable setRunner (Object obj, String methodName) {
        Runnable r = () -> {
            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) { e.printStackTrace(); }

            assert method != null;
            method.setAccessible(true);
            try {
                method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        };
        return r;
    }

    private Runnable setRunner (Object obj, String methodName, RunInstancesResponse runInstancesResponse) {
        Runnable r = () -> {
            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) { e.printStackTrace(); }

            assert method != null;
            method.setAccessible(true);
            try {
                method.invoke(obj, runInstancesResponse);
            } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        };
        return r;
    }

    private Runnable setRunner (Object obj, String methodName, String instanceId) {
        Runnable r = () -> {
            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) { e.printStackTrace(); }

            assert method != null;
            method.setAccessible(true);
            try {
                method.invoke(obj, instanceId);
            } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        };
        return r;
    }

    private Runnable setRunner (Object obj, String methodName, String oldAMIid, final String oldSnapshotId) {
        Runnable r = () -> {
            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) { e.printStackTrace(); }

            assert method != null;
            method.setAccessible(true);
            try {
                method.invoke(obj, oldAMIid, oldSnapshotId);
            } catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        };
        return r;
    }
}
