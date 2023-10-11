package com.hussein221.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PasswordDontMatchError extends ResponseStatusException {
    public PasswordDontMatchError( ) {
        super(HttpStatus.BAD_REQUEST,"Password fo not match errors");
    }
}
