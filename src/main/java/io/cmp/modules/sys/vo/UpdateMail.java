package io.cmp.modules.sys.vo;

import io.cmp.modules.sys.entity.SysUserEntity;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;

/**
 * 修改邮箱或者手机号
 */
@Data
public class UpdateMail{
    /**
     * 邮箱
     */
    //@Pattern(regexp = "^\\s*$|^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    //@Pattern(regexp = "^\\s*$|^1([345789])\\\\d{9}$",message = "手机号格式不正确")
    private String mobile;

}
