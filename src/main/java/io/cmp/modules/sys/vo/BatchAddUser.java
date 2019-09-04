package io.cmp.modules.sys.vo;

import io.cmp.common.validator.group.AddGroup;
import io.cmp.common.validator.group.UpdateGroup;
import lombok.Data;


import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchAddUser {

    /**
     * 前缀
     */
    @NotNull(message="前缀不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String prefix;

    /**
     * 后缀
     */
    @NotNull(message="后缀不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer  scopeNum;

    /**
     * 后缀2
     */
    @NotNull(message="后缀不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer  scopeNum1;

    private static final long serialVersionUID = 1L;

    /**
     * 部门Id
     */
    private Long deptId;

    /**
     * 角色roleIdList
     */
    private List<Long> roleIdList;
    /**
     * 部门负责人
     */
    private Integer isDeptLeader;


}
