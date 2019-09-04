package io.cmp.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.exception.RRException;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysRoleEntity;
import io.cmp.modules.sys.entity.UkUserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 用户表
 *
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-08-21 09:44:52
 */
public interface UkUserService extends IService<UkUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    public void insertUkUser(UkUserEntity ukUserEntity);
}

