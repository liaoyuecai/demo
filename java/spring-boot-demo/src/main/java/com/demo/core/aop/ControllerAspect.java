package com.demo.core.aop;

import com.demo.core.authentication.AuthenticationUser;
import com.demo.core.dto.ApiHttpRequest;
import com.demo.core.dto.ApiHttpResponse;
import com.demo.core.entity.TableBaseEntity;
import com.demo.core.exception.ErrorCode;
import com.demo.core.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 对controller的http request的aop处理
 */
@Aspect
@Component
@Slf4j
public class ControllerAspect {

    @Pointcut("""
                @annotation(org.springframework.web.bind.annotation.RequestMapping)
                ||@annotation(org.springframework.web.bind.annotation.PostMapping)
                ||@annotation(org.springframework.web.bind.annotation.GetMapping)
                ||@annotation(org.springframework.web.bind.annotation.PutMapping)
                ||@annotation(org.springframework.web.bind.annotation.DeleteMapping)
            """)
    public void requestPointcut() {
    }

    @Around(value = "requestPointcut()")
    public Object aroundRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        Object[] args = joinPoint.getArgs();
        try {
            params(args,joinPoint);
            Object response = joinPoint.proceed(args);
            log.info("http uri:{}, request:{},response:{}", uri, args, response);
            return response;
        } catch (GlobalException e) {
            log.error("http error uri:{}, request:{}", uri, args, e);
            return new ApiHttpResponse(e.getCode());
        } catch (Exception e) {
            log.error("http error uri:{}, request:{}", uri, args, e);
            throw e;
        }
    }

    /**
     * 入参处理
     *
     * @param args
     * @param joinPoint
     */
    void params(Object[] args, ProceedingJoinPoint joinPoint) {
        Class<?> targetCls = joinPoint.getTarget().getClass();
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();

        try {
            Method targetMethod =
                    targetCls.getDeclaredMethod(
                            ms.getName(),
                            ms.getParameterTypes());
            if (targetMethod.isAnnotationPresent(RequestSave.class)) {
                RequestSave save = targetMethod.getAnnotation(RequestSave.class);
                TableBaseEntity entity = getEntity(args, save.index());
                AuthenticationUser user = getUser();
                if (entity.getId() == null) {
                    entity.setCreateTime(LocalDateTime.now());
                    entity.setStatus(1);
                    entity.setDeleted(0);
                    entity.setCreateBy(user.getId());
                } else {
                    entity.setUpdateTime(LocalDateTime.now());
                    entity.setUpdateBy(user.getId());
                }
            }
            if (targetMethod.isAnnotationPresent(RequestSelect.class)) {
                RequestSelect select = targetMethod.getAnnotation(RequestSelect.class);
                TableBaseEntity entity = getEntity(args, select.index());
                if (select.status() != -1)
                    entity.setStatus(select.status());
                entity.setDeleted(0);
            }
        } catch (NoSuchMethodException e) {
            //这里几乎不可能报错，不予理会
            throw new GlobalException(ErrorCode.CODE_ERROR);
        }

    }

    TableBaseEntity getEntity(Object[] args, int index) {
        if (args.length < index)
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_NOT_FOUND);
        Object param = args[index];
        if (!(param instanceof ApiHttpRequest))
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_TYPE_ERROR);
        if (param == null)
            throw new GlobalException(ErrorCode.PARAMS_ERROR_REQUEST_DATA_NOT_FOUND);
        if (!(((ApiHttpRequest<?>) param).getData() instanceof TableBaseEntity))
            throw new GlobalException(ErrorCode.CODE_ERROR_PARAMS_TYPE_ERROR);
        return (TableBaseEntity) ((ApiHttpRequest<?>) param).getData();
    }

    AuthenticationUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_ERROR);
        return (AuthenticationUser) authentication.getDetails();
    }
}
