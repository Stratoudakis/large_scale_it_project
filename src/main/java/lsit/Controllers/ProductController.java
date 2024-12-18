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

import lsit.Models.Product;
import lsit.Repositories.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @PreAuthorize("hasRole('SALES')") // Restrict access to users with HAS_SALES role
    public String getProductsBase() {
        return "Welcome to the Products API!";
    }

    @PostMapping("/createProduct")
    @PreAuthorize("hasRole('SALES')") 
    public String createProduct(@RequestBody Product product) {
        try {
            productRepository.addProduct(product);
            return "Product created successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while creating the product. Please try again later.";
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SALES')") 
    public String getProduct(@PathVariable UUID id) {
        try {
            Product product = productRepository.getProduct(id);
            if (product == null) {
                return "Error: Product not found!";
            }
            return "Product found: " + product.toString();
        } catch (Exception e) {
            return "Error: Something went wrong while retrieving the product. Please try again later.";
        }
    }

    @PutMapping("/updateProduct")
    @PreAuthorize("hasRole('SALES')") 
    public String updateProduct(@RequestBody Product product) {
        try {
            Product existingProduct = productRepository.getProduct(product.getId());
            if (existingProduct == null) {
                return "Error: Product not found to update!";
            }
            productRepository.updateProduct(product);
            return "Product updated successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while updating the product. Please try again later.";
        }
    }

    @DeleteMapping("/deleteProduct/{id}")
    @PreAuthorize("hasRole('SALES')") 
    public String deleteProduct(@PathVariable UUID id) {
        try {
            Product product = productRepository.getProduct(id);
            if (product == null) {
                return "Error: Product not found to delete!";
            }
            productRepository.removeProduct(id);
            return "Product deleted successfully!";
        } catch (Exception e) {
            return "Error: Something went wrong while deleting the product. Please try again later.";
        }
    }

    @GetMapping("/listProducts")
    @PreAuthorize("hasRole('SALES')") 
    public List<Product> listProducts() {
        try {
            return productRepository.listProducts();
        } catch (Exception e) {
            return List.of();
        }
    }
}
