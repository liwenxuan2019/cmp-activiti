package io.cmp.modules.weixin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 微信appidtoken表
 * 
 * @author liwenxuan
 * @email liwenxuan@sinosoft.com.cn
 * @date 2019-11-04 11:20:33
 */
@Data
@TableName("crm_weixin_appid")
public class CrmWeixinAppidEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private String id;
	/**
	 * appid
	 */
	private String appid;
	/**
	 * access_token
	 */
	private String accessToken;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
