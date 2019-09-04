

package io.cmp.modules.sys.form;

import lombok.Data;

/**
 * 登录表单
 *
 * @author
 */
@Data
public class SysLoginForm {
    private String username;
    private String password;
    private String extension;
}
