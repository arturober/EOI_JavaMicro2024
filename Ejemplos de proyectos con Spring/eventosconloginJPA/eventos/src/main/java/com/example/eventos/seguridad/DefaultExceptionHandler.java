package com.example.eventos.seguridad;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;


@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception ex) {

        RestError re = new RestError(HttpStatus.UNAUTHORIZED.value(), 
          "Authentication failed at controller advice");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    // @Override
    // @Nullable
    // protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
    //         HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
    //     return ResponseEntity.status(400).body("Hola");
    // }

    @ExceptionHandler({ ResponseStatusException.class })
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {

      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
      problemDetail.setType(URI.create(req.getRequestURI()));
      // problemDetail.setInstance(URI.create("https://instance"));
      return ResponseEntity.of(Optional.of(problemDetail));
    }
}