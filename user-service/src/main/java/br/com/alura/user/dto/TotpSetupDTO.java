package br.com.alura.user.dto;

public record TotpSetupDTO(String secret, String qrCodeUrl) {}
