package io.papermc.hangarauth.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper mapper;

    @Autowired
    public GlobalDefaultExceptionHandler(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected @NotNull ResponseEntity<Object> handleExceptionInternal(final @NotNull Exception ex, @org.springframework.lang.Nullable final Object body, final @NotNull HttpHeaders headers, final @NotNull HttpStatusCode status, final @NotNull WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }

        return this.createExceptionResponse(ex, null, headers, status);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public @NotNull ResponseEntity<Object> responseStatusExceptionHandler(final @NotNull ResponseStatusException ex) {
        return this.createExceptionResponse(ex, null, e -> Objects.requireNonNullElse(e.getCause(), e).getMessage(), null, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public @NotNull ResponseEntity<Object> defaultErrorHandler(final @NotNull HttpServletRequest req, final @NotNull Exception ex) throws Exception {
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }
        final ObjectNode extra = this.mapper.createObjectNode();
        ex.printStackTrace();
        final ArrayNode stackTrace = extra.putArray("stacktrace");
        for (final StackTraceElement element : ex.getStackTrace()) {
            stackTrace.add(element.toString());
        }
        return this.createExceptionResponse(ex, extra, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <E extends Exception> ResponseEntity<Object> createExceptionResponse(final @NotNull E ex, final @Nullable ObjectNode extra, final @Nullable HttpHeaders headers, final @NotNull HttpStatusCode status) {
        return this.createExceptionResponse(ex, extra, Exception::getMessage, headers, status);
    }

    private <E extends Exception> ResponseEntity<Object> createExceptionResponse(final @NotNull E ex, final @Nullable ObjectNode extra, final @NotNull Function<E, @Nullable String> messageFunction, final @Nullable HttpHeaders headers, final @NotNull HttpStatusCode status) {
        final ObjectNode response = this.mapper.createObjectNode();
        response.putObject("status")
            .put("code", status.value());

        response.put("message", Objects.requireNonNullElse(messageFunction.apply(ex), status.value() + ""));

        if (extra != null && !extra.isEmpty()) {
            response.set("extra", extra);
        }

        return new ResponseEntity<>(response, headers, status);
    }
}
