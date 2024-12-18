package lsit.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lsit.Models.Client;
import lsit.Models.Order;
import lsit.Models.Product;
import lsit.Repositories.ClientRepository;
import lsit.Repositories.OrderRepository;
import lsit.Repositories.ProductRepository;

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
    private RestTemplate restTemplate;

    // Welcome message
    @GetMapping
    @PreAuthorize("hasRole('SALES')") // Restrict access to users with ROLE_SALES
    public String getSalesBase() {
        return "Welcome to the Sales API!";
    }

    // Create a new order
    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('SALES')")
    public String createOrder(@RequestBody Order order) {
        if (order.getProduct() == null || order.getProduct().getId() == null) {
            return "Error: Invalid product ID!";
        }
        if (order.getClientId() == null) {
            return "Error: Client ID cannot be null!";
        }

        try {
            order.setId(UUID.randomUUID());
            orderRepository.addOrder(order);

            Product product = productRepository.getProduct(order.getProduct().getId());
            if (product == null) {
                return "Error: Product is out of stock.";
            }
            if (product.getStockQuantity() < order.getQuantity()) {
                return "Error: Insufficient stock for the product.";
            }

            product.setStockQuantity(product.getStockQuantity() - order.getQuantity());
            productRepository.updateProduct(product);

            Client client = clientRepository.getClient(order.getClientId());
            if (client == null) {
                return "Error: Client not found!";
            }

            String creditUrl = "http://localhost:8080/credit/checkCredit/" + order.getId();
            String creditResponse = restTemplate.postForObject(creditUrl, null, String.class);

            if (creditResponse.contains("Credit Denied")) {
                return "Order cannot proceed due to credit denial: " + creditResponse;
            }

            String warehouseUrl = "http://localhost:8080/warehouse/processOrder/" + order.getId();
            String warehouseResponse = restTemplate.postForObject(warehouseUrl, null, String.class);

            return "Order created successfully! " + warehouseResponse;
        } catch (Exception e) {
            return "Error: Something went wrong while processing the order.";
        }
    }

    // Evaluate feedback from the client
    @PostMapping("/evaluateFeedback/{clientId}")
    @PreAuthorize("hasRole('SALES')")
    public String evaluateFeedback(@PathVariable UUID clientId, @RequestBody String feedback) {
        try {
            Client client = clientRepository.getClient(clientId);
            if (client == null) {
                return "Error: Client not found!";
            }

            System.out.println("Feedback received from " + client.getName() + ": " + feedback);

            String evaluationResult = performQualityEvaluation(feedback);
            return "Feedback received from " + client.getName() + ": " + feedback + ". " + evaluationResult;
        } catch (Exception e) {
            return "Error: Something went wrong while evaluating the feedback.";
        }
    }

    private String performQualityEvaluation(String feedback) {
        if (feedback.contains("good") || feedback.contains("excellent")) {
            return "Quality evaluation: Positive feedback.";
        } else if (feedback.contains("bad") || feedback.contains("poor")) {
            return "Quality evaluation: Negative feedback.";
        } else {
            return "Quality evaluation: Neutral feedback.";
        }
    }

    // Retrieve an order by ID
    @GetMapping("/getOrder/{id}")
    @PreAuthorize("hasRole('SALES')")
    public String getOrder(@PathVariable UUID id) {
        Order order = orderRepository.getOrder(id);
        if (order == null) {
            return "Error: Order not found!";
        }
        return "Order found: " + order.toString();
    }

    // Update an existing order
    @PutMapping("/updateOrder")
    @PreAuthorize("hasRole('SALES')")
    public String updateOrder(@RequestBody Order order) {
        Order existingOrder = orderRepository.getOrder(order.getId());
        if (existingOrder == null) {
            return "Error: Order not found to update!";
        }
        orderRepository.updateOrder(order);
        return "Order updated successfully!";
    }

    // Delete an order by ID
    @DeleteMapping("/deleteOrder/{id}")
    @PreAuthorize("hasRole('SALES')")
    public String deleteOrder(@PathVariable UUID id) {
        Order order = orderRepository.getOrder(id);
        if (order == null) {
            return "Error: Order not found to delete!";
        }
        orderRepository.removeOrder(id);
        return "Order deleted successfully!";
    }

    // List all orders
    @GetMapping("/listOrders")
    @PreAuthorize("hasRole('SALES')")
    public List<Order> listOrders() {
        return orderRepository.listOrders();
    }

    // Check product availability
    @GetMapping("/checkAvailability/{productName}")
    @PreAuthorize("hasRole('SALES')")
    public String checkProductAvailability(@PathVariable String productName) {
        Product product = productRepository.findByName(productName);
        if (product == null) {
            return "Error: Product is out of stock.";
        }
        return "Product is available with " + product.getStockQuantity() + " items in stock.";
    }
}
