package com.datamanager.service;

import com.datamanager.dto.DataSourceDTO;
import com.datamanager.exception.BusinessException;
import com.datamanager.exception.NotFoundException;
import com.datamanager.model.DataSource;
import com.datamanager.repository.DataSourceRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataSourceService {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Cacheable(value = "datasources", key = "'all'")
    public List<DataSourceDTO> findAll() {
        log.debug("获取所有数据源");
        return dataSourceRepository.findAllByOrderByCreateTimeDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Cacheable(value = "datasources", key = "#id")
    @CircuitBreaker(name = "datasource", fallbackMethod = "findByIdFallback")
    public DataSourceDTO findById(Long id) {
        log.debug("获取数据源详情：id={}", id);
        DataSource ds = dataSourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("数据源不存在：" + id));
        return toDTO(ds);
    }

    /**
     * 熔断器 fallback 方法
     */
    public DataSourceDTO findByIdFallback(Long id, Throwable t) {
        log.error("数据源服务调用失败，使用降级返回：id={}, error={}", id, t.getMessage());
        throw new BusinessException(503, "数据源服务暂时不可用，请稍后重试");
    }

    @Transactional
    public DataSource save(DataSourceDTO dto) {
        log.info("保存数据源：name={}", dto.getName());
        DataSource entity = new DataSource();
        BeanUtils.copyProperties(dto, entity);
        return dataSourceRepository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        log.info("删除数据源：id={}", id);
        dataSourceRepository.deleteById(id);
    }

    @CircuitBreaker(name = "datasource", fallbackMethod = "testConnectionFallback")
    public boolean testConnection(DataSourceDTO dto) {
        String jdbcUrl = dto.getJdbcUrl();
        String username = dto.getUsername();
        String password = dto.getPassword();

        try {
            log.info("测试连接：{}", jdbcUrl);
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            conn.close();
            return true;
        } catch (Exception e) {
            log.error("连接测试失败：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 熔断器 fallback 方法
     */
    public boolean testConnectionFallback(DataSourceDTO dto, Throwable t) {
        log.error("数据库连接测试失败，服务不可用：error={}", t.getMessage());
        throw new BusinessException(503, "数据库连接服务暂时不可用，请稍后重试");
    }

    private DataSourceDTO toDTO(DataSource entity) {
        DataSourceDTO dto = new DataSourceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
