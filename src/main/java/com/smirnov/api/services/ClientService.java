package com.smirnov.api.services;

import com.example.automationlib.entities.Client;
import com.example.automationlib.entities.Role;
import com.example.automationlib.exceptions.ClientAlreadyExist;
import com.example.automationlib.exceptions.ClientBlankNameException;
import com.example.automationlib.exceptions.ClientIllegalSymbols;
import com.example.automationlib.exceptions.ClientNotFoundException;

import com.example.automationlib.repositories.ClientsRepository;
import com.example.automationlib.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService implements UserDetailsService {
    private final ClientsRepository clientsRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ClientService(ClientsRepository clientsRepository,
                         RoleRepository roleRepository,
                         BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clientsRepository = clientsRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private void ValidateData(Client client) throws ClientBlankNameException, ClientIllegalSymbols {

        if (client.getFirstName() == null)
            throw new ClientBlankNameException("Имя не заполнено");
        if (!Client.isValidName(client.getFirstName()))
            throw new ClientIllegalSymbols(ClientIllegalSymbols.message);
        if (client.getLastName() != null && !Client.isValidName(client.getLastName()))
            throw new ClientIllegalSymbols(ClientIllegalSymbols.message);
        if (client.getFatherName() != null && !Client.isValidName(client.getFatherName()))
            throw new ClientIllegalSymbols(ClientIllegalSymbols.message);
        if (client.getPassportNum() != null && !Client.isValidPassport(client.getPassportNum()))
            throw new ClientIllegalSymbols(ClientIllegalSymbols.message);
        if (client.getPassportSeria() != null && !Client.isValidPassport(client.getPassportSeria()))
            throw new ClientIllegalSymbols(ClientIllegalSymbols.message);
    }

    /* CREATE */
    public Client createClient(Client client) throws ClientBlankNameException,
            ClientIllegalSymbols, ClientAlreadyExist {
        ValidateData(client);

        if (client.getPassportSeria() != null && client.getPassportNum() != null)
            if (clientsRepository.existsByPassportSeriaAndPassportNum
                    (client.getPassportSeria(), client.getPassportNum()))
                throw new ClientAlreadyExist("Пользователь с такими паспортными данными уже существует");

        client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
        client.setRoles(Collections.singleton(new Role(1L, "ROLE_CLIENT")));
        client.setActive(true);

        return clientsRepository.save(client);
    }

    public Client createAdmin(Client client) throws ClientBlankNameException,
            ClientIllegalSymbols, ClientAlreadyExist {
        ValidateData(client);

        if (client.getPassportSeria() != null && client.getPassportNum() != null)
            if (clientsRepository.existsByPassportSeriaAndPassportNum
                    (client.getPassportSeria(), client.getPassportNum()))
                throw new ClientAlreadyExist("Пользователь с такими паспортными данными уже существует");

        client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
        client.setRoles(Collections.singleton(new Role(2L, "ROLE_ADMIN")));
        client.setActive(true);

        return clientsRepository.save(client);
    }

    /* READ */
    public Client findClientById(Long id) throws ClientNotFoundException {
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Такого клиента не существует: id=" + id);
        return clientsRepository.getClientById(id);
    }

    public List<Client> findAllClients() {
        return clientsRepository.findAll();
    }

    public List<Client> findClientsByFirstNameAndLastName(String firstName, String lastName)
            throws ClientBlankNameException {
        if (firstName == null || lastName == null)
            throw new ClientBlankNameException("Имя или фамилия не заполнены");
        return clientsRepository.findClientsByFirstNameAndLastName(firstName, lastName);
    }

    public List<Client> findClientsByPassportSeriaNullAndPassportNumNull() {
        return clientsRepository.findClientsByPassportSeriaNullAndPassportNumNull();
    }

    public Boolean existByFirstName(String name) {
        return clientsRepository.existsByFirstName(name);
    }

    /* SORT */
    public List<Client> sortByFirstName() {
        return clientsRepository.sortByFirstName();
    }

    /* UPDATE */
    public Client updateClient(Client client, Long id) throws ClientBlankNameException,
            ClientIllegalSymbols, ClientAlreadyExist, ClientNotFoundException {
        ValidateData(client);
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Такого клиента не существует: id=" + id);
        Client newClient = findClientById(id).clone(client);
        return clientsRepository.save(newClient);
    }

    /* DELETE */
    public void deleteClientById(Long id) throws ClientNotFoundException {
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Такого клиента не существует: id=" + id);
        clientsRepository.deleteById(id);
    }

    public void deleteClientsByPassportSeriaIsNullAndPassportNumIsNull() {
        clientsRepository.deleteClientsByPassportSeriaIsNullAndPassportNumIsNull();
    }

    public void deleteClientsByFirstName(String firstName) throws ClientBlankNameException,
            ClientNotFoundException {
        if (firstName == null)
            throw new ClientBlankNameException("Имя или фамилия не заполнены");
        if (!existByFirstName(firstName))
            throw new ClientNotFoundException("Нет такого клиента с именем=" + firstName);
        clientsRepository.deleteClientsByFirstName(firstName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientsRepository.getClientByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user: "+ username + "was not found!"));
    }
}
