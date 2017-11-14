package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Entidad para obtener token de acceso
 */
public class OAuthAccessToken {

    /**
     * Token de acceso para realizar peticiónes a recursos protegidos
     */
    @SerializedName("access_token")
    private String accessToken;

    /**
     * Tipo de token obtenido, normalmente del tipo Bearer
     */
    @SerializedName("token_type")
    private String tokenType;

    /**
     * Tiempo de expiración del token en milisegundos
     */
    @SerializedName("expires_in")
    private Long expiresIn;

    /**
     * Permisos del usuario en el API
     */
    @SerializedName("scope")
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
