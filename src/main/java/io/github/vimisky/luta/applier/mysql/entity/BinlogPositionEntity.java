package io.github.vimisky.luta.applier.mysql.entity;

public class BinlogPositionEntity {

    public static final Integer REPLICA_TYPE_FILE_POSITION=0;
    public static final Integer REPLICA_TYPE_GTID=1;

    private Integer replicaType = REPLICA_TYPE_FILE_POSITION;
    private Integer gtid;
    private long serverId;
    private String binlogFilename;
    private long binlogPosition;

    public BinlogPositionEntity() {
    }

    public Integer getReplicaType() {
        return replicaType;
    }

    public void setReplicaType(Integer replicaType) {
        this.replicaType = replicaType;
    }

    public Integer getGtid() {
        return gtid;
    }

    public void setGtid(Integer gtid) {
        this.gtid = gtid;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public void setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }
}
