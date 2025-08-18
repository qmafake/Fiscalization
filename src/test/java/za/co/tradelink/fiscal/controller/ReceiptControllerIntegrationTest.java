package za.co.tradelink.fiscal.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import za.co.tradelink.fiscal.dto.CreateReceiptResponse;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReceiptControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders headers;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/receipts";
//        baseUrl = "http://localhost:" + 8181 + "/receipts"; TODO: -ve
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void contextLoads() {
        assertNotNull(restTemplate);
        assertTrue(port > 0);
        logger.info("Application is running on port: " + port);
    }


    @Test
    void testAllReceiptEndpoints() {
        // Test Create Receipt
        PosReceiptDto createRequest = createSamplePosReceipt("RCP-TEST-001");

        ResponseEntity<CreateReceiptResponse> createResponse = createReceipt(createRequest);

        logger.info("Created Receipt with id: {}, status: {}" , createResponse.getBody().getId(),
        createResponse.getBody().getStatus());

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertEquals("RCP-TEST-001", createResponse.getBody().getReceiptNumber());
        assertEquals("PENDING_FISCALIZATION", createResponse.getBody().getStatus().name());

        Long receiptId = createResponse.getBody().getId();

        // Test Get Receipt By ID
        ResponseEntity<CreateReceiptResponse> getResponse = getReceiptById(receiptId);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(receiptId, getResponse.getBody().getId());
        assertEquals("RCP-TEST-001", getResponse.getBody().getReceiptNumber());

        // Test Get All Receipts
        ResponseEntity<List<CreateReceiptResponse>> getAllResponse = getAllReceipts();

        assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
        assertNotNull(getAllResponse.getBody());
        assertFalse(getAllResponse.getBody().isEmpty());
        assertEquals(receiptId, getAllResponse.getBody().get(0).getId());

        // Test Fiscalize Receipt
        ResponseEntity<FiscalReceiptDto> fiscalizeResponse = fiscalizeReceipt(receiptId);

        assertEquals(HttpStatus.OK, fiscalizeResponse.getStatusCode());
        assertNotNull(fiscalizeResponse.getBody());
        assertEquals("RCP-TEST-001", fiscalizeResponse.getBody().getReceiptNumber());
        assertNotNull(fiscalizeResponse.getBody().getFiscalCode());

        // Verify status changed after fiscalization
        ResponseEntity<CreateReceiptResponse> updatedGetResponse = getReceiptById(receiptId);
        assertEquals("FISCALIZED", updatedGetResponse.getBody().getStatus().name());
    }

    @Test
    void testErrorScenarios() {
        // Test Invalid Create Receipt
        PosReceiptDto invalidRequest = new PosReceiptDto(); // Missing required fields
        ResponseEntity<String> badCreateResponse = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(invalidRequest, headers),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, badCreateResponse.getStatusCode());
//        assertTrue(badCreateResponse.getBody().contains("Validation failed"));

        // Test Get Non-Existent Receipt
        ResponseEntity<String> notFoundResponse = restTemplate.getForEntity(
                baseUrl + "/999999",
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());

        // Test Fiscalize Non-Existent Receipt
        ResponseEntity<String> badFiscalizeResponse = restTemplate.postForEntity(
                baseUrl + "/999999/fiscalize",
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, badFiscalizeResponse.getStatusCode());
    }

    private PosReceiptDto createSamplePosReceipt(String receiptNumber) {
        PosReceiptDto request = new PosReceiptDto();
        request.setReceiptNumber(receiptNumber);
        request.setTimestamp(LocalDateTime.now());
        request.setCashierId("CASH-TEST-01");
        request.setBranchCode("BR-TEST-001");

        PosReceiptDto.BuyerDto buyerDto = new PosReceiptDto.BuyerDto();
        buyerDto.setTin("12345678");
        buyerDto.setName("Hardwork Pays");
        request.setBuyer(buyerDto);

        PosReceiptDto.ItemDto item1 = new PosReceiptDto.ItemDto();
        item1.setName("Test Item");
        item1.setQty(2);
        item1.setUnitPrice(BigDecimal.valueOf(10.00));
        item1.setVat(BigDecimal.valueOf(1.50));
        request.setItems(List.of(item1));

        request.setTotal(BigDecimal.valueOf(100.00));
        request.setCurrency("ZAR");

        return request;
    }

    private ResponseEntity<CreateReceiptResponse> createReceipt(PosReceiptDto request) {
        return restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                CreateReceiptResponse.class
        );
    }

    private ResponseEntity<CreateReceiptResponse> getReceiptById(Long id) {
        return restTemplate.getForEntity(
                baseUrl + "/" + id,
                CreateReceiptResponse.class
        );
    }

    private ResponseEntity<List<CreateReceiptResponse>> getAllReceipts() {
        String url = baseUrl + "?status=PENDING_FISCALIZATION&fromDate=" +
                formatDateTime(LocalDateTime.now().minusDays(1)) +
                "&toDate=" + formatDateTime(LocalDateTime.now());

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
    }

    private ResponseEntity<FiscalReceiptDto> fiscalizeReceipt(Long id) {
        return restTemplate.postForEntity(
                baseUrl + "/" + id + "/fiscalize",
                new HttpEntity<>(headers),
                FiscalReceiptDto.class
        );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}