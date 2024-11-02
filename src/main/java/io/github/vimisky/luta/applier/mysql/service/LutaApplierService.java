package io.github.vimisky.luta.applier.mysql.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vimisky.luta.applier.mysql.dao.LutaApplierChannelMapper;
import io.github.vimisky.luta.applier.mysql.dao.LutaApplierMappingMapper;
import io.github.vimisky.luta.applier.mysql.dao.LutaApplierTaskMapper;
import io.github.vimisky.luta.applier.mysql.dao.LutaApplierTransactionMapper;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierMapping;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import io.github.vimisky.luta.applier.mysql.processor.LutaMySQLUtils;
import io.github.vimisky.luta.applier.mysql.utils.LutaBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Service
public class LutaApplierService {

    private static final Logger logger = LoggerFactory.getLogger(LutaApplierService.class);

    @Autowired
    private LutaApplierChannelMapper lutaApplierChannelMapper;
    @Autowired
    private LutaApplierTaskMapper lutaApplierTaskMapper;
    @Autowired
    private LutaApplierTransactionMapper lutaApplierTransactionMapper;
    @Autowired
    private LutaApplierMappingMapper lutaApplierMappingMapper;


    @Transactional
    public void fillTaskConfig(LutaApplierTask lutaApplierTask){
        Long lutaApplierChannelId = lutaApplierTask.getApplierChannelId();
        List<LutaApplierChannel> applierChannelList = lutaApplierChannelMapper.findById(lutaApplierChannelId);
        if (applierChannelList.size()>0){
            LutaApplierChannel applierChannel = applierChannelList.get(0);
            lutaApplierTask.setLutaApplierChannel(applierChannel);
        }
    }

    @Transactional
    public LutaApplierTask getApplierTask(String uuid){
        LutaApplierTask applierTask = null;
        List<LutaApplierTask> applierTaskList = lutaApplierTaskMapper.findByUuid(uuid);
        if (applierTaskList.size()>0){
            applierTask = applierTaskList.get(0);
            this.fillTaskConfig(applierTask);
        }
        return applierTask;
    }

    @Transactional
    public LutaApplierTask getBriefApplierTask(String uuid){
        LutaApplierTask applierTask = null;
        List<LutaApplierTask> applierTaskList = lutaApplierTaskMapper.findByUuid(uuid);
        if (applierTaskList.size()>0){
            applierTask = applierTaskList.get(0);
        }
        return applierTask;
    }

    @Transactional
    public List<LutaApplierTask> getApplierTaskList(){
        List<LutaApplierTask> lutaApplierTaskList = lutaApplierTaskMapper.findAll();
        for (LutaApplierTask lutaApplierTask: lutaApplierTaskList){
            this.fillTaskConfig(lutaApplierTask);
        }
        return lutaApplierTaskList;
    }

    @Transactional
    public LutaApplierTask createApplierTask(LutaApplierChannel lutaApplierChannel){
        this.createApplierChannel(lutaApplierChannel);
        LutaApplierTask lutaApplierTask = new LutaApplierTask(lutaApplierChannel);
        lutaApplierTaskMapper.insert(lutaApplierTask);
        return lutaApplierTask;
    }

    @Transactional
    public void updateApplierTaskConfig(String taskUUID, LutaApplierChannel lutaApplierChannel){
        LutaApplierTask lutaApplierTask = this.getApplierTask(taskUUID);
        LutaApplierChannel oldLutaApplierChannel = lutaApplierTask.getLutaApplierChannel();
        if (oldLutaApplierChannel != null){
            LutaBeanUtils.copyPropertiesIgnoreNull(lutaApplierChannel, oldLutaApplierChannel);
            lutaApplierChannelMapper.update(oldLutaApplierChannel);
        }
    }

