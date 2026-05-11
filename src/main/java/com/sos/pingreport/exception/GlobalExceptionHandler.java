package com.sos.pingreport.exception;

import com.sos.pingreport.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // common builder
    private ResponseEntity<ErrorResponseDTO> buildError(@NonNull HttpStatus status, String message) {

        return ResponseEntity.status(status)
                .body(ErrorResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .status(status.value())
                        .message(message)
                        .build()
                );
    }

    // invalid project
    @ExceptionHandler(InvalidProjectException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidProject(InvalidProjectException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // python service
    @ExceptionHandler(PythonServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handlePython(PythonServiceException ex) {
        return buildError(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    // mongo
    @ExceptionHandler(MongoFetchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMongo(MongoFetchException ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // PDF
    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ErrorResponseDTO> handlePdf(PdfGenerationException ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // illegal argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegal(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAll(Exception ex) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Unexpected server error"
        );
    }
}