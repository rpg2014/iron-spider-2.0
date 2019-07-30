package com.rpg2014.invokers;

import com.rpg2014.wrappers.SpidermanEC2Wrapper;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EC2Invoker {

    public void EC2Invoker (SpidermanEC2Wrapper instance, String methodName) throws InterruptedException {
        Runnable r = () -> {
            Method method = null;
            try {
                method = instance.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException | AssertionError e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(r);
        t.start();
        t.join();
    }
}
