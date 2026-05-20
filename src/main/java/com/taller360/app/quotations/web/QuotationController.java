package com.taller360.app.quotations.web;

import com.taller360.app.quotations.application.QuotationService;
import com.taller360.app.quotations.application.dto.QuotationItemRequest;
import com.taller360.app.quotations.application.dto.QuotationResponse;
import com.taller360.app.quotations.application.dto.UpdateQuotationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping("/api/work-orders/{id}/quotation")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse create(@PathVariable Long id) {
        return quotationService.create(id);
    }

    @GetMapping("/api/quotations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse findById(@PathVariable Long id) {
        return quotationService.findById(id);
    }

    @PutMapping("/api/quotations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse update(@PathVariable Long id, @Valid @RequestBody UpdateQuotationRequest request) {
        return quotationService.update(id, request);
    }

    @PostMapping("/api/quotations/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse addItem(@PathVariable Long id, @Valid @RequestBody QuotationItemRequest request) {
        return quotationService.addItem(id, request);
    }

    @PutMapping("/api/quotations/{id}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody QuotationItemRequest request
    ) {
        return quotationService.updateItem(id, itemId, request);
    }

    @DeleteMapping("/api/quotations/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public void deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        quotationService.deleteItem(id, itemId);
    }

    @PostMapping("/api/quotations/{id}/send")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public QuotationResponse send(@PathVariable Long id) {
        return quotationService.send(id);
    }
}
