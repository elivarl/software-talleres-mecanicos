package com.taller360.app.quotations.web;

import com.taller360.app.quotations.application.QuotationService;
import com.taller360.app.quotations.application.dto.QuotationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/quotations")
public class PublicQuotationController {

    private final QuotationService quotationService;

    public PublicQuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @GetMapping("/{token}")
    public QuotationResponse findByToken(@PathVariable String token) {
        return quotationService.findByPublicToken(token);
    }

    @PostMapping("/{token}/approve")
    public QuotationResponse approve(@PathVariable String token) {
        return quotationService.approveByPublicToken(token);
    }

    @PostMapping("/{token}/reject")
    public QuotationResponse reject(@PathVariable String token) {
        return quotationService.rejectByPublicToken(token);
    }
}
