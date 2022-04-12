package com.example.eventbank.customerregistration.web.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CamundaMessageDto implements Serializable {

    private String correlationId;
    private MessageProcessDto dto;

}
