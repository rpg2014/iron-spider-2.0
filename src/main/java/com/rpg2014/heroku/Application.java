package com.rpg2014.heroku;

import com.rpg2014.JsonObjectMapper.JsonMappingFeature;
import org.glassfish.jersey.server.ResourceConfig;


public class Application extends ResourceConfig {
    public Application() {
        packages("com.rpg2014");
        register(JsonMappingFeature.class);
    }

}
