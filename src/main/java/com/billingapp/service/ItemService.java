package com.billingapp.service;

import com.billingapp.dto.ItemQuantity;
import com.billingapp.entity.Item;
import com.billingapp.exception.InsufficientStockException;
import com.billingapp.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    
    @Autowired
    private ItemRepository itemRepository;
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }
    
    @Transactional
    public Item saveItem(Item item) {
        // Trim barcode and set to null if empty
        if (item.getBarcode() != null) {
            item.setBarcode(item.getBarcode().trim());
            if (item.getBarcode().isEmpty()) {
                item.setBarcode(null);
            } else if (itemRepository.existsByBarcode(item.getBarcode())) {
                throw new RuntimeException("Barcode already exists: " + item.getBarcode());
            }
        }
        
        // Validate item name uniqueness
        if (item.getId() == null && itemRepository.existsByItemNameIgnoreCase(item.getItemName())) {
            throw new RuntimeException("Item with name '" + item.getItemName() + "' already exists");
        }
        
        return itemRepository.save(item);
    }
    
    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        item.setItemName(itemDetails.getItemName());
        item.setCategory(itemDetails.getCategory());
        item.setPurchasePrice(itemDetails.getPurchasePrice());
        item.setMrp(itemDetails.getMrp());
        item.setSellPrice(itemDetails.getSellPrice());
        item.setMinSellPrice(itemDetails.getMinSellPrice());
        item.setCurrentStock(itemDetails.getCurrentStock());
        item.setMinStockLevel(itemDetails.getMinStockLevel());
        item.setUnit(itemDetails.getUnit());
        
        return itemRepository.save(item);
    }
    
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        itemRepository.delete(item);
    }
    
    public List<Item> searchItems(String query) {
        if (query == null || query.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        return itemRepository.searchByNameOrBarcode(query.toLowerCase());
    }
    
    public Optional<Item> getItemByBarcode(String barcode) {
        return itemRepository.findByBarcode(barcode);
    }
    
    public List<Item> getLowStockItems() {
        return itemRepository.findLowStockItems();
    }
    
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }
    
    public boolean itemNameExists(String itemName) {
        return itemRepository.existsByItemNameIgnoreCase(itemName);
    }
    
    public boolean barcodeExists(String barcode) {
        return barcode != null && itemRepository.existsByBarcode(barcode);
    }
    
    @Transactional
    public void reduceStock(Long itemId, int quantity) throws InsufficientStockException {
        Integer updated = itemRepository.reduceStock(itemId, quantity);
        if (updated == 0) {
            Integer currentStock = itemRepository.getCurrentStock(itemId);
            if (currentStock == null) {
                throw new RuntimeException("Item not found with id: " + itemId);
            }
            throw new InsufficientStockException("Insufficient stock for item " + itemId + 
                                              ". Requested: " + quantity + ", Available: " + currentStock);
        }
    }
    
    @Transactional
    public void updateStockForBill(List<ItemQuantity> items) throws InsufficientStockException {
        // First, verify all items have sufficient stock
        for (ItemQuantity itemQty : items) {
            Integer currentStock = itemRepository.getCurrentStock(itemQty.getItemId());
            if (currentStock == null) {
                throw new RuntimeException("Item not found with id: " + itemQty.getItemId());
            }
            if (currentStock < itemQty.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for item " + itemQty.getItemId() + 
                                                  ". Requested: " + itemQty.getQuantity() + 
                                                  ", Available: " + currentStock);
            }
        }
        
        // If all items have sufficient stock, update the stock
        for (ItemQuantity itemQty : items) {
            reduceStock(itemQty.getItemId(), itemQty.getQuantity());
        }
    }
}
