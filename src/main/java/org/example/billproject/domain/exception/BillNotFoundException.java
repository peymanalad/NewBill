package org.example.billproject.domain.exception;

import java.util.UUID;

public class BillNotFoundException extends RuntimeException {

    public BillNotFoundException(UUID id) {
        super("Bill with id " + id + " was not found");
    }
}