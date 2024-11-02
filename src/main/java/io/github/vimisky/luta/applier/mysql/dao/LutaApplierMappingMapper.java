package io.github.vimisky.luta.applier.mysql.dao;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierMapping;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaApplierMappingMapper {
    public List<LutaApplierMapping> findById(Long id);
    public List<LutaApplierMapping> findByApplierChannelId(Long applierChannelId);
    public List<LutaApplierMapping> findBySchemaName(Long applierChannelId, Integer type, String srcSchemaName);
    public List<LutaApplierMapping> findByTableName(Long applierChannelId, Integer type, String srcSchemaName, String srcTableName);
    public List<LutaApplierMapping> findAll();
    public int insert(LutaApplierMapping lutaApplierMapping);
    public int update(LutaApplierMapping lutaApplierMapping);
    public int delete(Long id);
}
