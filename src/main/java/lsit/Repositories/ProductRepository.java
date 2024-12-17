package lsit.Repositories;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lsit.Models.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository {

    final String BUCKET = "clothes-bucket";  // Your GCP bucket name
    final String PRODUCTS_FOLDER = "products/";  // Folder in the bucket for products data

    Storage storage;

    public ProductRepository() {
        // Initialize the Google Cloud Storage client
        storage = StorageOptions.getDefaultInstance().getService();
    }

    // Add a new product object to Google Cloud Storage
    public void addProduct(Product product) {
        try {
            product.setId(UUID.randomUUID());  // Assign a unique ID to the product

            ObjectMapper om = new ObjectMapper();
            String productJson = om.writeValueAsString(product);  // Convert product to JSON

            // Create a BlobId (unique identifier for the object in GCP Storage)
            BlobId blobId = BlobId.of(BUCKET, PRODUCTS_FOLDER + product.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();

            // Upload the product as a JSON object in GCP Storage
            storage.create(blobInfo, productJson.getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Get a product by its UUID
    public Product getProduct(UUID id) {
        try {
            // Retrieve the product from Google Cloud Storage using its UUID
            Blob blob = storage.get(BlobId.of(BUCKET, PRODUCTS_FOLDER + id.toString() + ".json"));
            if (blob == null) {
                System.out.println("Product with ID " + id + " not found in GCP storage.");
                return null;  // If the file does not exist
            }
    
            // Read the content of the blob (product data)
            byte[] content = blob.getContent();
    
            // Convert the byte array back into a Product object
            ObjectMapper om = new ObjectMapper();
            return om.readValue(content, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Find a product by its name
    public Product findByName(String name) {
        try {
            // List all product files under the specified prefix (folder)
            Iterable<Blob> objects = storage.list(BUCKET, Storage.BlobListOption.prefix(PRODUCTS_FOLDER)).iterateAll();

            // For each object (product file), check if it matches the name
            for (Blob object : objects) {
                byte[] content = object.getContent();
                ObjectMapper om = new ObjectMapper();
                Product product = om.readValue(content, Product.class);

                // Check if the product name matches
                if (product.getName().equalsIgnoreCase(name)) {
                    return product;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  // If no product with the specified name is found
    }

    // Remove a product by its UUID
    public void removeProduct(UUID id) {
        // Delete the product from Google Cloud Storage
        storage.delete(BlobId.of(BUCKET, PRODUCTS_FOLDER + id.toString() + ".json"));
    }

    // Update an existing product object
    public void updateProduct(Product product) {
        try {
            // Retrieve the existing product first
            Product existingProduct = this.getProduct(product.getId());
            if (existingProduct == null) return;  // If the product does not exist, don't update

            // Convert the updated product to JSON
            ObjectMapper om = new ObjectMapper();
            String productJson = om.writeValueAsString(product);

            // Create or overwrite the product in Google Cloud Storage
            BlobId blobId = BlobId.of(BUCKET, PRODUCTS_FOLDER + product.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
            storage.create(blobInfo, productJson.getBytes());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // List all products
    public List<Product> listProducts() {
        List<Product> products = new ArrayList<>();

        // List objects (product files) under the specified prefix (folder)
        Iterable<Blob> objects = storage.list(BUCKET, Storage.BlobListOption.prefix(PRODUCTS_FOLDER)).iterateAll();

        // For each object (product file), retrieve the content and map it to a Product object
        for (Blob object : objects) {
            try {
                byte[] content = object.getContent();
                ObjectMapper om = new ObjectMapper();
                Product product = om.readValue(content, Product.class);
                products.add(product);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return products;
    }
}
