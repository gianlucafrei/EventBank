package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssueCardRequest {

    private String firstName;
    private String lastName;
    private String accountId;

}
