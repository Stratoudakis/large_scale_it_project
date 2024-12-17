package lsit.Controllers;

import lsit.Models.Client;
import lsit.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Method to create a new client
    @PostMapping("/createClient")
    public String createClient(@RequestBody Client client) {
        try {
            // Call the repository to store the client in GCP
            clientRepository.addClient(client);
            return "Client created successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while creating the client. Please try again later.";
        }
    }

    // Method to retrieve a client by its UUID
    @GetMapping("/{id}")
    public String getClient(@PathVariable UUID id) {
        try {
            // Retrieve the client from GCP
            Client client = clientRepository.getClient(id);
            
            if (client == null) {
                return "Error: Client not found!";
            }
            
            return "Client found: " + client.toString();  // Return the client details (customize this as per your needs)
        } catch (Exception e) {
            return "Error: Something went wrong while retrieving the client. Please try again later.";
        }
    }

    // Method to update an existing client
    @PutMapping("/updateClient")
    public String updateClient(@RequestBody Client client) {
        try {
            // Check if the client exists
            Client existingClient = clientRepository.getClient(client.getId());
            if (existingClient == null) {
                return "Error: Client not found to update!";
            }

            // Update the client
            clientRepository.updateClient(client);
            return "Client updated successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while updating the client. Please try again later.";
        }
    }

    // Method to delete a client by its UUID
    @DeleteMapping("/deleteClient/{id}")
    public String deleteClient(@PathVariable UUID id) {
        try {
            // Check if the client exists
            Client client = clientRepository.getClient(id);
            if (client == null) {
                return "Error: Client not found to delete!";
            }

            // Delete the client
            clientRepository.removeClient(id);
            return "Client deleted successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while deleting the client. Please try again later.";
        }
    }

    // Method to list all clients
    @GetMapping("/listClients")
    public List<Client> listClients() {
        try {
            return clientRepository.listClients();  // Retrieve all clients from GCP
        } catch (Exception e) {
            // Handle errors when listing clients
            return List.of();  // Return an empty list if an error occurs
        }
    }

    // Method to submit feedback from the client to SalesController
    @PostMapping("/submitFeedback/{clientId}")
    public String submitFeedback(@PathVariable UUID clientId, @RequestBody String feedback) {
        try {
            // Fetch client details using clientId
            Client client = clientRepository.getClient(clientId);
            if (client == null) {
                return "Error: Client not found!";
            }

            // Save the feedback (you can store it in a database or GCP if needed)
            System.out.println("Feedback received from " + client.getName() + ": " + feedback);

            // Send the feedback to the SalesController for evaluation
            return salesController.evaluateFeedback(clientId, feedback);  // Call SalesController's evaluation method

        } catch (Exception e) {
            return "Error: Something went wrong while submitting feedback. Please try again later.";
        }
    }
}
