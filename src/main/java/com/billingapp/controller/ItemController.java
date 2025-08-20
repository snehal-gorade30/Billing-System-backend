package com.billingapp.controller;

import com.billingapp.dto.ItemQuantity;
import com.billingapp.entity.Item;
import com.billingapp.exception.InsufficientStockException;
import com.billingapp.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class ItemController {
    
    @Autowired
    private ItemService itemService;
    
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item) {
        try {
            // Check if item name already exists
            if (itemService.itemNameExists(item.getItemName())) {
                return ResponseEntity.badRequest()
                    .body("Item with name '" + item.getItemName() + "' already exists");
            }
            
            // Check if barcode already exists
            if (item.getBarcode() != null && !item.getBarcode().trim().isEmpty() && 
                itemService.barcodeExists(item.getBarcode())) {
                return ResponseEntity.badRequest()
                    .body("Item with barcode '" + item.getBarcode() + "' already exists");
            }
            
            Item savedItem = itemService.saveItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating item: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam(required = false) String query) {
        List<Item> items = itemService.searchItems(query);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Item> getItemByBarcode(@PathVariable String barcode) {
        Optional<Item> item = itemService.getItemByBarcode(barcode);
        return item.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item itemDetails) {
        try {
            Item existingItem = itemService.getItemById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
                
            // Check if barcode is being changed and already exists
            if (itemDetails.getBarcode() != null && !itemDetails.getBarcode().equals(existingItem.getBarcode()) && 
                itemService.barcodeExists(itemDetails.getBarcode())) {
                return ResponseEntity.badRequest()
                    .body("Another item with barcode '" + itemDetails.getBarcode() + "' already exists");
            }
            
            // Update item details
            existingItem.setItemName(itemDetails.getItemName());
            existingItem.setBarcode(itemDetails.getBarcode());
            existingItem.setCategory(itemDetails.getCategory());
            existingItem.setPurchasePrice(itemDetails.getPurchasePrice());
            existingItem.setMrp(itemDetails.getMrp());
            existingItem.setSellPrice(itemDetails.getSellPrice());
            existingItem.setMinSellPrice(itemDetails.getMinSellPrice());
            existingItem.setCurrentStock(itemDetails.getCurrentStock());
            existingItem.setMinStockLevel(itemDetails.getMinStockLevel());
            existingItem.setUnit(itemDetails.getUnit());
            
            Item updatedItem = itemService.saveItem(existingItem);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating item: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateItemStock(@PathVariable Long id, @RequestBody StockUpdateRequest request) {
        try {
            Item existingItem = itemService.getItemById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
            
            existingItem.setCurrentStock(request.getCurrentStock());
            Item updatedItem = itemService.saveItem(existingItem);
            
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating stock: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<Item>> getLowStockItems() {
        List<Item> lowStockItems = itemService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable String category) {
        List<Item> items = itemService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/update-stock")
    public ResponseEntity<?> updateStockForBill(@Valid @RequestBody List<ItemQuantity> itemQuantities) {
        try {
            itemService.updateStockForBill(itemQuantities);
            return ResponseEntity.ok().build();
        } catch (InsufficientStockException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error updating stock: " + e.getMessage());
        }
    }
    
    // Inner class for stock update request
    public static class StockUpdateRequest {
        private int currentStock;
        
        public int getCurrentStock() {
            return currentStock;
        }
        
        public void setCurrentStock(int currentStock) {
            this.currentStock = currentStock;
        }
    }
}
