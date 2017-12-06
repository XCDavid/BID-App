package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rgarciav on 05/12/2017.
 */

public class ResponseDetailMe {
    /*
    {
        "authenticated": true,
            "authorities": [
        "ROLE_USER"
    ],
        "clientId": 196,
            "details": {
        "remoteAddress": "192.168.1.66",
                "sessionId": null,
                "tokenValue": "61ec7784-c895-496c-bda9-6d53218d16a5",
                "tokenType": "Bearer",
                "decodedDetails": null
    },
        "username": "operq"
    }
    */

    @SerializedName("authenticated")
    boolean authenticated;

    @SerializedName("authorities")
    List<String> authorities;

    @SerializedName("clientId")
    int     clientId;

    @SerializedName("remoteAddress")
    String remoteAddress;

    @SerializedName("sessionId")
    String sessionId;

    @SerializedName("tokenValue")
    String tokenValue;

    @SerializedName("tokenType")
    String tokenType;

    @SerializedName("decodedDetails")
    String decodedDetails;

    @SerializedName("username")
    String username;

    public ResponseDetailMe(boolean authenticated, List<String> authorities, int clientId, String remoteAddress, String sessionId, String tokenValue, String tokenType, String decodedDetails, String username) {
        this.authenticated = authenticated;
        this.authorities = authorities;
        this.clientId = clientId;
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
        this.decodedDetails = decodedDetails;
        this.username = username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getDecodedDetails() {
        return decodedDetails;
    }

    public void setDecodedDetails(String decodedDetails) {
        this.decodedDetails = decodedDetails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
