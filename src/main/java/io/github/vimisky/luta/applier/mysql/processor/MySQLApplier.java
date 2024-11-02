package io.github.vimisky.luta.applier.mysql.processor;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import io.github.vimisky.luta.applier.mysql.service.LutaApplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MySQLApplier implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MySQLApplier.class);

    private String taskUUID;
    private LutaApplierService lutaApplierService;

    private boolean stopFlag = true;
    private JdbcTemplate jdbcTemplate;
    private DataSource jdbcDataSource;
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    public MySQLApplier(String taskUUID, LutaApplierService lutaApplierService) {
        this.taskUUID = taskUUID;
        this.lutaApplierService = lutaApplierService;
    }

    //加载配置，成功返回true，失败返回false
    public boolean loadConfig(){

        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(this.taskUUID);
        LutaApplierChannel lutaApplierChannel = lutaApplierTask.getLutaApplierChannel();
        //创建MQ Receiver
        String srcHost = lutaApplierChannel.getSrcHost();
        Integer srcPort = lutaApplierChannel.getSrcPort();
        String srcVhost = lutaApplierChannel.getSrcVhost();
        String srcUsername = lutaApplierChannel.getSrcUsername();
        String srcPassword = lutaApplierChannel.getSrcPassword();
        String srcQueueName = lutaApplierChannel.getSrcQueueName();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(srcHost);
        connectionFactory.setPort(srcPort);
        connectionFactory.setVirtualHost(srcVhost);
        connectionFactory.setUsername(srcUsername);
        connectionFactory.setPassword(srcPassword);

        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueueNames(srcQueueName);
//        simpleMessageListenerContainer.setBatchSize(1);
        simpleMessageListenerContainer.setPrefetchCount(1);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        //不用代理模式了，太费劲了。LutaMessageProcessor已删除。
//        LutaMessageProcessor lutaMessageProcessor = new LutaMessageProcessor();
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
//        messageListenerAdapter.setDelegate(lutaMessageProcessor);
//        messageListenerAdapter.setDefaultListenerMethod("onPlainMessage");
        //添加处理映射
        //messageListenerAdapter.addQueueOrTagToMethodName("queueName", "methodName");
        //method的参数类型，根据convert结果不同，都要设置一下。
        //默认情况下，传入Rabbit消息的内容在传递到目标侦听器方法之前被提取，
        // 以使目标方法对消息内容类型（如String或字节数组）而不是原始消息进行操作。
        //消息类型转换委托给Spring AMQ MessageConverter。
        // 默认情况下，将使用SimpleMessageConverter。
        // （如果您不希望发生这种自动消息转换，请确保将MessageConverter设置为null。）
//        messageListenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
//        messageListenerAdapter.setMessageConverter(null);

        //MessageAckListener是自动ack后回调执行.MessageListener是接收消息后执行回调.
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //配置MySQL
        String dstDriverClassName = lutaApplierChannel.getDstDriverClassName();
        String dstHost = lutaApplierChannel.getDstHost();
        Integer dstPort = lutaApplierChannel.getDstPort();
        String dstUsername = lutaApplierChannel.getDstUsername();
        String dstPassword = lutaApplierChannel.getDstPassword();
        //Factory从配置文件读取，这里从数据库里读取，可以直接复制给DruidSource。
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(dstDriverClassName);

        druidDataSource.setUrl("jdbc:mysql://" + dstHost + ":" + dstPort);
        druidDataSource.setUsername(dstUsername);
        druidDataSource.setPassword(dstPassword);

        this.jdbcDataSource = druidDataSource;

        simpleMessageListenerContainer.setMessageListener(this);
        this.simpleMessageListenerContainer = simpleMessageListenerContainer;
        return true;
    }

    public boolean start(){
        boolean ret = false;

        try {
            this.loadConfig();
            this.simpleMessageListenerContainer.start();
            this.stopFlag = false;
            ret = true;
            this.lutaApplierService.updateTaskStartStatus(this.taskUUID);
        }catch (Exception e){
            logger.error("任务"+ this.taskUUID+":"+"启动任务遇到异常");
            e.printStackTrace();
            this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, e.getMessage());
            return false;
        }
        return ret;
    }

    public boolean stop(){
        boolean ret = false;
        try {
            this.simpleMessageListenerContainer.stop();
            DruidDataSource druidDataSource = (DruidDataSource)this.jdbcDataSource;
            druidDataSource.close();
            ret = true;
            this.stopFlag = true;
            this.lutaApplierService.updateTaskStopStatus(this.taskUUID);
        }catch (Exception e){
            logger.error("任务"+ this.taskUUID+":"+"停止任务遇到异常");
            e.printStackTrace();
            this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, e.getMessage());
            return false;
        }
        return ret;
    }

    //未完成，卡在了preparedstatement的presql的编写上，对于null值的处理，缺少条件
    private List<PreparedStatement> builidPreparedStatement(Connection conn, String msg) throws JsonProcessingException {
        List<PreparedStatement> pss = new ArrayList<>();
        HashMap map = new ObjectMapper().readValue(msg, HashMap.class);
        boolean isDML = (boolean) map.get("isDML");

        if (isDML) {
            List<Map> rowRecordMapList = (List<Map>) map.get("rowRecordList");
            for (Map<String, Object> rowRecord : rowRecordMapList) {
                //根据映射关系修改库名和表名
                //！很重要！！！
                String schemaName = (String) rowRecord.get("schemaName");
                String tableName = (String) rowRecord.get("tableName");
                HashMap<String, String> stHashMap = lutaApplierService.getMappingBy(taskUUID, schemaName, tableName);
                schemaName = stHashMap.get("schemaName");
                tableName = stHashMap.get("tableName");

                String type = (String) rowRecord.get("type");
                List<String> columnNameList = (List<String>) rowRecord.get("columnNameList");
                List<Object> columnValueList = (List<Object>) rowRecord.get("columnValueList");
                List<String> oldColumnNameList = (List<String>) rowRecord.get("oldColumnNameList");
                List<Object> oldColumnValueList = (List<Object>) rowRecord.get("oldColumnValueList");

                PreparedStatement ps = null;
                StringBuffer sbPreSql = new StringBuffer();
                switch (type) {
                    case "insert":


                        break;
                    case "update":

                        break;
                    case "delete":

                        break;
                    default:
                        break;
                }
            }

        }
        return pss;
    }

    private Map<String, Object> parseMessage(String msg) throws Exception {
        HashMap map = new ObjectMapper().readValue(msg, HashMap.class);

        return map;
    }

    @Deprecated
    public String buildDDLSQL(String ddlSql) throws Exception {
        StringBuffer sbDDLSql = new StringBuffer();
        Map<String, String> tableNameMap = new HashMap<>();
        String databaseName = null;
        String tableName = null;

        String[] ddlSqlElements = ddlSql.split(":");
        String sql = null;
        if (ddlSqlElements.length>1){
            databaseName = ddlSqlElements[0];
            sql = ddlSql.substring(databaseName.length());
//            sql = ddlSqlElements[1];
        }else {
            String errMsg = "遇到有问题的ddl sql: " + ddlSql;
            lutaApplierService.updateTaskErrorStatus(this.taskUUID, errMsg);
            throw new Exception(errMsg);
        }
        String[] ddlElements = sql.split("\\s+");
        if (ddlElements.length>2 && ddlElements[0].equalsIgnoreCase("alter") && ddlElements[1].equalsIgnoreCase("table")){
            String tableEntireName = ddlElements[2];
            String[] tableNameElements = tableEntireName.split("\\.");
//            String tableSingleName = null;
            if (tableNameElements.length>1){
                databaseName = tableNameElements[0];
                tableName = tableNameElements[1];
                tableNameMap.put("databaseName", databaseName.replaceAll("`",""));
                tableNameMap.put("tableName", tableName.replaceAll("`",""));
            }else {
                tableName = tableNameElements[0];
                tableNameMap.put("databaseName", null);
                tableNameMap.put("tableName", tableName.replaceAll("`",""));
//                tableSingleName = tableEntireName;
            }
//            tableName = tableSingleName.replaceAll("`","");

            sbDDLSql.append(ddlElements[0]).append(" ").append(ddlElements[1]).append(" ").append(databaseName).append(".").append(tableName);
            for (int i=3;i<ddlElements.length;i++){
                sbDDLSql.append(" ").append(ddlElements[i]);
            }
        }

        return sbDDLSql.toString();
    }

    @Deprecated
    public String trxMap2String(Map<String, Object> map) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
