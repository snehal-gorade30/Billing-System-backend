package com.billingapp.service;

import com.billingapp.entity.Item;
import com.billingapp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final ItemRepository itemRepository;
    
    /**
     * Update inventory stock after a sale
     * @param itemId The ID of the item
     * @param quantitySold The quantity sold (positive number)
     * @throws RuntimeException if item not found or insufficient stock
     */
    @Transactional
    public void updateStockAfterSale(Long itemId, int quantitySold) {
        if (quantitySold <= 0) {
            throw new IllegalArgumentException("Quantity sold must be positive");
        }
        
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        
        int newStock = item.getCurrentStock() - quantitySold;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for item: " + item.getItemName() + 
                    ". Available: " + item.getCurrentStock() + ", Requested: " + quantitySold);
        }
        
        item.setCurrentStock(newStock);
        item.setUpdatedAt(java.time.LocalDateTime.now());
        itemRepository.save(item);
    }
    
    /**
     * Update inventory stock after a return/refund
     * @param itemId The ID of the item
     * @param quantityReturned The quantity returned (positive number)
     * @throws RuntimeException if item not found
     */
    @Transactional
    public void updateStockAfterReturn(Long itemId, int quantityReturned) {
        if (quantityReturned <= 0) {
            throw new IllegalArgumentException("Quantity returned must be positive");
        }
        
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        
        item.setCurrentStock(item.getCurrentStock() + quantityReturned);
        item.setUpdatedAt(java.time.LocalDateTime.now());
        itemRepository.save(item);
    }
    
    /**
     * Check if an item has sufficient stock
     * @param itemId The ID of the item
     * @param requiredQuantity The quantity needed
     * @return true if sufficient stock is available, false otherwise
     */
    public boolean hasSufficientStock(Long itemId, int requiredQuantity) {
        if (requiredQuantity <= 0) {
            return false;
        }
        
        return itemRepository.findById(itemId)
                .map(item -> item.getCurrentStock() >= requiredQuantity)
                .orElse(false);
    }
    
    /**
     * Get current stock level for an item
     * @param itemId The ID of the item
     * @return Current stock level
     * @throws RuntimeException if item not found
     */
    public int getCurrentStock(Long itemId) {
        return itemRepository.findById(itemId)
                .map(Item::getCurrentStock)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
    }
}
