package com.smirnov.api.models;

import com.smirnov.api.entities.Client;
import lombok.Data;

@Data
public class ClientView  {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String passportSeria;
    private String passportNum;


    public static Boolean isForbiddenSymbol(String forbiddenSymbols, char c) {
        for (int i = 0; i < forbiddenSymbols.length(); i++)
            if (c == forbiddenSymbols.charAt(i))
                return true;
        return false;
    }
    public static Boolean isValidName(String name) {
        String forbiddenSymbols = "1234567890-=><?/.,\\}{[]'";
        for (int i = 0; i < name.length(); i++)
            if (isForbiddenSymbol(forbiddenSymbols, name.charAt(i)))
                return false;
        return true;
    }
    public static Boolean isValidPassport(String string) {
        String forbiddenSymbols = "-!=><?/.,\\}{[]'";
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                return false;
            if (isForbiddenSymbol(forbiddenSymbols, c))
                return false;
        }
        return true;
    }
}