//        String databaseName = this.jdbcDataSource.getConnection().getSchema();

        boolean isDML = (boolean) map.get("dml");
//            int xid = (Integer) map.get("xid");
        String binlogFileName = (String) map.get("binlogFilename");
//            long nextPosition =  (Long) map.get("nextPosition");

        if (isDML){
//                stringBuffer.append("begin;");
            List<Map> rowRecordMapList = (List<Map>) map.get("rowRecordList");
            for(Map<String, Object> rowRecord: rowRecordMapList){
                //根据映射关系修改库名和表名
                //！很重要！！！
                String schemaName = (String) rowRecord.get("schemaName");
                String tableName = (String) rowRecord.get("tableName");
                HashMap<String, String> stHashMap = lutaApplierService.getMappingBy(taskUUID, schemaName,tableName);
                schemaName = stHashMap.get("schemaName");
                tableName = stHashMap.get("tableName");

                String type = (String) rowRecord.get("type");
                List<String> columnNameList = (List<String>) rowRecord.get("columnNameList");
                List<Object> columnValueList = (List<Object>) rowRecord.get("columnValueList");
                List<String> oldColumnNameList = (List<String>) rowRecord.get("oldColumnNameList");
                List<Object> oldColumnValueList = (List<Object>) rowRecord.get("oldColumnValueList");
                switch (type){
                    case "insert":
                        stringBuffer.append(LutaMySQLUtils.buildInsertSql(schemaName, tableName, columnNameList,columnValueList));
//                            stringBuffer.append(";");
                        break;
                    case "update":
                        stringBuffer.append(LutaMySQLUtils.buildUpdateSql(schemaName, tableName, columnNameList,columnValueList,oldColumnNameList,oldColumnValueList));
//                            stringBuffer.append(";");
                        break;
                    case "delete":
                        stringBuffer.append(LutaMySQLUtils.buildDeleteSql(schemaName, tableName, columnNameList,columnValueList));
//                            stringBuffer.append(";");
                        break;
                    default:
                        break;
                }
            }
        }else {
            //ddlsql的格式为schemaName:ddl
            String ddlSql = (String) map.get("ddlSql");
            String longDDLSql = this.buildDDLSQL(ddlSql);
            //这里先不执行，待映射转换
            stringBuffer.append(longDDLSql);
//            stringBuffer.append("select 0;");
//                stringBuffer.append(";");
        }

        return stringBuffer.toString();
    }

