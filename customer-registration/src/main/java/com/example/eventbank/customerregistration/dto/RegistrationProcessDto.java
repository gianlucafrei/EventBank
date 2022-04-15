package com.example.eventbank.customerregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationProcessDto implements Serializable {

    private String firstName;
    private String lastName;
    private String emailAddress;
}
