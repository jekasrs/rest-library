package com.smirnov.api.controllers;

import com.smirnov.api.entities.Client;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.ClientView;
import com.smirnov.api.services.ClientService;
import com.smirnov.api.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientRestController {
    private final ClientService clientService;
    private final JournalService journalService;

    @Autowired
    public ClientRestController(ClientService clientService, JournalService journalService) {
        this.clientService = clientService;
        this.journalService = journalService;
    }

    @PostMapping(value = "/client/", consumes = {"application/json"})
    public ResponseEntity registration(@RequestBody ClientView clientView) throws ClientException {
        clientService.createClient(clientView);
        return ResponseEntity.ok("Пользователь успешно добавлен");
    }

    @PostMapping(value = "/admin/", consumes = {"application/json"})
    public ResponseEntity addAdmin(@RequestBody ClientView clientView) throws ClientException {
        clientService.createAdmin(clientView);
        return ResponseEntity.ok("Пользователь успешно добавлен");
    }

    @GetMapping("/client/{id}")
    public ResponseEntity get(@PathVariable Long id) throws ClientException {
        ClientView client = clientService.findClientViewById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/client/{id}/fullInfo")
    public ResponseEntity getFullInfo(@PathVariable Long id) throws ClientException {
        Client client = clientService.findClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/client/")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName) throws ClientException {

        if (filter == null)
            throw new ClientException("Не передан параметр поиска");
        List<Client> clientList;
        switch (filter.toLowerCase()) {
            case "all":
                clientList = clientService.findAllClients();
                break;
            case "sorted":
                clientList = clientService.sortByFirstName();
                break;
            case "full_namesakes":
                clientList = clientService.findClientsByFirstNameAndLastName(firstName, lastName);
                break;
            default:
                throw new ClientException("Не передан параметр поиска");
        }
        return ResponseEntity.ok(clientList);

    }

    @GetMapping(value = "/client/{id}/fine")
    public ResponseEntity getFine(@PathVariable Long id) throws RecordException, ClientException {
        return ResponseEntity.ok(journalService.getFineByClient(id));
    }

    @PutMapping(value = "/client/{id}", consumes = {"application/json"})
    public ResponseEntity update(@RequestBody ClientView clientView, @PathVariable Long id) throws ClientException {
        clientService.updateClient(clientView, id);
        return ResponseEntity.ok("Пользователь успешно обновлен");
    }


    @DeleteMapping(value = "/client/{id}")
    public ResponseEntity delete(@PathVariable Long id) throws ClientException {
        clientService.deleteClientById(id);
        return ResponseEntity.ok("Клиент успешно удален");
    }

    @DeleteMapping(value = "/client/")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) String firstName) throws ClientException {
        if (filter == null)
            throw new ClientException("Не передан параметр поиска");

        switch (filter.toLowerCase()) {
            case "by_first_name":
                clientService.deleteClientsByFirstName(firstName);
                break;
            default:
                throw new ClientException("Не передан параметр поиска");
        }
        return ResponseEntity.ok("Пользователи успешно удалены");
    }
}