//v1
    @Deprecated
    public boolean processMessage(Message message) throws SQLException {

        byte[] bbb = message.getBody();
        logger.debug("接收并处理SQL消息: " + new String(bbb));

        Connection conn = null;

        try {
            Map<String, Object> parsedMap = parseMessage(new String(bbb));

            //入库留痕
            this.lutaApplierService.createApplierTrx(this.taskUUID, parsedMap);
            //生成SQL语句
            String parsedSql = trxMap2String(parsedMap);
            String[] sqls = parsedSql.split(";");

            logger.debug("SQL消息解析为:" + parsedSql);

            conn = jdbcDataSource.getConnection();

            conn.setAutoCommit(false);

            Statement statement = conn.createStatement();
            for (int i = 0; i < sqls.length-1; i++) {
                logger.info("应用SQL " + i + ": " + sqls[i]);
                statement.addBatch(sqls[i]);
            }
            statement.executeBatch();
            conn.commit();

//            不适用jdbcTemplate，不能执行多条SQL
//            this.jdbcTemplate.execute(parsedSql);
        }catch (Exception e){
            logger.error("任务" + this.taskUUID + "处理SQL消息遇到异常");
            e.printStackTrace();
            this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, e.getMessage());
            if (conn != null && !conn.isClosed()){
                conn.rollback();
            }
            return false;
        }

        return true;
    }
