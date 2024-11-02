package io.github.vimisky.luta.applier.mysql.entity;

import java.util.Date;

public class LutaApplierMapping {
    private Long id;
    private Long applierChannelId;
    //0:schema;1:table
    private Integer type;
    private String srcSchemaName;
    private String srcTableName;
    private String dstSchemaName;
    private String dstTableName;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSrcSchemaName() {
        return srcSchemaName;
    }

    public void setSrcSchemaName(String srcSchemaName) {
        this.srcSchemaName = srcSchemaName;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getDstSchemaName() {
        return dstSchemaName;
    }

    public void setDstSchemaName(String dstSchemaName) {
        this.dstSchemaName = dstSchemaName;
    }

    public String getDstTableName() {
        return dstTableName;
    }

    public void setDstTableName(String dstTableName) {
        this.dstTableName = dstTableName;
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
