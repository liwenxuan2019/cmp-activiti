

package io.cmp.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.cmp.common.utils.PageUtils;
import io.cmp.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 * @author
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}
