package za.co.tradelink.fiscal.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import za.co.tradelink.fiscal.config.TestConfig;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.mapper.FiscalReceiptMapper;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.service.FiscalService;
import za.co.tradelink.fiscal.service.ReceiptService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiptController.class)
@Import(TestConfig.class) // Import any additional test configuration

class ReceiptControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private FiscalService fiscalService;

    @Autowired
    private FiscalReceiptMapper fiscalReceiptMapper;

    @Test
    void createReceipt_shouldReturnCreated() throws Exception {
        // Arrange
        PosReceiptDto request = createSamplePosReceiptDto();
        Receipt savedReceipt = createSampleReceipt();

        when(receiptService.createReceipt(any(PosReceiptDto.class))).thenReturn(savedReceipt);

        // Act & Assert
        mockMvc.perform(post("/receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "receiptNumber": "RCP-123",
                        "timestamp": "2025-07-11T10:30:00Z",
                        "cashierId": "CASH-01",
                        "branchCode": "BR-001",
                        "buyer": {
                            "tin": "123456789",
                            "name": "John Doe"
                        },
                        "items": [
                            {
                                "name": "Item A",
                                "qty": 2,
                                "unitPrice": 10.00,
                                "vat": 1.50
                            }
                        ],
                        "total": 45.00,
                        "currency": "ZAR"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.receiptNumber").value("RCP-123"))
                .andExpect(jsonPath("$.status").value("PENDING_FISCALIZATION"));
    }

    @Test
    void getReceiptById_shouldReturnReceipt() throws Exception {
        // Arrange
        Receipt receipt = createSampleReceipt();
        when(receiptService.getReceiptById(1L)).thenReturn(receipt);

        // Act & Assert
        mockMvc.perform(get("/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.receiptNumber").value("RCP-123"));
    }

    @Test
    void getAllReceipts_shouldReturnFilteredResults() throws Exception {
        // Arrange
        Receipt receipt = createSampleReceipt();
        when(receiptService.getAllReceipts(any(), any(), any()))
                .thenReturn(Collections.singletonList(receipt));

        // Act & Assert
        mockMvc.perform(get("/receipts?status=PENDING_FISCALIZATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].receiptNumber").value("RCP-123"));
    }

    @Test
    void fiscalizeReceipt_shouldReturnFiscalizedReceipt() throws Exception {
        // Arrange
        Receipt fiscalizedReceipt = createSampleReceipt();
        fiscalizedReceipt.setStatus(ReceiptStatus.FISCALIZED);
        fiscalizedReceipt.setFiscalCode(UUID.randomUUID().toString());

        when(fiscalService.fiscalizeReceipt(1L)).thenReturn(fiscalizedReceipt);
        when(fiscalReceiptMapper.mapToFiscalReceiptDto(fiscalizedReceipt))
                .thenReturn(new FiscalReceiptDto());

        // Act & Assert
        mockMvc.perform(post("/receipts/1/fiscalize"))
                .andExpect(status().isOk());
    }

    private PosReceiptDto createSamplePosReceiptDto() {
        PosReceiptDto dto = new PosReceiptDto();
        dto.setReceiptNumber("RCP-123");
        dto.setTimestamp(LocalDateTime.now());
        dto.setCashierId("CASH-01");
        dto.setBranchCode("BR-001");
        dto.setTotal(BigDecimal.valueOf(45.00));
        dto.setCurrency("ZAR");
        return dto;
    }

    private Receipt createSampleReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1L);
        receipt.setReceiptNumber("RCP-123");
        receipt.setStatus(ReceiptStatus.PENDING_FISCALIZATION);
        return receipt;
    }
}