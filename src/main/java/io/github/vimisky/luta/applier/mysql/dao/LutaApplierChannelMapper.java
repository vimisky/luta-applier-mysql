package io.github.vimisky.luta.applier.mysql.dao;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LutaApplierChannelMapper {
    public List<LutaApplierChannel> findById(Long id);
    public List<LutaApplierChannel> findAll();
    public int insert(LutaApplierChannel lutaApplierChannel);
    public int update(LutaApplierChannel lutaApplierChannel);
    public int delete(Long id);
}
