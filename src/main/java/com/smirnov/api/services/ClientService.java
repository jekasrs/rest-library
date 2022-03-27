package com.smirnov.api.services;

import com.smirnov.api.entities.Client;
import com.smirnov.api.entities.TypeBook;
import com.smirnov.api.exceptions.*;


import com.smirnov.api.models.ClientView;
import com.smirnov.api.repositories.ClientsRepository;
import com.smirnov.api.repositories.JournalRepository;
import com.smirnov.api.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class ClientService implements UserDetailsService {
    private final ClientsRepository clientsRepository;
    private final RoleRepository roleRepository;
    private final JournalRepository journalRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public ClientService(ClientsRepository clientsRepository,
                         RoleRepository roleRepository,
                         JournalRepository journalRepository,
                         BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clientsRepository = clientsRepository;
        this.roleRepository = roleRepository;
        this.journalRepository = journalRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private boolean isValidData(ClientView clientView) {

        if (clientView.getUsername() == null ||
                clientView.getPassword() == null ||
                clientView.getPassportNum() == null ||
                clientView.getPassportSeria() == null ||
                clientView.getFirstName() == null)
            return false;

        return ClientView.isValidPassport(clientView.getPassportNum()) &&
                ClientView.isValidPassport(clientView.getPassportSeria()) &&
                ClientView.isValidName(clientView.getFirstName());
    }

    /* CREATE */
    public Client createClient(ClientView clientView) throws ClientAlreadyExist, ClientIncorrectData {

        if (!isValidData(clientView))
            throw new ClientIncorrectData("Неправильные значения, пользователь не добавлен. ");

        if (clientsRepository.existsByPassportSeriaAndPassportNum(clientView.getPassportSeria(), clientView.getPassportNum()))
            throw new ClientAlreadyExist("Пользователь с такими паспортными данными уже существует. ");

        if (clientsRepository.existsByUsername(clientView.getUsername()))
            throw new ClientAlreadyExist("Логин уже занят. ");

        Client client = new Client(clientView);
        client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
        client.setRoles(Collections.singleton(roleRepository.getById(1L)));
        client.setActive(true);

        return clientsRepository.save(client);
    }
    public Client createAdmin(ClientView clientView) throws ClientAlreadyExist, ClientIncorrectData {

        if (!isValidData(clientView))
            throw new ClientIncorrectData("Неправильные значения, Админ не добавлен. ");

        if (clientsRepository.existsByPassportSeriaAndPassportNum(clientView.getPassportSeria(), clientView.getPassportNum()))
            throw new ClientAlreadyExist("Админ с такими паспортными данными уже существует. ");

        if (clientsRepository.existsByUsername(clientView.getUsername()))
            throw new ClientAlreadyExist("Логин уже занят. ");

        Client client = new Client(clientView);
        client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
        client.setRoles(Collections.singleton(roleRepository.getById(2L)));
        client.setActive(true);

        return clientsRepository.save(client);
    }

    /* READ */
    public Client findClientById(Long id) throws ClientNotFoundException {
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Пользователя не существует с id: " + id);
        return clientsRepository.getClientById(id);
    }
    public ClientView findClientViewById(Long id) throws ClientNotFoundException {
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Клиента не существует с id: " + id);

        Client client = clientsRepository.getClientById(id);
        ClientView clientView = new ClientView();
        clientView.setId(client.getId());
        clientView.setUsername(client.getUsername());
        clientView.setPassword(client.getPassword());
        clientView.setFirstName(client.getFirstName());
        clientView.setLastName(client.getLastName());
        clientView.setFatherName(client.getFatherName());
        clientView.setPassportNum(client.getPassportNum());
        clientView.setPassportSeria(client.getPassportSeria());

        return clientView;
    }
    public List<Client> findAllClients() {
        return clientsRepository.findAll();
    }
    public List<Client> findClientsByFirstNameAndLastName(String firstName, String lastName) throws ClientIncorrectData {
        if (firstName == null || lastName == null)
            throw new ClientIncorrectData("Имя или фамилия не заполнены. ");
        return clientsRepository.findClientsByFirstNameAndLastName(firstName, lastName);
    }
    public Boolean existByFirstName(String name) throws ClientIncorrectData {
        if (name == null)
            throw new ClientIncorrectData("Пользователь не может быть без имени");
        return clientsRepository.existsByFirstName(name);
    }

    /* SORT */
    public List<Client> sortByFirstName() {
        return clientsRepository.sortByFirstName();
    }

    /* UPDATE */
    public Client updateClient(ClientView clientView, Long id) throws ClientAlreadyExist, ClientNotFoundException, ClientIncorrectData {

        if (!isValidData(clientView))
            throw new ClientIncorrectData("Неправильные значения, пользователь не обновлен. ");

        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Такого пользователя не существует: id=" + id+" пользователь не обновлен. ");

        Client preClient = findClientById(id);
        List<Client> clients1 = clientsRepository.findAllByPassportSeriaAndPassportNum(preClient.getPassportSeria(), preClient.getPassportNum());
        if (clients1.size()>1)
            throw new ClientAlreadyExist("Пользователь с такими паспортными данными уже существует, пользователь не обновлен.");

        List<Client> clients2 = clientsRepository.findAllByUsername(preClient.getUsername());
        if (clients2.size()>1)
            throw new ClientAlreadyExist("Логин уже занят. ");

        preClient.setUsername(clientView.getUsername());
        preClient.setPassword(bCryptPasswordEncoder.encode(clientView.getPassword()));
        preClient.setFirstName(clientView.getFirstName());
        preClient.setLastName(clientView.getLastName());
        preClient.setFatherName(clientView.getFatherName());
        preClient.setPassportNum(clientView.getPassportNum());
        preClient.setPassportSeria(clientView.getPassportSeria());

        return clientsRepository.save(preClient);
    }

    /* DELETE */
    public void deleteClientById(Long id) throws ClientNotFoundException {
        if (!clientsRepository.existsById(id))
            throw new ClientNotFoundException("Пользователя не существует с id: " + id);

        journalRepository.deleteRecordsByClientId(findClientById(id));
        clientsRepository.deleteById(id);
    }
    public void deleteClientsByFirstName(String firstName) throws ClientNotFoundException, ClientIncorrectData {
        if (firstName == null)
            throw new ClientIncorrectData("Пользователь не может быть без имени, удаление не произошло. ");
        if (!existByFirstName(firstName))
            throw new ClientNotFoundException("Нет такого клиента с именем: " + firstName+"удаление не произошло. ");
        List<Client> clients = clientsRepository.findAllByFirstName(firstName);

        for (Client c: clients)
            journalRepository.deleteRecordsByClientId(c);

        clientsRepository.deleteClientsByFirstName(firstName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientsRepository.getClientByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user: "+ username + "was not found!"));
    }
}
