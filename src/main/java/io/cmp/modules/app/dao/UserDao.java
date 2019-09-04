

package io.cmp.modules.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.cmp.modules.app.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 *
 * @author
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
