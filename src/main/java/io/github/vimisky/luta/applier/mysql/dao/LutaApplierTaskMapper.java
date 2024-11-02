package io.github.vimisky.luta.applier.mysql.dao;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaApplierTaskMapper {
    public List<LutaApplierTask> findById(Long id);
    public List<LutaApplierTask> findByUuid(String uuid);
    public List<LutaApplierTask> findByApplierChannelId(Long applierChannelId);
    public List<LutaApplierTask> findAll();
    public int insert(LutaApplierTask lutaApplierTask);
    public int update(LutaApplierTask lutaApplierTask);
    public int delete(Long id);
}
