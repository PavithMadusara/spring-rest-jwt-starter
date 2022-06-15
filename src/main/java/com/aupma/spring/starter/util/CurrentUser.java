package com.aupma.spring.starter.util;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Parameter(hidden = true)
@AuthenticationPrincipal(expression = "@fetchCurrentUser.apply(#this)", errorOnInvalidType=true)
public @interface CurrentUser {}
