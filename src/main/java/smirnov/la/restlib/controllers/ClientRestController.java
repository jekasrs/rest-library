package smirnov.la.restlib.controllers;

import smirnov.la.restlib.entities.Client;
import smirnov.la.restlib.exceptions.*;
import smirnov.la.restlib.models.ClientView;
import smirnov.la.restlib.services.ClientService;
import smirnov.la.restlib.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/client/")
    public Client registration(@RequestBody ClientView clientView) throws ClientException {
        return clientService.createClient(clientView);
    }

    @GetMapping("/client/{id}")
    public ClientView get(@PathVariable Long id) throws ClientException {
        return clientService.findClientViewById(id);
    }

    @GetMapping("/client/{id}/fullInfo")
    public Client getFullInfo(@PathVariable Long id) throws ClientException {
        return clientService.findClientById(id);
    }

    @GetMapping("/client/")
    public List<Client> getWithFilter(@RequestParam String filter,
                                      @RequestParam(required = false) String firstName,
                                      @RequestParam(required = false) String lastName) throws ClientException {

        if (filter == null)
            throw new ClientException("Не передан параметр поиска");

        switch (filter.toLowerCase()) {
            case "all":
                return clientService.findAllClients();
            case "sorted":
                return clientService.sortByFirstName();
            case "full_namesakes":
                return clientService.findClientsByFirstNameAndLastName(firstName, lastName);
            default:
                throw new ClientException("Не передан параметр поиска");
        }
    }

    @PutMapping(value = "/client/{id}")
    public Client update(@RequestBody ClientView clientView, @PathVariable Long id) throws ClientException {
        return clientService.updateClient(clientView, id);
    }


    @DeleteMapping(value = "/client/{id}")
    public void delete(@PathVariable Long id) throws ClientException {
        clientService.deleteClientById(id);
    }

    @DeleteMapping(value = "/client/")
    public void deleteWithFilter(@RequestParam String filter,
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
    }
}