    @Transactional
    public void deleteApplierTask(String taskUUID){
        List<LutaApplierTask> lutaApplierTaskList = lutaApplierTaskMapper.findByUuid(taskUUID);
        if (lutaApplierTaskList.size()>0){
            LutaApplierTask lutaApplierTask = lutaApplierTaskList.get(0);
            LutaApplierChannel lutaApplierChannel = lutaApplierTask.getLutaApplierChannel();
            if (lutaApplierChannel != null){
                lutaApplierChannelMapper.delete(lutaApplierChannel.getId());
            }
            lutaApplierTaskMapper.delete(lutaApplierTask.getId());
        }
    }

    @Transactional
    public LutaApplierChannel getApplierChannel(Long id){
        LutaApplierChannel applierChannel = null;
        List<LutaApplierChannel> applierChannelList = lutaApplierChannelMapper.findById(id);
        if (applierChannelList.size()>0){
            applierChannel = applierChannelList.get(0);
        }
        return applierChannel;
    }

    @Transactional
    public LutaApplierChannel createApplierChannel(LutaApplierChannel lutaApplierChannel){
        lutaApplierChannelMapper.insert(lutaApplierChannel);
        return lutaApplierChannel;
    }

    @Transactional
    public LutaApplierChannel updateApplierChannel(LutaApplierChannel lutaApplierChannel){
        lutaApplierChannelMapper.update(lutaApplierChannel);
        return lutaApplierChannel;
    }

    @Transactional
    public void deleteApplierChannel(Long id){
        lutaApplierChannelMapper.delete(id);
    }

    @Transactional
    public List<LutaApplierMapping> getApplierMappingAll(){
        List<LutaApplierMapping> lutaApplierMappingList = lutaApplierMappingMapper.findAll();
        return lutaApplierMappingList;
    }
    @Transactional
    public List<LutaApplierMapping> getApplierMappingList(Long applierChannelId){
        return lutaApplierMappingMapper.findByApplierChannelId(applierChannelId);
    }
    @Transactional
    public LutaApplierMapping getApplierMapping(Long id){
        LutaApplierMapping lutaApplierMapping = null;
        List<LutaApplierMapping> lutaApplierMappingList = lutaApplierMappingMapper.findById(id);
        if (lutaApplierMappingList.size()>0){
            lutaApplierMapping = lutaApplierMappingList.get(0);
        }
        return lutaApplierMapping;
    }
    @Transactional
    public void deleteApplierMapping(Long id){lutaApplierMappingMapper.delete(id);}
    @Transactional
    public void createApplierMapping(LutaApplierMapping lutaApplierMapping){
        lutaApplierMappingMapper.insert(lutaApplierMapping);
    }
    @Transactional
    public void updateApplierMapping(LutaApplierMapping lutaApplierMapping){
        lutaApplierMappingMapper.update(lutaApplierMapping);
    }

    @Transactional
    public HashMap<String, String> getMappingBy(String taskUUID, String schemaName, String tableName){

        HashMap<String, String> retHashMap= new HashMap<>();
        LutaApplierTask lutaApplierTask = this.getBriefApplierTask(taskUUID);
        Long applierChannelId = lutaApplierTask.getApplierChannelId();
        List<LutaApplierMapping> lutaApplierMappingList = lutaApplierMappingMapper.findBySchemaName(applierChannelId, 0, schemaName);
        if (lutaApplierMappingList.size()>0){
                retHashMap.put("schemaName", lutaApplierMappingList.get(0).getDstSchemaName());
                retHashMap.put("tableName", tableName);
        }else {
            lutaApplierMappingList = lutaApplierMappingMapper.findByTableName(applierChannelId, 1, schemaName, tableName);
            if (lutaApplierMappingList.size()>0){
                retHashMap.put("schemaName", lutaApplierMappingList.get(0).getDstSchemaName());
                retHashMap.put("tableName", lutaApplierMappingList.get(0).getDstTableName());
            }else {
                retHashMap.put("schemaName", schemaName);
                retHashMap.put("tableName", tableName);
            }
        }
        return retHashMap;
    }

