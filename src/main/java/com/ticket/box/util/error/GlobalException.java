package com.ticket.box.util.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ticket.box.domain.RestResponse;

@RestControllerAdvice
public class GlobalException {
  @ExceptionHandler(value = {
      UsernameNotFoundException.class,
      BadCredentialsException.class
  })
  public ResponseEntity<RestResponse<Object>> handleOverallException(Exception e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Exception occurs... ");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(value = {
      IdInvalidException.class
  })
  public ResponseEntity<RestResponse<Object>> handleIdInvalidException(Exception e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Account not found... ");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    final List<FieldError> fieldErrors = result.getFieldErrors();

    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(ex.getBody().getDetail());

    List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
    res.setMessage(errors.size() > 1 ? errors : errors.get(0));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<RestResponse<Object>> handleOrderNotFound(OrderNotFoundException e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Order not found... ");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(TicketNotFoundException.class)
  public ResponseEntity<RestResponse<Object>> handleTicketNotFound(TicketNotFoundException e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Ticket not found... ");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(TicketQuantityNotAvail.class)
  public ResponseEntity<RestResponse<Object>> handleTicketQuantityNotAvailable(TicketQuantityNotAvail e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Quantity not available... ");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }

  @ExceptionHandler(VerificationException.class)
  public ResponseEntity<RestResponse<Object>> handleVerificationException(VerificationException e) {
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setError(e.getMessage());
    res.setMessage("Verification unsuccessfully... ");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
  }
}
