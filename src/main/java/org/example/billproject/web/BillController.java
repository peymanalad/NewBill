package org.example.billproject.web;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.example.billproject.domain.model.Bill;
import org.example.billproject.domain.model.BillStatus;
import org.example.billproject.service.BillService;
import org.example.billproject.web.dto.BillMapper;
import org.example.billproject.web.dto.BillPaymentRequest;
import org.example.billproject.web.dto.BillRequest;
import org.example.billproject.web.dto.BillResponse;
import org.example.billproject.web.dto.BillStatusUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bills")
@Validated
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> list(@RequestParam(value = "status", required = false) BillStatus status) {
        List<BillResponse> responses = billService.findAll(status).stream()
                .map(BillMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getById(@PathVariable("id") UUID id) {
        Bill bill = billService.findById(id);
        return ResponseEntity.ok(BillMapper.toResponse(bill));
    }

    @PostMapping
    public ResponseEntity<BillResponse> create(@Valid @RequestBody BillRequest request) {
        Bill created = billService.create(BillMapper.toDomain(request));
        BillResponse response = BillMapper.toResponse(created);
        return ResponseEntity.created(URI.create("/api/bills/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillResponse> update(@PathVariable("id") UUID id, @Valid @RequestBody BillRequest request) {
        Bill updated = billService.update(id, BillMapper.toDomain(request));
        return ResponseEntity.ok(BillMapper.toResponse(updated));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<BillResponse> updateStatus(@PathVariable("id") UUID id,
                                                     @Valid @RequestBody BillStatusUpdateRequest request) {
        Bill updated = billService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(BillMapper.toResponse(updated));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<BillResponse> registerPayment(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody BillPaymentRequest request) {
        Bill updated = billService.registerPayment(id, request.getPaymentReference());
        return ResponseEntity.ok(BillMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        billService.delete(id);
    }
}