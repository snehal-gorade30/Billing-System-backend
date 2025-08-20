package com.billingapp.dto;

public class ItemQuantity {
    private Long itemId;
    private int quantity;

    // Default constructor for JSON deserialization
    public ItemQuantity() {}

    public ItemQuantity(Long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
