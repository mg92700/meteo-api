package com.meto.api.infrastructure.exception;

import jakarta.validation.ConstraintViolationException;
import com.meto.api.application.exception.CityNotFoundException;
import com.meto.api.application.exception.WeatherProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requête invalide");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requête invalide");
        pd.setDetail(ex.getBindingResult().toString());
        return pd;
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ProblemDetail handleCityNotFound(CityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Ville introuvable");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(WeatherProviderException.class)
    public ProblemDetail handleProvider(WeatherProviderException ex) {
        log.warn("Fournisseur météo indisponible : {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setTitle("Service météo indisponible");
        pd.setDetail("Le fournisseur météo ne répond pas, réessayez plus tard.");
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Requête invalide");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    /**
     * Les exceptions Spring qui portent déjà leur statut (paramètre manquant 400, route inconnue 404,
     * méthode 405…) le gardent ; tout le reste est une erreur serveur (500), sans détail technique.
     */
    @ExceptionHandler(Throwable.class)
    public ProblemDetail handleAny(Throwable ex) {
        if (ex instanceof ErrorResponse error) {
            return error.getBody();
        }
        log.error("Unexpected error", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Erreur interne");
        pd.setDetail("Une erreur inattendue est survenue.");
        return pd;
    }
}