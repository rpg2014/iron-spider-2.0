package com.rpg2014.JsonObjectMapper;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public class JsonMappingFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JsonMappingFeature.class, MessageBodyReader.class, MessageBodyWriter.class);
        return true;
    }
}
