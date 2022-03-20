package com.smirnov.api.entities;

import javax.persistence.*;

import com.smirnov.api.models.ClientView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String username;
    private String password;
    private Boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "passport_series")
    private String passportSeria;
    @Column(name = "passport_num")
    private String passportNum;

    public Client() {}
    public Client(ClientView clientView){
        username = clientView.getUsername();
        password = clientView.getPassword();
        firstName = clientView.getFirstName();
        lastName = clientView.getLastName();
        fatherName = clientView.getFatherName();
        passportSeria = clientView.getPassportSeria();
        passportNum = clientView.getPassportNum();
    }

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

    public Client clone(Client newClient) {
        setFirstName(newClient.getFirstName());
        setFatherName(newClient.getFatherName());
        setLastName(newClient.getLastName());
        setPassportNum(newClient.getPassportNum());
        setPassportSeria(newClient.getPassportSeria());
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPassportSeria() {
        return passportSeria;
    }

    public void setPassportSeria(String passportSeria) {
        this.passportSeria = passportSeria;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    public String getPassword() {

        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}