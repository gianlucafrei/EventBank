package com.example.eventbank.customerregistration.service.interf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IssueCardCommand {

    private String firstName;
    private String lastName;
}