    public void updateTaskStopStatus(String taskUUID) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(0);
            lutaTask.setExecuteStopTime(new Date());
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }

    public void updateTaskStartStatus(String taskUUID) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(1);
            lutaTask.setExecuteStartTime(new Date());
            lutaTask.setExecuteMessage("");
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }
    public void updateTaskHeartbeatStatus(String taskUUID) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(2);
            lutaTask.setHeartbeatTimestamp(new Date());
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }
    public void updateTaskLastMessageStatus(String taskUUID) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(3);
            lutaTask.setLastMessageTime(new Date());
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }

    public void updateTaskLastOutputTrxStatus(String taskUUID, String outputTrx) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(4);
            lutaTask.setLastApplierTrxTime(new Date());
            lutaTask.setLastApplierTrx(outputTrx);
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }

    public void updateTaskErrorStatus(String taskUUID, String errorMessage) {
        LutaApplierTask lutaTask = this.getBriefApplierTask(taskUUID);
        if (lutaTask != null){
            lutaTask.setExecuteStatus(-1);
            lutaTask.setExecuteMessage(errorMessage);
            this.lutaApplierTaskMapper.update(lutaTask);
        }
    }

    private Long getLongFromObject(Object o){
        Long ret = null;
        if (o instanceof Integer){
            ret = Long.valueOf((Integer)o) ;
        }else if (o instanceof Long){
            ret = (Long) o;
        }
        return ret;
    }

    public String buildDDLSQL(String taskUUID, String ddlSql) {
        StringBuffer sbDDLSql = new StringBuffer();
        String schemaName = null;
        String tableName = null;

        String[] ddlSqlElements = ddlSql.split(":");
        String sql = null;
        if (ddlSqlElements.length>1){
            schemaName = ddlSqlElements[0];
            sql = ddlSql.substring(schemaName.length()+1);
        }else {
            String errMsg = "遇到有问题的ddl sql: " + ddlSql;
            logger.error(errMsg);
            return sbDDLSql.toString();
        }
        String[] sqlElements = sql.split("\\s+");
        if (sqlElements.length>2 && sqlElements[0].equalsIgnoreCase("alter") && sqlElements[1].equalsIgnoreCase("table")){
            String tableEntireName = sqlElements[2];
            String[] tableNameElements = tableEntireName.split("\\.");
            if (tableNameElements.length>1){
                schemaName = tableNameElements[0].replaceAll("`","");
                tableName = tableNameElements[1].replaceAll("`","");
            }else {
                tableName = tableNameElements[0].replaceAll("`","");
            }
            HashMap<String, String> stHashMap = this.getMappingBy(taskUUID, schemaName,tableName);
            schemaName = stHashMap.get("schemaName");
            tableName = stHashMap.get("tableName");

            sbDDLSql.append(sqlElements[0]).append(" ").append(sqlElements[1]).append(" ").append(schemaName).append(".").append(tableName);
            for (int i=3;i<sqlElements.length;i++){
                sbDDLSql.append(" ").append(sqlElements[i]);
            }
        }

        return sbDDLSql.toString();
    }

    @Deprecated
    @Transactional
    public void createApplierTrx(String taskUUID, Map<String, Object> map) throws JsonProcessingException {

        List<LutaApplierTransaction> applierTransactionList = new ArrayList<>();
        Long serverId = getLongFromObject(map.get("serverId"));
        String binlogFileName = (String) map.get("binlogFilename");
        Long nextPosition =  getLongFromObject(map.get("nextPosition")) ;
        Long xid = getLongFromObject( map.get("xid"));
        boolean dml = (boolean) map.get("dml");
        LutaApplierTask applierTask = this.getBriefApplierTask(taskUUID);
        Long applierChannelId = applierTask.getApplierChannelId();

        if (dml){
            List<Map> rowRecordMapList = (List<Map>) map.get("rowRecordList");
            for(Map<String, Object> rowRecord: rowRecordMapList){
                LutaApplierTransaction applierTransaction = new LutaApplierTransaction();
                applierTransaction.setApplierChannelId(applierChannelId);
                applierTransaction.setServerId(serverId);
                applierTransaction.setBinlogFilename(binlogFileName);
                applierTransaction.setNextPosition(nextPosition);
                applierTransaction.setXid(xid);
                applierTransaction.setDml(dml);
                //根据映射关系修改库名和表名
                //！很重要！！！
                String schemaName = (String) rowRecord.get("schemaName");
                String tableName = (String) rowRecord.get("tableName");

                applierTransaction.setSchemaName(schemaName);
                applierTransaction.setTableName(tableName);

                HashMap<String, String> stHashMap = this.getMappingBy(taskUUID, schemaName,tableName);
                schemaName = stHashMap.get("schemaName");
                tableName = stHashMap.get("tableName");

                applierTransaction.setMappingSchemaName(schemaName);
                applierTransaction.setMappingTableName(tableName);

                String type = (String) rowRecord.get("type");

                applierTransaction.setType(type);

                List<String> columnNameList = (List<String>) rowRecord.get("columnNameList");
                List<Object> columnValueList = (List<Object>) rowRecord.get("columnValueList");
                List<String> oldColumnNameList = (List<String>) rowRecord.get("oldColumnNameList");
                List<Object> oldColumnValueList = (List<Object>) rowRecord.get("oldColumnValueList");
                switch (type){
                    case "insert":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());
                        applierTransaction.setSql(LutaMySQLUtils.buildInsertSql(schemaName, tableName, columnNameList,columnValueList));

                    case "delete":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());

                        applierTransaction.setSql(LutaMySQLUtils.buildDeleteSql(schemaName, tableName, columnNameList,columnValueList));
                        break;
                    case "update":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());
                        applierTransaction.setOldColumnNameList(oldColumnNameList.toString());
                        applierTransaction.setOldColumnValueList(oldColumnValueList.toString());

                        applierTransaction.setSql(LutaMySQLUtils.buildUpdateSql(schemaName, tableName, columnNameList,columnValueList,oldColumnNameList,oldColumnValueList));
                        break;
                    default:
                        break;
                }
                applierTransactionList.add(applierTransaction);
            }
        }else {
            LutaApplierTransaction applierTransaction = new LutaApplierTransaction();
            applierTransaction.setApplierChannelId(applierChannelId);
            applierTransaction.setServerId(serverId);
            applierTransaction.setBinlogFilename(binlogFileName);
            applierTransaction.setNextPosition(nextPosition);
            applierTransaction.setXid(xid);
            applierTransaction.setDml(dml);

            String ddlSql = (String) map.get("ddlSql");
            applierTransaction.setDdlSql(ddlSql);
            applierTransactionList.add(applierTransaction);
        }
        for (LutaApplierTransaction tApplierTrx:applierTransactionList) {
            this.createApplierTrx(tApplierTrx);
        }
    }

