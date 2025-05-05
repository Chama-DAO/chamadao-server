package com.chama.chamadao_server.models.dto.mpesa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {
    private String access_token;
    private String expires_in;

    @Override
    public String toString() {
        return "AccessTokenResponse{" +
                "access_token='" + (access_token != null ? access_token.substring(0, Math.min(10, access_token.length())) + "..." : "null") + '\'' +
                ", expires_in='" + expires_in + '\'' +
                '}';
    }
}
