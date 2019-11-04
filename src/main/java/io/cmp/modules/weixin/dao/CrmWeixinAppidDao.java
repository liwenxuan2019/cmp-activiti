package io.cmp.modules.weixin.dao;

import io.cmp.modules.weixin.entity.CrmWeixinAppidEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信appidtoken表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-11-04 11:20:33
 */
@Mapper
public interface CrmWeixinAppidDao extends BaseMapper<CrmWeixinAppidEntity> {
	
}
