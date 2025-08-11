// ReceiptRepository.java
package za.co.tradelink.fiscal.repository;

import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByStatus(ReceiptStatus status);
    
    @Query("SELECT r FROM Receipt r WHERE (:status IS NULL OR r.status = :status) " +
           "AND (:fromDate IS NULL OR r.timestamp >= :fromDate) " +
           "AND (:toDate IS NULL OR r.timestamp <= :toDate)")
    List<Receipt> findByStatusAndDateRange(
            @Param("status") ReceiptStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);
}