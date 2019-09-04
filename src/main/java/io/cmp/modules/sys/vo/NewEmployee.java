package io.cmp.modules.sys.vo;

import lombok.Data;

import java.util.List;

/**
 * 引入员工
 */
@Data
public class NewEmployee {
    /**
     * 用户Id
     */


    private List<Integer> userId;
    /**
     * 部门Id
     */
    private Long deptId;
}
