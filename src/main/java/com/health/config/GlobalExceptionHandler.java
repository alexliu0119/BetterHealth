package com.health.config;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllExceptions(Exception ex) {
	    return ResponseEntity.badRequest().body(Map.of(
	        "error", "操作失敗",
	        "message", ex.getMessage()
	    ));
	}}