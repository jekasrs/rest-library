package smirnov.la.restlib.models;

import smirnov.la.restlib.entities.Client;
import lombok.Data;

import java.util.stream.Stream;

@Data
public class ClientView {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String fatherName;
    private String passportSeria;
    private String passportNum;

    public static Boolean isForbiddenSymbol(String forbiddenSymbols, char c) {
        long count = forbiddenSymbols
                .chars()
                .mapToObj(i -> (char)i)
                .filter(i-> c==i)
                .count();
        return count > 0;
    }
    public static Boolean isValidName(String name) {
        String forbiddenSymbols = "1234567890-=><?/.,\\}{[]'";
        long count = name.chars()
                .mapToObj(i-> (char) i)
                .filter(i -> isForbiddenSymbol(forbiddenSymbols, i))
                .count();
        return count == 0;
    }
    public static Boolean isValidPassport(String passport) {
        String forbiddenSymbols = "-!=><?/.,\\}{[]'";
        long count = passport.chars()
                .mapToObj(i-> (char) i)
                .filter(i -> (i >= 'a' && i <= 'z') || (i >= 'A' && i <= 'Z'))
                .filter(i -> isForbiddenSymbol(forbiddenSymbols, i))
                .count();
        return count == 0;
    }
}
