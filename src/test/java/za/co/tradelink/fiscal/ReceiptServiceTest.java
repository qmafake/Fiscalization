// ReceiptServiceTest.java
package za.co.tradelink.fiscal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.exception.ReceiptNotFoundException;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.repository.ReceiptRepository;
import za.co.tradelink.fiscal.service.ReceiptService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @Test
    void createReceipt_shouldSaveReceiptWithPendingStatus() {
        // Arrange
        PosReceiptDto dto = createSamplePosReceiptDto();
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Receipt result = receiptService.createReceipt(dto);

        // Assert
        assertNotNull(result);
        assertEquals(ReceiptStatus.PENDING_FISCALIZATION, result.getStatus());
        assertEquals("RCP-123", result.getReceiptNumber());
        assertEquals("CASH-01", result.getCashierId());
        assertEquals(2, result.getItems().size());
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    @Test
    void getReceiptById_shouldReturnReceiptWhenFound() {
        // Arrange
        Long receiptId = 1L;
        Receipt expected = new Receipt();
        expected.setId(receiptId);
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(expected));

        // Act
        Receipt result = receiptService.getReceiptById(receiptId);

        // Assert
        assertNotNull(result);
        assertEquals(receiptId, result.getId());
    }

    @Test
    void getReceiptById_shouldThrowWhenNotFound() {
        // Arrange
        Long receiptId = 1L;
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReceiptNotFoundException.class, () -> receiptService.getReceiptById(receiptId));
    }

    @Test
    void getAllReceipts_shouldReturnFilteredResults() {
        // Arrange
        ReceiptStatus status = ReceiptStatus.PENDING_FISCALIZATION;
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now();
        
        when(receiptRepository.findByStatusAndDateRange(status, fromDate, toDate))
            .thenReturn(List.of(new Receipt(), new Receipt()));

        // Act
        List<Receipt> results = receiptService.getAllReceipts(status, fromDate, toDate);

        // Assert
        assertEquals(2, results.size());
    }

    private PosReceiptDto createSamplePosReceiptDto() {
        PosReceiptDto dto = new PosReceiptDto();
        dto.setReceiptNumber("RCP-123");
        dto.setTimestamp(LocalDateTime.now());
        dto.setCashierId("CASH-01");
        dto.setBranchCode("BR-001");
        
        PosReceiptDto.BuyerDto buyer = new PosReceiptDto.BuyerDto();
        buyer.setTin("123456789");
        buyer.setName("Siya Ndlovu");
        dto.setBuyer(buyer);
        
        PosReceiptDto.ItemDto item1 = new PosReceiptDto.ItemDto();
        item1.setName("Sunlight");
        item1.setQty(1);
        item1.setUnitPrice(BigDecimal.valueOf(10.00));
        item1.setVat(BigDecimal.valueOf(1.50));
        
        PosReceiptDto.ItemDto item2 = new PosReceiptDto.ItemDto();
        item2.setName("Alabama Rice");
        item2.setQty(2);
        item2.setUnitPrice(BigDecimal.valueOf(15.00));
        item2.setVat(BigDecimal.valueOf(2.25));
        
        dto.setItems(List.of(item1, item2));
        dto.setTotal(BigDecimal.valueOf(45.00));
        dto.setCurrency("ZAR");
        
        return dto;
    }
}