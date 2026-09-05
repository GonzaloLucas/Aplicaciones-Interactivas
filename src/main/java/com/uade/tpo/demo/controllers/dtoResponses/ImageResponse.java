package com.uade.tpo.demo.controllers.dtoResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private Long id;
    private String file;
    private boolean esPortada;
}
