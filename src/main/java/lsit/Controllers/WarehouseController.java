package lsit.Controllers;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    // Base endpoint - accessible only to users with ROLE_WAREHOUSE
    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE')") // Restrict to users with ROLE_WAREHOUSE
    public String getWarehouseBase() {
        return "Welcome to the Warehouse API!";
    }

    // Method to process the order after successful creation
    @PostMapping("/processOrder/{orderId}")
    @PreAuthorize("hasRole('WAREHOUSE')") // Restrict access to ROLE_WAREHOUSE users
    public String processOrder(@PathVariable UUID orderId) {
        // Simulate the process of finding the item in the warehouse
        System.out.println("Processing order with ID: " + orderId);

        // Step 1: Finding the item in the warehouse
        boolean itemFound = true;  // Simulate the item is found
        if (!itemFound) {
            return "Error: Item not found in the warehouse.";
        }

        // Step 2: Packing the item
        System.out.println("Item is packed successfully.");

        // Step 3: Shipping the item to the customer
        System.out.println("Item is shipped successfully to the customer.");

        // Return success message
        return "Order " + orderId + " is processed: item found, packed, and shipped!";
    }

    // Example: Check warehouse status
    @GetMapping("/status")
    @PreAuthorize("hasRole('WAREHOUSE')") // Restrict access to ROLE_WAREHOUSE users
    public String checkWarehouseStatus() {
        return "The warehouse is operational.";
    }
}
