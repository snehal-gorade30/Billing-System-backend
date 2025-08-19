package com.billingapp.repository;

import com.billingapp.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    Optional<Bill> findByBillNumber(String billNumber);
    
    List<Bill> findByCustomerNameContainingIgnoreCase(String customerName);
    
    List<Bill> findByType(String type);
    
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate ORDER BY b.billDate DESC")
    List<Bill> findBillsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Bill b WHERE b.type = 'credit' ORDER BY b.billDate DESC")
    List<Bill> findCreditBills();
    
    List<Bill> findByOrderByBillDateDesc();
}
