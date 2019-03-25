package ru.rtksoftlabs.licensegenerator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({GenerateLicenseException.class, SignLicenseException.class})
    public ResponseEntity<ApiExceptionMessage> handleLicenseException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(new ApiExceptionMessage(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
