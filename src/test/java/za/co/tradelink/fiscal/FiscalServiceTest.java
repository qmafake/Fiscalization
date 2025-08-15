package za.co.tradelink.fiscal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.exception.FiscalizationException;
import za.co.tradelink.fiscal.exception.ReceiptNotFoundException;
import za.co.tradelink.fiscal.mapper.FiscalReceiptMapper;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptItem;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.repository.ReceiptRepository;
import za.co.tradelink.fiscal.service.FiscalService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiscalServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private FiscalService fiscalService;

    @InjectMocks
    private FiscalReceiptMapper fiscalReceiptMapper;

    @Test
    void fiscalizeReceipt_shouldFiscalizeSuccessfully() {
        // Arrange
        Receipt receipt = createPendingReceipt();
        when(receiptRepository.findById(1L)).thenReturn(Optional.of(receipt));
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock the simulation to return success
        FiscalService spyService = spy(fiscalService);
        doReturn(true).when(spyService).simulateFiscalization();

        // Act
        Receipt result = spyService.fiscalizeReceipt(1L);

        // Assert
        assertEquals(ReceiptStatus.FISCALIZED, result.getStatus());
        assertNotNull(result.getFiscalCode());
        assertNotNull(result.getFiscalizedAt());
        verify(receiptRepository, times(1)).save(receipt);
    }

    @Test
    void fiscalizeReceipt_shouldThrowWhenFiscalizationFails() {
        // Arrange
        Receipt receipt = createPendingReceipt();
        when(receiptRepository.findById(1L)).thenReturn(Optional.of(receipt));

        // Mock the simulation to return failure
        FiscalService spyService = spy(fiscalService);
        doReturn(false).when(spyService).simulateFiscalization();

        // Act & Assert
        assertThrows(FiscalizationException.class, () -> spyService.fiscalizeReceipt(1L));

        // Verify the receipt status was updated before exception
        assertEquals(ReceiptStatus.FISCALIZATION_FAILED, receipt.getStatus());

        // Verify save was not called (since exception was thrown)
        verify(receiptRepository, never()).save(any());
    }

    @Test
    void fiscalizeReceipt_shouldThrowWhenReceiptNotFound() {
        // Arrange
        when(receiptRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReceiptNotFoundException.class, () -> fiscalService.fiscalizeReceipt(1L));
        verify(receiptRepository, never()).save(any());
    }

    @Test
    void mapToFiscalReceiptDto_shouldConvertCorrectly() {
        // Arrange
        Receipt receipt = createFiscalizedReceipt();

        // Act
        FiscalReceiptDto dto = fiscalReceiptMapper.mapToFiscalReceiptDto(receipt);

        // Assert
        assertNotNull(dto);
        assertEquals(receipt.getFiscalCode(), dto.getFiscalCode());
        assertEquals(receipt.getReceiptNumber(), dto.getReceiptNumber());
        assertEquals(2, dto.getLineItems().size());
    }

    private Receipt createPendingReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1L);
        receipt.setReceiptNumber("RCP-123");
        receipt.setStatus(ReceiptStatus.PENDING_FISCALIZATION);
        return receipt;
    }

    private Receipt createFiscalizedReceipt() {
        Receipt receipt = createPendingReceipt();
        receipt.setStatus(ReceiptStatus.FISCALIZED);
        receipt.setFiscalCode(UUID.randomUUID().toString());
        receipt.setFiscalizedAt(LocalDateTime.now());

        ReceiptItem item1 = new ReceiptItem();
        item1.setName("Sunlight");
        item1.setQty(1);
        item1.setUnitPrice(BigDecimal.valueOf(10.00));
        item1.setVat(BigDecimal.valueOf(1.50));

        ReceiptItem item2 = new ReceiptItem();
        item2.setName("Alabama Rice");
        item2.setQty(2);
        item2.setUnitPrice(BigDecimal.valueOf(15.00));
        item2.setVat(BigDecimal.valueOf(2.25));

        receipt.setItems(List.of(item1,item2));

        return receipt;
    }
}