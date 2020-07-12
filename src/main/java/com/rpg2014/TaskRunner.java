package com.rpg2014;

import com.rpg2014.model.Ec2MethodNames;
import com.rpg2014.wrappers.SpidermanEC2Wrapper;
import lombok.NonNull;

import javax.ws.rs.InternalServerErrorException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {

    Executor executor = Executors.newSingleThreadExecutor();
    private SpidermanEC2Wrapper instance = SpidermanEC2Wrapper.getInstance();

    public Optional runEC2Task(Ec2MethodNames methodName) {
        Runnable r;
        switch (methodName) {
            case StartInstance:
            case StopInstance:
                r = getAsyncRunnable(methodName);
                executor.execute(r);
                return Optional.empty();
            default:
                return invokeMethod(methodName);
        }
    }

    public void runAsyncTask(@NonNull Runnable task) {
        executor.execute(task);
    }

    private Runnable getAsyncRunnable(Ec2MethodNames methodName) {
        return () -> {
            Method method = null;
            try {
                method = instance.getClass().getDeclaredMethod(methodName.getMethodName());
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException | AssertionError | NoSuchMethodException e) {
                e.printStackTrace();
                throw new InternalServerErrorException(e.getMessage());
            }
        };
    }

    private Optional invokeMethod(Ec2MethodNames methodName) {
        Method method = null;
        try {
            method = instance.getClass().getDeclaredMethod(methodName.getMethodName());
            Object o = method.invoke(instance);
            return Optional.of(o);
        } catch (IllegalAccessException | InvocationTargetException | AssertionError | NoSuchMethodException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getCause().getMessage());
        }
    }
}
