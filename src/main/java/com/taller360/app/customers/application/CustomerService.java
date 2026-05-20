package com.taller360.app.customers.application;

import com.taller360.app.customers.application.dto.CreateCustomerRequest;
import com.taller360.app.customers.application.dto.CustomerResponse;
import com.taller360.app.customers.application.dto.UpdateCustomerRequest;
import com.taller360.app.customers.domain.Customer;
import com.taller360.app.customers.infrastructure.CustomerRepository;
import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll(String search) {
        List<Customer> customers = hasText(search)
                ? customerRepository.search(search.trim())
                : customerRepository.findAll().stream().sorted((left, right) -> left.getFullName().compareToIgnoreCase(right.getFullName())).toList();

        return customers.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return toResponse(getCustomerEntity(id));
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        String identification = normalizeText(request.identification());
        validateIdentificationAvailability(identification, null);

        Customer customer = new Customer();
        applyChanges(customer, request.fullName(), identification, request.phone(), request.email(), request.address());

        return toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = getCustomerEntity(id);
        String identification = normalizeText(request.identification());
        validateIdentificationAvailability(identification, id);

        applyChanges(customer, request.fullName(), identification, request.phone(), request.email(), request.address());
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void applyChanges(
            Customer customer,
            String fullName,
            String identification,
            String phone,
            String email,
            String address
    ) {
        customer.setFullName(fullName.trim());
        customer.setIdentification(identification);
        customer.setPhone(phone.trim());
        customer.setEmail(normalizeEmail(email));
        customer.setAddress(normalizeText(address));
    }

    private void validateIdentificationAvailability(String identification, Long currentCustomerId) {
        if (!hasText(identification)) {
            return;
        }

        customerRepository.findByIdentification(identification)
                .filter(existingCustomer -> !existingCustomer.getId().equals(currentCustomerId))
                .ifPresent(existingCustomer -> {
                    throw new BusinessRuleException("Identification is already registered");
                });
    }

    private String normalizeText(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeEmail(String email) {
        if (!hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getIdentification(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
