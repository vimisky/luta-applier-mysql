package io.github.vimisky.luta.applier.mysql.entity;

import java.util.Date;

public class LutaApplierTransaction {
    private Long id;
    private Long applierChannelId;
    private Date applierTime;
    private Integer applierStatus;
    private boolean dml;
    private String ddlSql;
    private Long serverId;
    private String binlogFilename;
    private Long nextPosition;
    private Long xid;
    private String schemaName;
    private String tableName;
    private String mappingSchemaName;
    private String mappingTableName;
    private String type;
    private String columnNameList;
    private String columnValueList;
    private String oldColumnNameList;
    private String oldColumnValueList;
    private String sql;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplierChannelId() {
        return applierChannelId;
    }

    public void setApplierChannelId(Long applierChannelId) {
        this.applierChannelId = applierChannelId;
    }

    public Date getApplierTime() {
        return applierTime;
    }

    public void setApplierTime(Date applierTime) {
        this.applierTime = applierTime;
    }

    public Integer getApplierStatus() {
        return applierStatus;
    }

    public void setApplierStatus(Integer applierStatus) {
        this.applierStatus = applierStatus;
    }

    public boolean isDml() {
        return dml;
    }

    public void setDml(boolean dml) {
        this.dml = dml;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public void setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
    }

    public Long getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(Long nextPosition) {
        this.nextPosition = nextPosition;
    }

    public Long getXid() {
        return xid;
    }

    public void setXid(Long xid) {
        this.xid = xid;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMappingSchemaName() {
        return mappingSchemaName;
    }

    public void setMappingSchemaName(String mappingSchemaName) {
        this.mappingSchemaName = mappingSchemaName;
    }

    public String getMappingTableName() {
        return mappingTableName;
    }

    public void setMappingTableName(String mappingTableName) {
        this.mappingTableName = mappingTableName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(String columnNameList) {
        this.columnNameList = columnNameList;
    }

    public String getColumnValueList() {
        return columnValueList;
    }

    public void setColumnValueList(String columnValueList) {
        this.columnValueList = columnValueList;
    }

    public String getOldColumnNameList() {
        return oldColumnNameList;
    }

    public void setOldColumnNameList(String oldColumnNameList) {
        this.oldColumnNameList = oldColumnNameList;
    }

    public String getOldColumnValueList() {
        return oldColumnValueList;
    }

    public void setOldColumnValueList(String oldColumnValueList) {
        this.oldColumnValueList = oldColumnValueList;
    }

    public String getDdlSql() {
        return ddlSql;
    }

    public void setDdlSql(String ddlSql) {
        this.ddlSql = ddlSql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
