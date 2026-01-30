package com.banking.acccountservice.exception;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Get the class that is handling the request
        Class<?> controllerClass = returnType.getContainingClass();

        // Disable wrapping for SpringDoc/Swagger internal controllers
        return !controllerClass.getPackage().getName().contains("org.springdoc") &&
                !controllerClass.getPackage().getName().contains("org.springframework.boot.autoconfigure.web");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // Double check: don't wrap if it's already an ApiResponse or if the path is for docs
        String path = request.getURI().getPath();
        if (body instanceof ApiResponse || path.contains("/v3/api-docs") || path.contains("/swagger-ui")) {
            return body;
        }

        return ApiResponse.success(200, "OK", body);
    }
}