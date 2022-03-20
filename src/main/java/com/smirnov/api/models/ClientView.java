package com.smirnov.api.models;

import com.smirnov.api.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientView {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String passportSeria;
    private String passportNum;

    public ClientView(Client client) {
        username = client.getUsername();
        password = client.getPassword();
        firstName = client.getFirstName();
        lastName = client.getLastName();
        fatherName = client.getFatherName();
        passportSeria = client.getPassportSeria();
        passportNum = client.getPassportNum();
    }
}
