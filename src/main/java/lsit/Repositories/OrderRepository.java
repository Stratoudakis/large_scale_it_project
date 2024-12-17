package lsit.Repositories;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lsit.Models.Order;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    final String BUCKET = "clothes-bucket";  // Your GCP bucket name
    final String ORDERS_FOLDER = "orders/";  // Folder in the bucket for orders data

    Storage storage;

    public OrderRepository() {
        // Initialize the Google Cloud Storage client
        storage = StorageOptions.getDefaultInstance().getService();
    }

    // Add a new order object to Google Cloud Storage
    public void addOrder(Order order) {
        try {
            order.setId(UUID.randomUUID());  // Assign a unique ID to the order

            ObjectMapper om = new ObjectMapper();
            String orderJson = om.writeValueAsString(order);  // Convert order to JSON

            // Create a BlobId (unique identifier for the object in GCP Storage)
            BlobId blobId = BlobId.of(BUCKET, ORDERS_FOLDER + order.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();

            // Upload the order as a JSON object in GCP Storage
            storage.create(blobInfo, orderJson.getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Get an order by its UUID
    public Order getOrder(UUID id) {
        try {
            // Retrieve the order from Google Cloud Storage using its UUID
            Blob blob = storage.get(BlobId.of(BUCKET, ORDERS_FOLDER + id.toString() + ".json"));

            if (blob == null) {
                return null;  // If the file does not exist
            }

            // Read the content of the blob (order data)
            byte[] content = blob.getContent();

            // Convert the byte array back into an Order object
            ObjectMapper om = new ObjectMapper();
            return om.readValue(content, Order.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Remove an order by its UUID
    public void removeOrder(UUID id) {
        // Delete the order from Google Cloud Storage
        storage.delete(BlobId.of(BUCKET, ORDERS_FOLDER + id.toString() + ".json"));
    }

    // Update an existing order object
    public void updateOrder(Order order) {
        try {
            // Retrieve the existing order first
            Order existingOrder = this.getOrder(order.getId());
            if (existingOrder == null) return;  // If the order does not exist, don't update

            // Convert the updated order to JSON
            ObjectMapper om = new ObjectMapper();
            String orderJson = om.writeValueAsString(order);

            // Create or overwrite the order in Google Cloud Storage
            BlobId blobId = BlobId.of(BUCKET, ORDERS_FOLDER + order.getId().toString() + ".json");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
            storage.create(blobInfo, orderJson.getBytes());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // List all orders
    public List<Order> listOrders() {
        List<Order> orders = new ArrayList<>();

        // List objects (order files) under the specified prefix (folder)
        Iterable<Blob> objects = storage.list(BUCKET, Storage.BlobListOption.prefix(ORDERS_FOLDER)).iterateAll();

        // For each object (order file), retrieve the content and map it to an Order object
        for (Blob object : objects) {
            try {
                byte[] content = object.getContent();
                ObjectMapper om = new ObjectMapper();
                Order order = om.readValue(content, Order.class);
                orders.add(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return orders;
    }
}
