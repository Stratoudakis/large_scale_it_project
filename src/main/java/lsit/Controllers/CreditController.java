package lsit.Controllers;

import java.util.UUID;

import lsit.Models.Order;
import lsit.Models.Client;
import lsit.Repositories.ClientRepository;
import lsit.Repositories.OrderRepository;  // Import the OrderRepository

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credit")
public class CreditController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;  // Injecting OrderRepository

    // Method to check the credit of the client
    @PostMapping("/checkCredit/{orderId}")
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
