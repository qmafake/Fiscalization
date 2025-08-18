package za.co.tradelink.fiscal.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.tradelink.fiscal.dto.CreateReceiptResponse;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.mapper.FiscalReceiptMapper;
import za.co.tradelink.fiscal.mapper.ReceiptMapper;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.service.FiscalService;
import za.co.tradelink.fiscal.service.ReceiptService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/receipts")
//@AllArgsConstructor
//@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;
    private final FiscalService fiscalService;
    private final ReceiptMapper receiptMapper;
    private final FiscalReceiptMapper fiscalReceiptMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ReceiptController(ReceiptService receiptService, FiscalService fiscalService, ReceiptMapper receiptMapper, FiscalReceiptMapper fiscalReceiptMapper) {
        this.receiptService = receiptService;
        this.fiscalService = fiscalService;
        this.receiptMapper = receiptMapper;
        this.fiscalReceiptMapper = fiscalReceiptMapper;
    }

    @PostMapping
    public ResponseEntity<CreateReceiptResponse> createReceipt(@Valid @RequestBody PosReceiptDto posReceiptDto) {

        logger.info("Create receipt: {}", posReceiptDto);

        Receipt receipt = receiptService.createReceipt(posReceiptDto);
        CreateReceiptResponse createReceiptResponse = receiptMapper.mapToCreateReceiptResponse(receipt);

        logger.info("Created - Receipt Number: {}, Id: {}, status: {}", receipt.getReceiptNumber(), receipt.getId(),
                receipt.getStatus() );

        return ResponseEntity.status(HttpStatus.CREATED).body(createReceiptResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateReceiptResponse>  getReceiptById(@PathVariable Long id) {

        logger.info("Lookup receipt Id:{}", id);

        Receipt receipt = receiptService.getReceiptById(id);
        CreateReceiptResponse createReceiptResponse = receiptMapper.mapToCreateReceiptResponse(receipt);

        logger.info("Found receipt {}", receipt);

        return ResponseEntity.ok(createReceiptResponse);
    }
    
    @GetMapping
    public ResponseEntity<List<CreateReceiptResponse>> getAllReceipts(
            @RequestParam(required = false) ReceiptStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

        logger.info("Lookup receipts filter by - status: {}, fromDate: {}, toDate: {}", status, fromDate, toDate);

        List<Receipt> receipts = receiptService.getAllReceipts(status, fromDate, toDate);

        List<CreateReceiptResponse> createReceiptResponses = receipts.stream()
                .map(receiptMapper::mapToCreateReceiptResponse)
                .toList();

        logger.info("Number of receipts found: {}", createReceiptResponses.size());

        return ResponseEntity.ok(createReceiptResponses);
    }
    
    @PostMapping("/{id}/fiscalize")
    public ResponseEntity<FiscalReceiptDto> fiscalizeReceipt(@PathVariable Long id) {

        logger.info("Fiscalizing receipt - {} ", id);

        Receipt receipt = fiscalService.fiscalizeReceipt(id);
        FiscalReceiptDto fiscalReceiptDto = fiscalReceiptMapper.mapToFiscalReceiptDto(receipt);

        logger.info("Fiscalization completed for receipt - {}, Fiscalization code - {} ",
                fiscalReceiptDto.getReceiptNumber(), fiscalReceiptDto.getFiscalCode());

        return ResponseEntity.ok(fiscalReceiptDto);
    }
}