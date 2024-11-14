package com.spring.integrationPoc.dto;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String status;
}
