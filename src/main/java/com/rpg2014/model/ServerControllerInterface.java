package com.rpg2014.model;


import com.rpg2014.model.start.StartResponse;
import com.rpg2014.model.stop.StopResponse;

public interface ServerControllerInterface {


    StatusResponse serverStatus();

    DetailsResponse serverDetails();

    StartResponse serverStart();

//    StartResponse serverStart();

//

    StopResponse serverStop();


//    RebootRequest serverReboot(Reboot rebootRequest);
}
