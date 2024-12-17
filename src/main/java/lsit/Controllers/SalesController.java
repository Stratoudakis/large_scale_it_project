package lsit.Controllers;

import lsit.Models.Order;
import lsit.Models.Product;
import lsit.Models.Client;
import lsit.Repositories.OrderRepository;
import lsit.Repositories.ProductRepository;
import lsit.Repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class SalesController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RestTemplate restTemplate;  // Injecting RestTemplate to make HTTP requests

    @PostMapping("/createOrder")
    public String createOrder(@RequestBody Order order) {
    
        // Step 1: Validate the Product and Client (as you already did)
        if (order.getProduct() == null || order.getProduct().getId() == null) {
            return "Error: Invalid product ID!";
        }
        if (order.getClientId() == null) {
            return "Error: Client ID cannot be null!";
        }
    
        try {
            // Step 2: Assign an ID to the order before proceeding with further checks
            order.setId(UUID.randomUUID());  // Assign a unique ID to the order
            orderRepository.addOrder(order);  // Store the order (if you have a repository for order persistence)
    
            // Step 3: Check if the product exists in the repository
            Product product = productRepository.getProduct(order.getProduct().getId());
            
            if (product == null) {
                return "Error: Product is out of stock. Try finding another product you may like!";
            }
    
            // Step 4: Check if enough stock is available for the order
            if (product.getStockQuantity() < order.getQuantity()) {
                return "Error: Insufficient stock for the product. Try finding another product you may like!";
            }
    
            // Step 5: Reduce the stock quantity of the product
            product.setStockQuantity(product.getStockQuantity() - order.getQuantity());
            productRepository.updateProduct(product);  // Save the updated product with reduced stock
    
            // Step 6: Link the order to a client (set the clientId in the order)
            Client client = clientRepository.getClient(order.getClientId());
            if (client == null) {
                return "Error: Client not found!";
            }
            order.setClientId(client.getId());  // Link the client to the order
    
            // Step 7: Perform credit check using the newly assigned order ID
            String creditUrl = "http://localhost:8080/credit/checkCredit/" + order.getId();
            String creditResponse = restTemplate.postForObject(creditUrl, null, String.class);
    
            if (creditResponse.contains("Credit Denied")) {
                return "Order cannot proceed due to credit denial: " + creditResponse;
            }
    
            // Step 8: Send the credit issue report to the client and get the message
            String creditReportMessage = sendCreditReportToClient(client, creditResponse);
    
            // Step 9: Notify the WarehouseController to process the order
            String warehouseUrl = "http://localhost:8080/warehouse/processOrder/" + order.getId();
            String warehouseResponse = restTemplate.postForObject(warehouseUrl, null, String.class);
    
            // Return success message with credit report and shipping confirmation
            return "Order created successfully! " + warehouseResponse + "\n" +
                   creditReportMessage;

        } catch (Exception e) {
            return "Error: Something went wrong while processing the order. Please try again later.";
        }
    }

    // Method to send the credit report to the client and return the message
    private String sendCreditReportToClient(Client client, String creditReport) {
        String message = "Sending credit report to client: " + client.getName() + "\n" +
                        "Credit report: " + creditReport;
        System.out.println(message);  
        return message;  
    }

     // Method to evaluate the feedback (Quality Evaluation)
    @PostMapping("/evaluateFeedback/{clientId}")
    public String evaluateFeedback(@PathVariable UUID clientId, @RequestBody String feedback) {
        try {
            // Fetch client details using clientId
            Client client = clientRepository.getClient(clientId);
            if (client == null) {
                return "Error: Client not found!";
            }

            // Save the feedback (you can store it in a database or GCP if needed)
            System.out.println("Feedback received from " + client.getName() + ": " + feedback);

            // Evaluate the feedback
            String evaluationResult = performQualityEvaluation(feedback);

            // Send feedback evaluation result to the client (this can be an email or notification)
            return "Feedback received from " + client.getName() + ": " + feedback + ". " + evaluationResult;
        } catch (Exception e) {
            return "Error: Something went wrong while evaluating the feedback. Please try again later.";
        }
    }

    // Method to perform the quality evaluation based on feedback
    private String performQualityEvaluation(String feedback) {
        // Simulate a basic quality evaluation process
        if (feedback.contains("good") || feedback.contains("excellent")) {
            return "Quality evaluation: Positive feedback received.";
        } else if (feedback.contains("bad") || feedback.contains("poor")) {
            return "Quality evaluation: Negative feedback received.";
        } else {
            return "Quality evaluation: Neutral feedback.";
        }
    }

    // Method to retrieve an order by its UUID
    @GetMapping("/getOrder/{id}")
    public String getOrder(@PathVariable UUID id) {
        try {
            // Retrieve the order using OrderRepository
            Order order = orderRepository.getOrder(id);
            
            if (order == null) {
                return "Error: Order not found!";
            }
            
            return "Order found: " + order.toString();  // Return the order details (you can customize this as per your needs)
        } catch (Exception e) {
            return "Error: Something went wrong while retrieving the order. Please try again later.";
        }
    }

    // Method to update an existing order
    @PutMapping("/updateOrder")
    public String updateOrder(@RequestBody Order order) {
        try {
            // Check if the order exists
            Order existingOrder = orderRepository.getOrder(order.getId());
            if (existingOrder == null) {
                return "Error: Order not found to update!";
            }

            // Update the order
            orderRepository.updateOrder(order);
            return "Order updated successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while updating the order. Please try again later.";
        }
    }

    // Method to delete an order by its UUID
    @DeleteMapping("/deleteOrder/{id}")
    public String deleteOrder(@PathVariable UUID id) {
        try {
            // Check if the order exists
            Order order = orderRepository.getOrder(id);
            if (order == null) {
                return "Error: Order not found to delete!";
            }

            // Delete the order
            orderRepository.removeOrder(id);
            return "Order deleted successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while deleting the order. Please try again later.";
        }
    }

    // Method to list all orders
    @GetMapping("/listOrders")
    public List<Order> listOrders() {
        try {
            return orderRepository.listOrders();  // Retrieve all orders from GCP
        } catch (Exception e) {
            // Handle errors when listing orders
            return List.of();  // Return an empty list if an error occurs
        }
    }

    // Method to check product availability before order creation
    @GetMapping("/checkAvailability/{productName}")
    public String checkProductAvailability(@PathVariable String productName) {
        try {
            Product product = productRepository.findByName(productName);  // Find product by name
            if (product == null) {
                return "Error: Product is out of stock. Try finding another product you may like!";
            }

            return "Product is available with " + product.getStockQuantity() + " items in stock.";
        } catch (Exception e) {
            return "Error: Something went wrong while checking availability. Please try again later.";
        }
    }
}
