package com.smirnov.api.repositories;

import com.example.automationlib.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Client, Long> {

    List<Client> findClientsByFirstNameAndLastName(String firstName, String lastName);
    List<Client> findClientsByPassportSeriaNullAndPassportNumNull();
    Client getClientById(Long id);
    Optional<Client> getClientByUsername(String username);
    Boolean existsByFirstName(String name);
    Boolean existsByPassportSeriaAndPassportNum(String passporSeria, String passportNum);

    @Query("SELECT c FROM Client AS c ORDER BY c.firstName")
    List<Client> sortByFirstName();

    void deleteClientsByPassportSeriaIsNullAndPassportNumIsNull();
    void deleteClientsByFirstName(String firstName);
}
