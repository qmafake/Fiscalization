package za.co.tradelink.fiscal.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.service.FiscalService;
import za.co.tradelink.fiscal.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;
    private final FiscalService fiscalService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@Valid @RequestBody PosReceiptDto posReceiptDto) {

        logger.info("Incoming receipt for to creation: {}", posReceiptDto);

        Receipt receipt = receiptService.createReceipt(posReceiptDto);

        logger.info("Receipt Number: {}, created with Id: {}, status: {}", receipt.getReceiptNumber(), receipt.getId(),
                receipt.getStatus() );

        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    /* //TODO: -ve
    @PostMapping
    public ResponseEntity<Object> createReceipt(@Valid @RequestBody PosReceiptDto posReceiptDto) {

        Receipt receipt = receiptService.createReceipt(posReceiptDto);

        logger.info("Created receipt number: {}", receipt.getReceiptNumber());

        return new ResponseEntity<>(receipt.getReceiptNumber() + " created successfully", HttpStatus.OK);

    }
    */
    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {

        logger.info("Lookup receipt with Id:{}", id);

        Receipt receipt = receiptService.getReceiptById(id);

        logger.info("Found receipt {}", receipt);

        return ResponseEntity.ok(receipt);
    }
    
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts(
            @RequestParam(required = false) ReceiptStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

        logger.info("Lookup receipts filter by - status: {}, fromDate: {}, toDate: {}", status, fromDate, toDate);

        List<Receipt> receipts = receiptService.getAllReceipts(status, fromDate, toDate);

        logger.info("Number of receipts found: {}", receipts.size());

        return ResponseEntity.ok(receipts);
    }
    
    @PostMapping("/{id}/fiscalize")
    public ResponseEntity<FiscalReceiptDto> fiscalizeReceipt(@PathVariable Long id) {

        Receipt receipt = fiscalService.fiscalizeReceipt(id);
        FiscalReceiptDto fiscalReceiptDto = fiscalService.mapToFiscalReceiptDto(receipt);
        return ResponseEntity.ok(fiscalReceiptDto);
    }
}