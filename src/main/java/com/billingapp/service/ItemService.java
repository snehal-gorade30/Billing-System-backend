package com.billingapp.service;

import com.billingapp.entity.Item;
import com.billingapp.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    
    public Item saveItem(Item item) {
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
    
    public List<Item> searchItems(String searchTerm) {
        return itemRepository.searchItems(searchTerm);
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
}
