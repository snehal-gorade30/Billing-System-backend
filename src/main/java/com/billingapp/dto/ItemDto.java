package com.billingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDto {
    private Long id;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    private String category;
    
    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    private BigDecimal purchasePrice;
    
    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "MRP must be greater than 0")
    private BigDecimal mrp;
    
    @NotNull(message = "Sell price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sell price must be greater than 0")
    private BigDecimal sellPrice;
    
    @DecimalMin(value = "0.0", message = "Min sell price must be greater than or equal to 0")
    private BigDecimal minSellPrice;
    
    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be greater than or equal to 0")
    private Integer currentStock;
    
    @NotNull(message = "Min stock level is required")
    @Min(value = 0, message = "Min stock level must be greater than or equal to 0")
    private Integer minStockLevel;
    
    @NotBlank(message = "Unit is required")
    private String unit;
    
    private String barcode;
    
    // Helper method to check if stock is low
    public boolean isLowStock() {
        return currentStock <= minStockLevel;
    }
    
    // Calculate stock value
    public BigDecimal getStockValue() {
        return purchasePrice.multiply(new BigDecimal(currentStock));
    }
}
