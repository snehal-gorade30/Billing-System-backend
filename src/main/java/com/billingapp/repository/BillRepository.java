package com.billingapp.repository;

import com.billingapp.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    
    Optional<Bill> findByBillNumber(String billNumber);
    
    List<Bill> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Bill> findByType(String type);
    
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
    List<Bill> findBillsBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT b FROM Bill b WHERE b.type = 'CREDIT'")
    List<Bill> findCreditBills();
    
    List<Bill> findByOrderByBillDateDesc();
    
    @Query("SELECT b FROM Bill b WHERE " +
           "LOWER(b.billNumber) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(b.customerName) LIKE LOWER(concat('%', :query, '%')) OR " +
           "LOWER(b.phoneNumber) LIKE LOWER(concat('%', :query, '%')) " +
           "ORDER BY b.billDate DESC")
    List<Bill> searchBills(@Param("query") String query);
    
    @Query("SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.items ORDER BY b.billDate DESC")
    List<Bill> findAllWithItems();
    
    @Query("SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.items WHERE " +
           "LOWER(b.billNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.customerName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.phoneNumber) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY b.billDate DESC")
    List<Bill> searchBillsWithItemsByQuery(@Param("q") String query);
    
    @Query("SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.items WHERE " +
           "b.billDate >= :startDate AND b.billDate <= :endDate " +
           "ORDER BY b.billDate DESC")
    List<Bill> findBillsWithItemsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT DISTINCT b FROM Bill b LEFT JOIN FETCH b.items WHERE " +
           "b.type = :type ORDER BY b.billDate DESC")
    List<Bill> findBillsWithItemsByType(@Param("type") String type);
}