//    @Transactional
    public List<LutaApplierTransaction> createApplierTrxs(String taskUUID, Map<String, Object> map) throws JsonProcessingException {

        List<LutaApplierTransaction> applierTransactionList = new ArrayList<>();
        Long serverId = getLongFromObject(map.get("serverId"));
        String binlogFileName = (String) map.get("binlogFilename");
        Long nextPosition =  getLongFromObject(map.get("nextPosition")) ;
        Long xid = getLongFromObject( map.get("xid"));
        boolean dml = (boolean) map.get("dml");
        LutaApplierTask applierTask = this.getBriefApplierTask(taskUUID);
        Long applierChannelId = applierTask.getApplierChannelId();

        if (dml){
            List<Map> rowRecordMapList = (List<Map>) map.get("rowRecordList");
            for(Map<String, Object> rowRecord: rowRecordMapList){
                LutaApplierTransaction applierTransaction = new LutaApplierTransaction();
                applierTransaction.setApplierChannelId(applierChannelId);
                applierTransaction.setServerId(serverId);
                applierTransaction.setBinlogFilename(binlogFileName);
                applierTransaction.setNextPosition(nextPosition);
                applierTransaction.setXid(xid);
                applierTransaction.setDml(dml);
                //根据映射关系修改库名和表名
                //！很重要！！！
                String schemaName = (String) rowRecord.get("schemaName");
                String tableName = (String) rowRecord.get("tableName");

                applierTransaction.setSchemaName(schemaName);
                applierTransaction.setTableName(tableName);

                HashMap<String, String> stHashMap = this.getMappingBy(taskUUID, schemaName,tableName);
                schemaName = stHashMap.get("schemaName");
                tableName = stHashMap.get("tableName");

                applierTransaction.setMappingSchemaName(schemaName);
                applierTransaction.setMappingTableName(tableName);

                String type = (String) rowRecord.get("type");

                applierTransaction.setType(type);

                List<String> columnNameList = (List<String>) rowRecord.get("columnNameList");
                List<Object> columnValueList = (List<Object>) rowRecord.get("columnValueList");
                List<String> oldColumnNameList = (List<String>) rowRecord.get("oldColumnNameList");
                List<Object> oldColumnValueList = (List<Object>) rowRecord.get("oldColumnValueList");
                switch (type){
                    case "insert":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());
                        applierTransaction.setSql(LutaMySQLUtils.buildInsertSql(schemaName, tableName, columnNameList,columnValueList));
                        break;
                    case "delete":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());

                        applierTransaction.setSql(LutaMySQLUtils.buildDeleteSql(schemaName, tableName, columnNameList,columnValueList));
                        break;
                    case "update":
                        applierTransaction.setColumnNameList(columnNameList.toString());
                        applierTransaction.setColumnValueList(columnValueList.toString());
                        applierTransaction.setOldColumnNameList(oldColumnNameList.toString());
                        applierTransaction.setOldColumnValueList(oldColumnValueList.toString());

                        applierTransaction.setSql(LutaMySQLUtils.buildUpdateSql(schemaName, tableName, columnNameList,columnValueList,oldColumnNameList,oldColumnValueList));
                        break;
                    default:
                        break;
                }
                applierTransactionList.add(applierTransaction);
            }
        }else {
            LutaApplierTransaction applierTransaction = new LutaApplierTransaction();
            applierTransaction.setApplierChannelId(applierChannelId);
            applierTransaction.setServerId(serverId);
            applierTransaction.setBinlogFilename(binlogFileName);
            applierTransaction.setNextPosition(nextPosition);
            applierTransaction.setXid(xid);
            applierTransaction.setDml(dml);

            String ddlSql = (String) map.get("ddlSql");
            applierTransaction.setDdlSql(ddlSql);
            applierTransaction.setSql(this.buildDDLSQL(taskUUID, ddlSql));
            applierTransactionList.add(applierTransaction);
        }

        for (int i = 0; i < applierTransactionList.size(); i++) {
            LutaApplierTransaction element = applierTransactionList.get(i);
            element = this.safeCreateApplierTrx(element);
            applierTransactionList.set(i, element);
        }

        return applierTransactionList;
    }

    @Transactional
    public LutaApplierTransaction safeCreateApplierTrx(LutaApplierTransaction applierTrx){
        LutaApplierTransaction lutaApplierTransaction = this.getApplierTransaction(applierTrx);
        if (lutaApplierTransaction == null){
            this.createApplierTrx(applierTrx);
            lutaApplierTransaction = applierTrx;
        }
        return lutaApplierTransaction;
    }

    @Transactional
    public void deleteApplierTrxsCompleted(String taskUUID){
        LutaApplierTask applierTask = this.getBriefApplierTask(taskUUID);
        Long applierChannelId = applierTask.getApplierChannelId();
        this.lutaApplierTransactionMapper.deleteCompletedByChannelId(applierChannelId);
    }

    @Transactional
    public void deleteApplierTrx(Long id){lutaApplierMappingMapper.delete(id);}
    @Transactional
    public void createApplierTrx(LutaApplierTransaction lutaApplierTransaction){
        lutaApplierTransactionMapper.insert(lutaApplierTransaction);
    }
    @Transactional
    public void updateApplierTrx(LutaApplierTransaction lutaApplierTransaction){
        lutaApplierTransactionMapper.update(lutaApplierTransaction);
    }
    @Transactional
    public List<LutaApplierTransaction> getApplierTrxList(Long applierChannelId){
        return lutaApplierTransactionMapper.findByApplierChannelId(applierChannelId);
    }
    @Transactional
    public List<LutaApplierTransaction> getApplierTrxList(String taskUUID){
        List<LutaApplierTransaction> lutaApplierTransactionList = null;
        LutaApplierTask lutaApplierTask = this.getBriefApplierTask(taskUUID);
        if (lutaApplierTask!=null){
            lutaApplierTransactionList = lutaApplierTransactionMapper.findByApplierChannelId(lutaApplierTask.getApplierChannelId());
        }
        return lutaApplierTransactionList;
    }
    @Transactional
    public LutaApplierTransaction getApplierTransaction(Long id){
        LutaApplierTransaction lutaApplierTransaction = null;
        List<LutaApplierTransaction> lutaApplierTransactionList = lutaApplierTransactionMapper.findById(id);
        if (lutaApplierTransactionList.size()>0){
            lutaApplierTransaction = lutaApplierTransactionList.get(0);
        }
        return lutaApplierTransaction;
    }

    public LutaApplierTransaction getApplierTransaction(LutaApplierTransaction applierTrx){
        LutaApplierTransaction lutaApplierTransaction = null;
        List<LutaApplierTransaction> lutaApplierTransactionList =
                lutaApplierTransactionMapper.findBySpecific(
                        applierTrx.getApplierChannelId(),
                        applierTrx.getServerId(),
                        applierTrx.getBinlogFilename(),
                        applierTrx.getNextPosition(),
                        applierTrx.getXid(),
                        applierTrx.getSql()
                );
        if (lutaApplierTransactionList.size()>0){
            lutaApplierTransaction = lutaApplierTransactionList.get(0);
        }
        return lutaApplierTransaction;
    }

    @Transactional
    public void updateApplierTrxStatus(Long id, Integer status){
        LutaApplierTransaction applierTransaction = this.getApplierTransaction(id);
        applierTransaction.setApplierStatus(status);
        if (status == 1){
            applierTransaction.setApplierTime(new Date());
        }
        this.updateApplierTrx(applierTransaction);
    }
    @Transactional
    public void applyApplierTrx(Long id) throws SQLException, ClassNotFoundException {
        LutaApplierTransaction applierTransaction = this.getApplierTransaction(id);
        Long applierChannelId = applierTransaction.getApplierChannelId();
        LutaApplierChannel lutaApplierChannel = this.getApplierChannel(applierChannelId);

        //配置MySQL
        String dstDriverClassName = lutaApplierChannel.getDstDriverClassName();
        String dstHost = lutaApplierChannel.getDstHost();
        Integer dstPort = lutaApplierChannel.getDstPort();
        String dstUsername = lutaApplierChannel.getDstUsername();
        String dstPassword = lutaApplierChannel.getDstPassword();
        //Factory从配置文件读取，这里从数据库里读取，可以直接复制给DruidSource。
        Class.forName(dstDriverClassName);
        String url = "jdbc:mysql://" + dstHost + ":" + dstPort;
//        druidDataSource.setUrl(url);
//        druidDataSource.setUsername(dstUsername);
//        druidDataSource.setPassword(dstPassword);

        Connection conn = DriverManager.getConnection(url, dstUsername, dstPassword);
        boolean oriAutoCommit = true;

        try {

            //入库
            //这里返回的类破坏了隔离性，没用Map。调用就调用吧，整个处理过程也没多少代码。

//            conn = druidDataSource.getConnection();
            oriAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            Statement statement = conn.createStatement();
            statement.addBatch(applierTransaction.getSql());
            statement.executeBatch();
            conn.commit();

            applierTransaction.setApplierStatus(1);
            applierTransaction.setApplierTime(new Date());
            this.updateApplierTrx(applierTransaction);

        }catch (Exception e){
            logger.error("手工应用SQL语句遇到异常");
            e.printStackTrace();
            if (conn != null && !conn.isClosed()){
                conn.rollback();
            }
        }finally {
            if (conn != null && !conn.isClosed()){
                conn.setAutoCommit(oriAutoCommit);
            }
            conn.close();
        }

    }
}
