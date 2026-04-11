package com.datamanager.service;

import com.datamanager.exception.NotFoundException;
import com.datamanager.model.DataSource;
import com.datamanager.model.Metadata;
import com.datamanager.repository.DataSourceRepository;
import com.datamanager.repository.MetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据服务
 */
@Service
public class MetadataService {

    private static final Logger log = LoggerFactory.getLogger(MetadataService.class);

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    /**
     * 同步数据源的元数据
     */
    @Transactional
    public void syncMetadata(Long datasourceId) {
        DataSource dataSource = dataSourceRepository.findById(datasourceId)
                .orElseThrow(() -> new NotFoundException("数据源不存在"));

        // 清除旧元数据
        metadataRepository.deleteByDatasourceId(datasourceId);

        List<Metadata> metadataList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                dataSource.getJdbcUrl(),
                dataSource.getUsername(),
                dataSource.getPassword())) {

            DatabaseMetaData dbMeta = conn.getMetaData();

            // 获取数据库名
            String catalog = conn.getCatalog();

            // 同步表信息
            try (ResultSet tables = dbMeta.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String tableComment = tables.getString("REMARKS");

                    // 保存表级元数据
                    Metadata tableMeta = new Metadata();
                    tableMeta.setDatasourceId(datasourceId);
                    tableMeta.setDatabaseName(catalog);
                    tableMeta.setTableName(tableName);
                    tableMeta.setComment(tableComment);
                    tableMeta.setObjectType("TABLE");
                    tableMeta.setIsNullable(false);
                    tableMeta.setIsPrimaryKey(false);
                    metadataList.add(tableMeta);

                    // 获取表行数
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                        if (rs.next()) {
                            tableMeta.setRowCount(rs.getInt(1));
                        }
                    } catch (SQLException e) {
                        log.warn("获取表行数失败：{}", tableName);
                    }
                }
            }

            // 同步列信息
            try (ResultSet columns = dbMeta.getColumns(catalog, null, "%", null)) {
                while (columns.next()) {
                    String tableName = columns.getString("TABLE_NAME");
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    int precision = columns.getInt("DECIMAL_DIGITS");
                    int scale = columns.getInt("NUM_PREC_RADIX");
                    String isNullable = columns.getString("IS_NULLABLE");
                    String defaultValue = columns.getString("COLUMN_DEF");
                    String comment = columns.getString("REMARKS");

                    Metadata columnMeta = new Metadata();
                    columnMeta.setDatasourceId(datasourceId);
                    columnMeta.setDatabaseName(catalog);
                    columnMeta.setTableName(tableName);
                    columnMeta.setColumnName(columnName);
                    columnMeta.setColumnType(columnType);
                    columnMeta.setColumnLength(columnSize);
                    columnMeta.setColumnPrecision(precision);
                    columnMeta.setIsNullable("YES".equalsIgnoreCase(isNullable));
                    columnMeta.setDefaultValue(defaultValue);
                    columnMeta.setComment(comment);
                    columnMeta.setObjectType("COLUMN");
                    columnMeta.setIsPrimaryKey(false);
                    metadataList.add(columnMeta);
                }
            }

            // 获取主键信息
            try (ResultSet tables = dbMeta.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    try (ResultSet primaryKeys = dbMeta.getPrimaryKeys(catalog, null, tableName)) {
                        while (primaryKeys.next()) {
                            String pkColumn = primaryKeys.getString("COLUMN_NAME");
                            // 更新主键标记
                            metadataList.stream()
                                    .filter(m -> m.getDatasourceId().equals(datasourceId)
                                            && m.getTableName().equals(tableName)
                                            && m.getColumnName().equals(pkColumn))
                                    .forEach(m -> m.setIsPrimaryKey(true));
                        }
                    }
                }
            }

            // 设置同步时间
            LocalDateTime now = LocalDateTime.now();
            metadataList.forEach(m -> m.setLastSyncTime(now));

            // 批量保存
            metadataRepository.saveAll(metadataList);

            log.info("同步元数据完成，数据源：{}，共 {} 条记录", datasourceId, metadataList.size());

        } catch (SQLException e) {
            log.error("同步元数据失败：{}", e.getMessage(), e);
            throw new RuntimeException("同步元数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取数据源的所有数据库
     */
    public List<String> getDatabases(Long datasourceId) {
        return metadataRepository.findDistinctDatabasesByDatasourceId(datasourceId);
    }

    /**
     * 获取数据库的所有表
     */
    public List<String> getTables(Long datasourceId, String databaseName) {
        return metadataRepository.findDistinctTablesByDatasourceIdAndDatabase(datasourceId, databaseName);
    }

    /**
     * 获取表的所有列
     */
    public List<Metadata> getColumns(Long datasourceId, String databaseName, String tableName) {
        return metadataRepository.findByDatasourceIdAndDatabaseNameAndTableNameOrderByColumnNameAsc(
                datasourceId, databaseName, tableName);
    }

    /**
     * 获取数据源的所有元数据
     */
    public List<Metadata> getMetadataByDatasource(Long datasourceId) {
        return metadataRepository.findByDatasourceIdOrderByDatabaseNameAscTableNameAsc(datasourceId);
    }

    /**
     * 获取表级元数据
     */
    public List<Metadata> getTableMetadata(Long datasourceId) {
        return metadataRepository.findByDatasourceIdAndObjectTypeOrderByDatabaseNameAsc(datasourceId, "TABLE");
    }

    /**
     * 获取单个元数据
     */
    public Metadata findById(Long id) {
        return metadataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("元数据不存在：" + id));
    }

    /**
     * 更新元数据注释
     */
    @Transactional
    public Metadata updateComment(Long id, String comment) {
        Metadata metadata = findById(id);
        metadata.setComment(comment);
        return metadataRepository.save(metadata);
    }
}
