package com.rpg2014.filters.RequiresAccess;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class RequiresAccessFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        RequiresAccess requiresAccess = resourceInfo.getResourceMethod().getAnnotation(RequiresAccess.class);
        if (requiresAccess == null) {
            return;
        }
        RequiresAuthFilter filter = new RequiresAuthFilter();
        featureContext.register(filter);
    }
}
