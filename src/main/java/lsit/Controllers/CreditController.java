package lsit.Controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;  // Import the OrderRepository
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lsit.Models.Client;
import lsit.Models.Order;
import lsit.Repositories.ClientRepository;
import lsit.Repositories.OrderRepository;

@RestController
@RequestMapping("/credit")
public class CreditController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;  // Injecting OrderRepository

    @GetMapping
    @PreAuthorize("hasRole('CREDIT')") 
    public String getCreditBase() {
        return "Welcome to the Credit API!";
    }

    // Method to check the credit of the client
    @PostMapping("/checkCredit/{orderId}")
    @PreAuthorize("hasRole('CREDIT')") 
    public String checkCredit(@PathVariable UUID orderId) {
        // Retrieve the order using OrderRepository
        Order order = orderRepository.getOrder(orderId);  // Get the order by orderId
        
        if (order == null) {
            return "Error: Order not found!";
        }

        // Fetch client details using the clientId from the order
        Client client = clientRepository.getClient(order.getClientId());
        
        if (client == null) {
            return "Error: Client not found!";
        }

        // Check if the credit score is above a certain threshold (e.g., 600)
        if (client.getCreditScore() >= 600) {
            // Credit is approved
            double creditIssueReport = calculateCreditReport(client);
            return "Credit Approved. Credit report: " + creditIssueReport;
        } else {
            // Credit is denied
            return "Credit Denied. Please contact customer service.";
        }
    }
    
    private double calculateCreditReport(Client client) {
        // For simplicity, we just return a simple formula for now
        return client.getCreditScore() * 0.1;
    }
}
