package lsit.Models;

import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private String kind;  // E.g., T-Shirt, Sweater, etc.
    private double price;
    private int stockQuantity;

    // Getters and Setters
    public UUID getId() { return id; }
    
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}