//v2
    public boolean processMessageGraceful(Message message) throws SQLException {

        byte[] bbb = message.getBody();
        logger.debug("处理SQL消息: " + new String(bbb));

        Connection conn = null;
        boolean oriAutoCommit = true;

        try {
            Map<String, Object> parsedMap = parseMessage(new String(bbb));
            logger.debug("解析消息到Map完成");
            //入库
            //这里返回的类破坏了隔离性，没用Map。调用就调用吧，整个处理过程也没多少代码。
            List<LutaApplierTransaction> applierTrxs = this.lutaApplierService.createApplierTrxs(this.taskUUID, parsedMap);
            logger.info("生成Transaction对象和SQL语句正常");

            conn = jdbcDataSource.getConnection();
            oriAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            logger.debug("获取Connnection正常");
            Statement statement = conn.createStatement();
            logger.debug("事务提交开始");
            for (int i = 0; i < applierTrxs.size(); i++) {

                LutaApplierTransaction applierTrx = applierTrxs.get(i);
                logger.info("事务ID:" + applierTrx.getXid() + " 应用SQL子句 " + i + ": " + applierTrx.getSql());
                //status:0:待执行
                if (applierTrx.getApplierStatus()==null || 0==applierTrx.getApplierStatus()){
                    statement.addBatch(applierTrx.getSql());
                }
            }
            statement.executeBatch();

            conn.commit();
            logger.info("事务提交正常" );

            for (int i = 0; i < applierTrxs.size(); i++) {
                LutaApplierTransaction applierTrx = applierTrxs.get(i);
                //status:1:成功; 0:待执行;
                if (applierTrx.getApplierStatus()==null || 0==applierTrx.getApplierStatus()){
                    applierTrx.setApplierTime(new Date());
                    applierTrx.setApplierStatus(1);
                    lutaApplierService.updateApplierTrx(applierTrx);
                }
            }

        }catch (Exception e){
            logger.error("任务" + this.taskUUID + "处理SQL消息遇到异常");
            e.printStackTrace();
            this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, e.getMessage());
            if (conn != null && !conn.isClosed()){
                conn.rollback();
            }
            return false;
        }finally {
            if (conn != null && !conn.isClosed()){
                logger.info("Druid Connection归还");
                conn.setAutoCommit(oriAutoCommit);
                conn.close();
            }

//            this.stop();
        }

        return true;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        logger.debug("onMessage:接收到新消息");
        boolean processOk = false;
        long deliveryTag = 0L;
        try {
            logger.debug("任务"+this.taskUUID+":更新最后一条消息状态");
            this.lutaApplierService.updateTaskLastMessageStatus(this.taskUUID);
            logger.debug("任务"+this.taskUUID+":更新最后一条消息状态完成");
            MessageProperties messageProperties = message.getMessageProperties();
            deliveryTag = messageProperties.getDeliveryTag();
            logger.debug("onMessage:开始处理新消息");
            if(processMessageGraceful(message)){
                logger.info("任务"+this.taskUUID+": 处理消息正常");
                processOk = true;
                //第二个参数是，是否把小于tag的一起确认了，false，只确认自己。
                channel.basicAck(deliveryTag, false);
                logger.info("任务"+this.taskUUID+": Ack确认消息正常");

                String trxString = new String(message.getBody());
                if (trxString!=null && trxString.length()>2048){
                    this.lutaApplierService.updateTaskLastOutputTrxStatus(this.taskUUID,
                            "SQL太长,仅保存前2048字符,详细的请检查日志:" + trxString.substring(0,2047));
                }else {
                    this.lutaApplierService.updateTaskLastOutputTrxStatus(this.taskUUID, trxString);
                }
                this.lutaApplierService.deleteApplierTrxsCompleted(this.taskUUID);

            }else {
                channel.basicNack(deliveryTag, false, true);
                String errMsg = "处理消息遇到异常，消息退回:";
                logger.error("任务" + this.taskUUID + ":" + errMsg);
                this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, errMsg);
            }


        }catch (Exception e){
            logger.error("任务" + this.taskUUID + ": onMessage遇到异常");
            e.printStackTrace();
            this.lutaApplierService.updateTaskErrorStatus(this.taskUUID, e.getMessage());
        }finally {
            logger.info("任务" + this.taskUUID + ":消息处理完成，finally收摊");
            if (!processOk && deliveryTag!=0L){
                logger.debug("任务" + this.taskUUID + ":消息处理完成，finally收摊, 确定并Requeue消息");
                channel.basicNack(deliveryTag, false, true);
            }
        }


    }
}
