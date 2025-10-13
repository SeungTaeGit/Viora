package com.viora.config; // 또는 com.viora.exception 패키지

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 여기서 처리합니다.
public class GlobalExceptionHandler {

    // IllegalArgumentException 예외를 특별히 처리하는 메서드
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        // "리뷰를 찾을 수 없습니다" 같은 예외가 발생하면,
        // 404 Not Found 상태 코드와 예외 메시지를 담아 응답합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // AccessDeniedException 예외를 특별히 처리하는 메서드
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        // "수정/삭제 권한이 없습니다" 같은 예외가 발생하면,
        // 403 Forbidden 상태 코드와 예외 메시지를 담아 응답합니다.
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}