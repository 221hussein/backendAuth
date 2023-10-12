package com.hussein221.model;

import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

public record Token(String refreshToken, LocalDateTime issuedAt, LocalDateTime expiredAt) {}
