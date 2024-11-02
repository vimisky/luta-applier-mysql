package io.github.vimisky.luta.applier.mysql.entity;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vimisky.luta.applier.mysql.processor.LutaMessageDrivenTask;
import io.github.vimisky.luta.applier.mysql.processor.LutaMessageListener;
import io.github.vimisky.luta.applier.mysql.processor.LutaMySQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.*;

public class LutaApplierTask  {
    private Long id;
    private String uuid;
    private Long applierChannelId;
    private Date heartbeatTimestamp;
    private String executeNode;
    private Date executeStartTime;
    private Date executeStopTime;
    private Integer executeStatus;
    private String executeMessage;
    private Date lastMessageTime;
    private Date lastApplierTrxTime;
    private String lastApplierTrx;
    private String recBinlogFilename;
    private Long recBinlogNextPosition;
    private String recGtidNext;
    private Long recXid;
    private Date createTime;
    private Date updateTime;

    private LutaApplierChannel lutaApplierChannel;

    public LutaApplierTask() {
    }

    public LutaApplierTask(LutaApplierChannel lutaApplierChannel) {
        this.setApplierChannelId(lutaApplierChannel.getId());
        this.setUuid(UUID.randomUUID().toString());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getApplierChannelId() {
        return applierChannelId;
    }

    public void setApplierChannelId(Long applierChannelId) {
        this.applierChannelId = applierChannelId;
    }

    public Date getHeartbeatTimestamp() {
        return heartbeatTimestamp;
    }

    public void setHeartbeatTimestamp(Date heartbeatTimestamp) {
        this.heartbeatTimestamp = heartbeatTimestamp;
    }

    public String getExecuteNode() {
        return executeNode;
    }

    public void setExecuteNode(String executeNode) {
        this.executeNode = executeNode;
    }

    public Date getExecuteStartTime() {
        return executeStartTime;
    }

    public void setExecuteStartTime(Date executeStartTime) {
        this.executeStartTime = executeStartTime;
    }

    public Date getExecuteStopTime() {
        return executeStopTime;
    }

    public void setExecuteStopTime(Date executeStopTime) {
        this.executeStopTime = executeStopTime;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Date getLastApplierTrxTime() {
        return lastApplierTrxTime;
    }

    public void setLastApplierTrxTime(Date lastApplierTrxTime) {
        this.lastApplierTrxTime = lastApplierTrxTime;
    }

    public String getLastApplierTrx() {
        return lastApplierTrx;
    }

    public void setLastApplierTrx(String lastApplierTrx) {
        this.lastApplierTrx = lastApplierTrx;
    }

    public String getRecBinlogFilename() {
        return recBinlogFilename;
    }

    public void setRecBinlogFilename(String recBinlogFilename) {
        this.recBinlogFilename = recBinlogFilename;
    }

    public Long getRecBinlogNextPosition() {
        return recBinlogNextPosition;
    }

    public void setRecBinlogNextPosition(Long recBinlogNextPosition) {
        this.recBinlogNextPosition = recBinlogNextPosition;
    }

    public String getRecGtidNext() {
        return recGtidNext;
    }

    public void setRecGtidNext(String recGtidNext) {
        this.recGtidNext = recGtidNext;
    }

    public Long getRecXid() {
        return recXid;
    }

    public void setRecXid(Long recXid) {
        this.recXid = recXid;
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

    public LutaApplierChannel getLutaApplierChannel() {
        return lutaApplierChannel;
    }

    public void setLutaApplierChannel(LutaApplierChannel lutaApplierChannel) {
        this.lutaApplierChannel = lutaApplierChannel;
    }
}
