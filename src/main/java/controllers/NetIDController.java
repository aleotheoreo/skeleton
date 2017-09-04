package controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

@Path("/netid")
public class NetIDController {

    @GET
    public String getNetID() {
        return "anm232";
    }

}
