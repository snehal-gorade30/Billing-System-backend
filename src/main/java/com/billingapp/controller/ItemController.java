package com.billingapp.controller;

import com.billingapp.entity.Item;
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
            
            Item savedItem = itemService.saveItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating item: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item itemDetails) {
        try {
            Item updatedItem = itemService.updateItem(id, itemDetails);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating item: " + e.getMessage());
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
    
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String q) {
        List<Item> items = itemService.searchItems(q);
        return ResponseEntity.ok(items);
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
}
