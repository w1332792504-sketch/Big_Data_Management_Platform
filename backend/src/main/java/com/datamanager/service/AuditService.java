package com.datamanager.service;

import com.datamanager.model.AuditLog;
import com.datamanager.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计日志服务
 */
@Service
@Aspect
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 记录审计日志
     */
    @Transactional
    public void log(String module, String operation, String description,
                    String operator, Long operatorId, String ip,
                    String method, String requestUrl, String requestParams,
                    String responseData, Integer responseStatus, Long duration,
                    String status, String errorMsg) {

        AuditLog auditLog = new AuditLog();
        auditLog.setModule(module);
        auditLog.setOperation(operation);
        auditLog.setDescription(description);
        auditLog.setOperator(operator);
        auditLog.setOperatorId(operatorId);
        auditLog.setIp(ip);
        auditLog.setMethod(method);
        auditLog.setRequestUrl(requestUrl);
        auditLog.setRequestParams(requestParams);
        auditLog.setResponseData(responseData);
        auditLog.setResponseStatus(responseStatus);
        auditLog.setDuration(duration);
        auditLog.setStatus(status);
        auditLog.setErrorMsg(errorMsg);

        auditLogRepository.save(auditLog);
    }

    /**
     * 简化日志记录方法
     */
    @Transactional
    public void log(String module, String operation, String description, String operator) {
        HttpServletRequest request = getCurrentRequest();
        String ip = request != null ? getClientIp(request) : null;

        log(module, operation, description, operator, null, ip,
                request != null ? request.getMethod() : null,
                request != null ? request.getRequestURI() : null,
                null, null, null, null, "SUCCESS", null);
    }

    /**
     * 记录API调用日志
     */
    @Transactional
    public void logApiCall(String module, String operation, String description,
                           HttpServletRequest request, Object response,
                           Long duration, String status, String errorMsg) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setModule(module);
            auditLog.setOperation(operation);
            auditLog.setDescription(description);
            auditLog.setIp(getClientIp(request));
            auditLog.setMethod(request.getMethod());
            auditLog.setRequestUrl(request.getRequestURI());

            // 请求参数
            Map<String, String[]> params = request.getParameterMap();
            if (!params.isEmpty()) {
                auditLog.setRequestParams(objectMapper.writeValueAsString(params));
            }

            // 响应数据
            if (response != null) {
                String responseData = objectMapper.writeValueAsString(response);
                if (responseData.length() > 5000) {
                    responseData = responseData.substring(0, 5000) + "...";
                }
                auditLog.setResponseData(responseData);
            }

            auditLog.setDuration(duration);
            auditLog.setStatus(status);
            auditLog.setErrorMsg(errorMsg);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("记录审计日志失败", e);
        }
    }

    /**
     * 查询审计日志
     */
    public Page<AuditLog> findLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findAll(pageable);
    }

    /**
     * 按模块查询
     */
    public Page<AuditLog> findByModule(String module, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByModuleOrderByCreateTimeDesc(module, pageable);
    }

    /**
     * 按操作人查询
     */
    public Page<AuditLog> findByOperator(String operator, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByOperatorOrderByCreateTimeDesc(operator, pageable);
    }

    /**
     * 按时间范围查询
     */
    public Page<AuditLog> findByTimeRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return auditLogRepository.findByCreateTimeBetweenOrderByCreateTimeDesc(start, end, pageable);
    }

    /**
     * 统计今日操作数
     */
    public Long countTodayOperations() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return auditLogRepository.countByCreateTimeAfter(today);
    }

    /**
     * 按模块统计
     */
    public List<Object[]> countByModule() {
        return auditLogRepository.countGroupByModule();
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
