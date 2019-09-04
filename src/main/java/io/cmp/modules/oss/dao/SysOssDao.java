

package io.cmp.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.oss.entity.SysOssEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传
 *
 * @author
 */
@Mapper
public interface SysOssDao extends BaseMapper<SysOssEntity> {
	
}
