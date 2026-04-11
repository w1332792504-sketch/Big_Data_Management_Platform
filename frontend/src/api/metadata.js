import request from './request'

// 同步元数据
export const syncMetadata = (datasourceId) => request.post(`/metadata/sync/${datasourceId}`)

// 获取数据库列表
export const getDatabases = (datasourceId) => request.get(`/metadata/databases/${datasourceId}`)

// 获取表列表
export const getTables = (datasourceId, databaseName) =>
  request.get(`/metadata/tables/${datasourceId}`, { params: { databaseName } })

// 获取列列表
export const getColumns = (datasourceId, databaseName, tableName) =>
  request.get(`/metadata/columns/${datasourceId}`, { params: { databaseName, tableName } })

// 获取数据源的所有元数据
export const getMetadataByDatasource = (datasourceId) => request.get(`/metadata/list/${datasourceId}`)

// 获取表级元数据
export const getTableMetadata = (datasourceId) => request.get(`/metadata/tables-level/${datasourceId}`)

// 获取单个元数据
export const getMetadata = (id) => request.get(`/metadata/${id}`)

// 更新元数据注释
export const updateMetadataComment = (id, comment) =>
  request.put(`/metadata/${id}/comment`, null, { params: { comment } })
