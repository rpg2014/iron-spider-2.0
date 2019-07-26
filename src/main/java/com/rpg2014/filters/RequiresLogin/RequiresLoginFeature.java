package com.rpg2014.filters.RequiresLogin;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class RequiresLoginFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        RequiresLogin requiresLogin = resourceInfo.getResourceMethod().getAnnotation(RequiresLogin.class);
        if(requiresLogin ==null){
            return;
        }
        RequiresLoginFilter filter = new RequiresLoginFilter();
        featureContext.register(filter);
    }
}
