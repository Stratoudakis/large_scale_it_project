package lsit.Models;

import java.util.UUID;

public class Order {

    private UUID id;
    private UUID clientId;  // Link to Client
    private Product product;  // Assuming one product per order for simplicity
    private int quantity;
    private double totalPrice;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClientId() { return clientId; }  // Getter for clientId
    public void setClientId(UUID clientId) { this.clientId = clientId; }  // Setter for clientId

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
