package de.blackforestsolutions.dravelopspolygonservice.objectmothers;


import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public class ApiTokenObjectMother {

    public static ApiToken getOpenTripPlannerApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol("http")
                .setHost("localhost")
                .setPort(8089)
                .setRouter("bw")
                .setPath("/otp/routers/bw")
                .build();
    }
}
