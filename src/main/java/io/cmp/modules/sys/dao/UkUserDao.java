package io.cmp.modules.sys.dao;

import io.cmp.modules.sys.entity.UkUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-08-21 09:44:52
 */
@Mapper
public interface UkUserDao extends BaseMapper<UkUserEntity> {
    public void insertUkUser(UkUserEntity ukUserEntity);
}
