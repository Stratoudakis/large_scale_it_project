package lsit.Controllers;

import lsit.Models.Product;
import lsit.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Method to create a new product
    @PostMapping("/createProduct")
    public String createProduct(@RequestBody Product product) {
        try {
            // Call the repository to store the product in GCP
            productRepository.addProduct(product);
            return "Product created successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while creating the product. Please try again later.";
        }
    }

    // Method to retrieve a product by its UUID
    @GetMapping("/{id}")
    public String getProduct(@PathVariable UUID id) {
        try {
            // Retrieve the product from GCP
            Product product = productRepository.getProduct(id);
            
            if (product == null) {
                return "Error: Product not found!";
            }
            
            return "Product found: " + product.toString();  // Return the product details (customize this as per your needs)
        } catch (Exception e) {
            return "Error: Something went wrong while retrieving the product. Please try again later.";
        }
    }

    // Method to update an existing product
    @PutMapping("/updateProduct")
    public String updateProduct(@RequestBody Product product) {
        try {
            // Check if the product exists
            Product existingProduct = productRepository.getProduct(product.getId());
            if (existingProduct == null) {
                return "Error: Product not found to update!";
            }

            // Update the product
            productRepository.updateProduct(product);
            return "Product updated successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while updating the product. Please try again later.";
        }
    }

    // Method to delete a product by its UUID
    @DeleteMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        try {
            // Check if the product exists
            Product product = productRepository.getProduct(id);
            if (product == null) {
                return "Error: Product not found to delete!";
            }

            // Delete the product
            productRepository.removeProduct(id);
            return "Product deleted successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while deleting the product. Please try again later.";
        }
    }

    // Method to list all products
    @GetMapping("/listProducts")
    public List<Product> listProducts() {
        try {
            return productRepository.listProducts();  
        } catch (Exception e) {
            
            return List.of();  
        }
    }
}
