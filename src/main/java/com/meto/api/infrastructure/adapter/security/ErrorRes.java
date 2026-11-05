package com.meto.api.infrastructure.adapter.security;

import org.springframework.http.HttpStatus;

public record ErrorRes(HttpStatus httpStatus, String message) {
}