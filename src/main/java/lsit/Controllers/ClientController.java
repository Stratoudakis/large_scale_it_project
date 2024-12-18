package lsit.Controllers;

import lsit.Models.Client;
import lsit.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SalesController salesController;

    // Welcome message for the Client API
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')") // Allow access only to users with ROLE_CUSTOMER
    public String getClientBase() {
        return "Welcome to the Client API!";
    }

    // Method to create a new client
    @PostMapping("/createClient")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String createClient(@RequestBody Client client) {
        try {
            clientRepository.addClient(client);
            return "Client created successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while creating the client. Please try again later.";
        }
    }

    // Method to retrieve a client by its UUID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String getClient(@PathVariable UUID id) {
        try {
            Client client = clientRepository.getClient(id);
            if (client == null) {
                return "Error: Client not found!";
            }
            return "Client found: " + client.toString();
        } catch (Exception e) {
            return "Error: Something went wrong while retrieving the client. Please try again later.";
        }
    }

    // Method to update an existing client
    @PutMapping("/updateClient")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String updateClient(@RequestBody Client client) {
        try {
            Client existingClient = clientRepository.getClient(client.getId());
            if (existingClient == null) {
                return "Error: Client not found to update!";
            }
            clientRepository.updateClient(client);
            return "Client updated successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while updating the client. Please try again later.";
        }
    }

    // Method to delete a client by its UUID
    @DeleteMapping("/deleteClient/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String deleteClient(@PathVariable UUID id) {
        try {
            Client client = clientRepository.getClient(id);
            if (client == null) {
                return "Error: Client not found to delete!";
            }
            clientRepository.removeClient(id);
            return "Client deleted successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while deleting the client. Please try again later.";
        }
    }

    // Method to list all clients
    @GetMapping("/listClients")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Client> listClients() {
        try {
            return clientRepository.listClients();
        } catch (Exception e) {
            return List.of();
        }
    }

    // Method to submit feedback from the client to SalesController
    @PostMapping("/submitFeedback/{clientId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String submitFeedback(@PathVariable UUID clientId, @RequestBody String feedback) {
        try {
            Client client = clientRepository.getClient(clientId);
            if (client == null) {
                return "Error: Client not found!";
            }
            System.out.println("Feedback received from " + client.getName() + ": " + feedback);
            return salesController.evaluateFeedback(clientId, feedback);
        } catch (Exception e) {
            return "Error: Something went wrong while submitting feedback. Please try again later.";
        }
    }
}
