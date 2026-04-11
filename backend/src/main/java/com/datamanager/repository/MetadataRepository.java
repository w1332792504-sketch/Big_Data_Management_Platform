package com.datamanager.repository;

import com.datamanager.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {
    List<Metadata> findByDatasourceIdOrderByDatabaseNameAscTableNameAsc(Long datasourceId);
    List<Metadata> findByDatasourceIdAndObjectTypeOrderByDatabaseNameAsc(Long datasourceId, String objectType);
    List<Metadata> findByDatasourceIdAndDatabaseNameOrderByTableNameAsc(Long datasourceId, String databaseName);
    List<Metadata> findByDatasourceIdAndDatabaseNameAndTableNameOrderByColumnNameAsc(Long datasourceId, String databaseName, String tableName);
    Optional<Metadata> findByDatasourceIdAndDatabaseNameAndTableNameAndColumnName(Long datasourceId, String databaseName, String tableName, String columnName);

    @Query("SELECT DISTINCT m.databaseName FROM Metadata m WHERE m.datasourceId = ?1 ORDER BY m.databaseName")
    List<String> findDistinctDatabasesByDatasourceId(Long datasourceId);

    @Query("SELECT DISTINCT m.tableName FROM Metadata m WHERE m.datasourceId = ?1 AND m.databaseName = ?2 ORDER BY m.tableName")
    List<String> findDistinctTablesByDatasourceIdAndDatabase(Long datasourceId, String databaseName);

    void deleteByDatasourceId(Long datasourceId);
}
