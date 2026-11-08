package com.meto.api.application.exception;

/** Fournisseur météo injoignable ou réponse inexploitable : le client peut réessayer. */
public class WeatherProviderException extends RuntimeException {

    public WeatherProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
