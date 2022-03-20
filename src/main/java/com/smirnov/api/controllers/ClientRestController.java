package com.smirnov.api.controllers;

import com.smirnov.api.entities.Client;
import com.smirnov.api.exceptions.*;
import com.smirnov.api.models.ClientView;
import com.smirnov.api.services.ClientService;
import com.smirnov.api.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientRestController {
    private final ClientService clientService;
    private final JournalService journalService;

    @Autowired
    public ClientRestController(ClientService clientService, JournalService journalService) {
        this.clientService = clientService;
        this.journalService = journalService;
    }

    @PostMapping(value = "/", consumes = {"application/json"})
    public ResponseEntity registration(@RequestBody ClientView clientView) {
        try {
            clientService.createClient(new Client(clientView));
            return ResponseEntity.ok("Пользователь успешно добавлен");
        } catch (ClientBlankNameException | ClientIllegalSymbols | ClientAlreadyExist e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PostMapping(value = "/reg_admin", consumes = {"application/json"})
    public ResponseEntity addAdmin(@RequestBody ClientView clientView) {
        try {
            clientService.createAdmin(new Client(clientView));
            return ResponseEntity.ok("Пользователь успешно добавлен");
        } catch (ClientBlankNameException | ClientIllegalSymbols | ClientAlreadyExist e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Long id) {
        try {
            ClientView clientView = new ClientView(clientService.findClientById(id));
            return ResponseEntity.ok(clientView);
        } catch (ClientNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping("/full_info/{id}")
    public ResponseEntity getFullInfo(@PathVariable Long id) {
        try {
            Client client = clientService.findClientById(id);
            return ResponseEntity.ok(client);
        } catch (ClientNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping("/filter")
    public ResponseEntity getWithFilter(@RequestParam String filter,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName
                                        ) {
        try {

            if (filter == null)
                throw new FilterNotFound("Не передан параметр поиска");

            List<Client> clientList;
            switch (filter.toLowerCase()) {
                case "all":
                    clientList = clientService.findAllClients();
                    break;
                case "nopassport":
                    clientList = clientService.findClientsByPassportSeriaNullAndPassportNumNull();
                    break;
                case "sorted":
                    clientList = clientService.sortByFirstName();
                    break;
                case "namesakes":
                    clientList = clientService.findClientsByFirstNameAndLastName(firstName, lastName);
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
            return ResponseEntity.ok(clientList.stream().map(ClientView::new).collect(Collectors.toList()));
        } catch (ClientBlankNameException | FilterNotFound e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @GetMapping(value = "/{id}/fine")
    public ResponseEntity getFine(@PathVariable Long id) {
        try {
            Client client = clientService.findClientById(id);
            return ResponseEntity.ok(journalService.getFineByClient(client));
        } catch (RecordIllegalOptions e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @PutMapping(value = "/{id}", consumes = {"application/json"})
    public ResponseEntity update(@RequestBody Client client, @PathVariable Long id) {
        try {
            clientService.updateClient(client, id);
            return ResponseEntity.ok("Пользователь успешно обновлен");
        } catch (ClientNotFoundException | ClientBlankNameException | ClientAlreadyExist | ClientIllegalSymbols e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            clientService.deleteClientById(id);
            return ResponseEntity.ok("Клиент успешно удален");
        } catch (ClientNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }

    @DeleteMapping(value = "/filter")
    public ResponseEntity deleteWithFilter(@RequestParam String filter,
                                           @RequestParam(required = false) String firstName) {
        try {
            if (filter == null)
                throw new FilterNotFound("Не передан параметр поиска");
            switch (filter.toLowerCase()) {
                case "namesakes":
                    clientService.deleteClientsByFirstName(firstName);
                    break;
                case "without_passport":
                    clientService.deleteClientsByPassportSeriaIsNullAndPassportNumIsNull();
                    break;
                default:
                    throw new FilterNotFound("Не передан параметр поиска");
            }
            return ResponseEntity.ok("Пользователи успешно удалены");
        } catch (ClientNotFoundException | FilterNotFound | ClientBlankNameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Неизвестная ошибка");
        }
    }
}
