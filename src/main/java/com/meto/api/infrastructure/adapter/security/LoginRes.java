package com.meto.api.infrastructure.adapter.security;

public record LoginRes(String email, String token, String role) {
}