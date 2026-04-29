package com.example.ayan.Chat.Application.Model;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JwtAuthResponseDTO {

    private String accessToken;
    private String refreshToken;



    public JwtAuthResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
