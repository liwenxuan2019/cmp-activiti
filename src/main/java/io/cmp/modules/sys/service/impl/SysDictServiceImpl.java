

package io.cmp.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cmp.common.utils.PageUtils;
import io.cmp.common.utils.Query;
import io.cmp.modules.sys.dao.SysDictDao;
import io.cmp.modules.sys.entity.SysDictEntity;
import io.cmp.modules.sys.service.SysDictService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String)params.get("name");
        String type = (String)params.get("type");
        String code = (String)params.get("code");
        String value = (String)params.get("value");
        String orderNum = (String)params.get("orderNum");
        String delFlag = (String)params.get("delFlag");

        IPage<SysDictEntity> page = this.page(
            new Query<SysDictEntity>().getPage(params),
            new QueryWrapper<SysDictEntity>()
                .like(StringUtils.isNotBlank(name),"name", name)
                .like(StringUtils.isNotBlank(type),"type", type)
                .like(StringUtils.isNotBlank(code),"code", code)
                .like(StringUtils.isNotBlank(value),"value", value)
                .eq(StringUtils.isNotBlank(orderNum),"order_num", orderNum)
                .eq(StringUtils.isNotBlank(delFlag),"del_flag", delFlag)
        );

        return new PageUtils(page);
    }

}
