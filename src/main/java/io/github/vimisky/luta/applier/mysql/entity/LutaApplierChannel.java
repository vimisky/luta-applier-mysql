package io.github.vimisky.luta.applier.mysql.entity;

import java.util.Date;

public class LutaApplierChannel {

    private Long id;
    //0, RabbitMQ
    private String name;
    private String description;
    private Integer srcMessageQueueType;
    private String srcHost;
    private Integer srcPort;
    private String srcVhost;
    private String srcUsername;
    private String srcPassword;
    private String srcQueueName;

    private String dstDriverClassName;
    private String dstHost;
    private Integer dstPort;
    private String dstUsername;
    private String dstPassword;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSrcMessageQueueType() {
        return srcMessageQueueType;
    }

    public void setSrcMessageQueueType(Integer srcMessageQueueType) {
        this.srcMessageQueueType = srcMessageQueueType;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public String getSrcVhost() {
        return srcVhost;
    }

    public void setSrcVhost(String srcVhost) {
        this.srcVhost = srcVhost;
    }

    public String getSrcUsername() {
        return srcUsername;
    }

    public void setSrcUsername(String srcUsername) {
        this.srcUsername = srcUsername;
    }

    public String getSrcPassword() {
        return srcPassword;
    }

    public void setSrcPassword(String srcPassword) {
        this.srcPassword = srcPassword;
    }

    public String getSrcQueueName() {
        return srcQueueName;
    }

    public void setSrcQueueName(String srcQueueName) {
        this.srcQueueName = srcQueueName;
    }

    public String getDstDriverClassName() {
        return dstDriverClassName;
    }

    public void setDstDriverClassName(String dstDriverClassName) {
        this.dstDriverClassName = dstDriverClassName;
    }

    public String getDstHost() {
        return dstHost;
    }

    public void setDstHost(String dstHost) {
        this.dstHost = dstHost;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public String getDstUsername() {
        return dstUsername;
    }

    public void setDstUsername(String dstUsername) {
        this.dstUsername = dstUsername;
    }

    public String getDstPassword() {
        return dstPassword;
    }

    public void setDstPassword(String dstPassword) {
        this.dstPassword = dstPassword;
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
