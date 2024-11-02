package io.github.vimisky.luta.applier.mysql.dao;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaApplierTransactionMapper {
    public List<LutaApplierTransaction> findById(Long id);
    public List<LutaApplierTransaction> findByApplierChannelId(Long applierChannelId);
    public List<LutaApplierTransaction> findAll();
    public List<LutaApplierTransaction> findBySpecific(Long applierChannelId,
                                                       Long serverId,
                                                       String binlogFilename,
                                                       Long nextPosition,
                                                       Long xid,
                                                       String sql
    );
    public int deleteCompletedByChannelId(Long applierChannelId);
    public int insert(LutaApplierTransaction lutaApplierTransaction);
    public int update(LutaApplierTransaction lutaApplierTransaction);
    public int delete(Long id);
}
