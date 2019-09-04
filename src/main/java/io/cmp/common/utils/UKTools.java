package io.cmp.common.utils;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class UKTools {
	private static MD5 md5 = new MD5();
	
	public static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
	
	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
	
	public static SimpleDateFormat timeRangeDateFormat = new SimpleDateFormat("HH:mm");
	
	public static final Pattern INSECURE_URI = Pattern.compile("[\\s\\S]*?(\\.\\.)[\\s\\S]*?");
	
	static{
		dateFormate.setLenient(true);
		simpleDateFormat.setLenient(true);
	}
	/**
	 * 当前时间+已过随机生成的 长整形数字
	 * @return
	 */
	public static String genID(){
		return Base62.encode(getUUID()).toLowerCase() ;
	}
	
	public static String genIDByKey(String key){
		return Base62.encode(key).toLowerCase() ;
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "") ;
	}
	
	public static String getContextID(String session){
		return session.replaceAll("-", "") ;
	}
	
	public static String md5(String str) {
		return md5.getMD5ofStr(md5.getMD5ofStr(str));
	}
	
	public static String md5(byte[] bytes) {
		return md5.getMD5ofByte(bytes);
	}
	
	public static void copyProperties(Object source, Object target, String... ignoreProperties)
	        throws BeansException {
	  
	    Assert.notNull(source, "Source must not be null");
	    Assert.notNull(target, "Target must not be null");
	  
	    Class<?> actualEditable = target.getClass();
	    PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
	    List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
	  
	    for (PropertyDescriptor targetPd : targetPds) {
	        Method writeMethod = targetPd.getWriteMethod();
	        if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {  
	            PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
	            if (sourcePd != null && !targetPd.getName().equalsIgnoreCase("id")) {  
	                Method readMethod = sourcePd.getReadMethod();
	                if (readMethod != null &&  
	                        ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
	                    try {  
	                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
	                            readMethod.setAccessible(true);  
	                        }  
	                        Object value = readMethod.invoke(source);
	                        if(value != null){  //只拷贝不为null的属性 by zhao  
	                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
	                                writeMethod.setAccessible(true);  
	                            }  
	                            writeMethod.invoke(target, value);  
	                        }  
	                    }  
	                    catch (Throwable ex) {
	                        throw new FatalBeanException(
	                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);  
	                    }  
	                }  
	            }  
	        }  
	    }  
	}  
	
	public static long ipToLong(String ipAddress) {
		long result = 0;
		String[] ipAddressInArray = ipAddress.split("\\.");
		if(ipAddressInArray!=null && ipAddressInArray.length == 4){
			for (int i = 3; i >= 0; i--) {
				long ip = Long.parseLong(ipAddressInArray[3 - i]);
	
				// left shifting 24,16,8,0 and bitwise OR
	
				// 1. 192 << 24
				// 1. 168 << 16
				// 1. 1 << 8
				// 1. 2 << 0
				result |= ip << (i * 8);
	
			}
		}
		return result;
	}

	public static String longToIp2(long ip) {

		return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
				+ ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
	}
	/***
	 * ID编码 ， 发送对话的时候使用
	 * @param id
	 * @param nid
	 * @return
	 */
	public static String genNewID(String id , String nid){
		StringBuffer strb = new StringBuffer();
		if(id!=null && nid!=null){
			int length = Math.max(id.length(), nid.length());
			for(int i=0 ; i<length ; i++){
				if(nid.length() > i && id.length() > i){
					int cur = (id.charAt(i) + nid.charAt(i)) / 2 ;
					strb.append((char)cur) ;
				}else if(nid.length() > i){
					strb.append(nid.charAt(i)) ;
				}else{
					strb.append(id.charAt(i)) ;
				}
			}
		}
		return strb.toString() ;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getRequestParam(HttpServletRequest request){
		Map<String, Object> values = new HashMap<String, Object>();
		Enumeration<String> enums = request.getParameterNames() ;
		while(enums.hasMoreElements()){
			String param = enums.nextElement() ;
			values.put(param,request.getParameter(param)) ;
		}
		return values;
	}
	

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getParameter(HttpServletRequest request){
		Enumeration<String> names = request.getParameterNames() ;
		StringBuffer strb = new StringBuffer();
		while(names.hasMoreElements()){
			String name = names.nextElement() ;
			if(name.indexOf("password") < 0){	//不记录 任何包含 password 的参数内容
				if(strb.length() > 0){
					strb.append(",") ;
				}
				strb.append(name).append("=").append(request.getParameter(name)) ;
			}
		}
		return strb.toString() ;
		
	}
	
	/**
	 * 获取一天的开始时间
	 * @return
	 */
	public static Date getStartTime(){
		Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();  
	}
	
	/**
	 * 获取一天的开始时间
	 * @return
	 */
	public static Date getWeekStartTime(){
		Calendar weekStart = Calendar.getInstance();
		weekStart.set(weekStart.get(Calendar.YEAR), weekStart.get(Calendar.MONDAY), weekStart.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return weekStart.getTime();  
	}
	
	/**
	 * 获取一天的开始时间
	 * @return
	 */
	public static Date getLast30Day(){
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.DAY_OF_MONTH, todayStart.get(Calendar.DAY_OF_MONTH) - 30);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();  
	}
	
	/**
	 * 获取一天的开始时间
	 * @return
	 */
	public static Date getLastDay(int days){
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.DAY_OF_MONTH, todayStart.get(Calendar.DAY_OF_MONTH) - days);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();  
	}
	
	/**
	 * 获取指定日期前多少天的开始时间
	 * @return
	 */
	public static Date getLastDay(Date date , int days){
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.DAY_OF_MONTH, todayStart.get(Calendar.DAY_OF_MONTH) - days);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();  
	}
	
	/**
	 * 	获取指定日期后几天的开始时间
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date getNextDay(Date date , int days){
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(date);
		todayStart.set(Calendar.DAY_OF_MONTH, todayStart.get(Calendar.DAY_OF_MONTH) + days);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();  
	}
	
	/**
	 * 获取一天的结束时间
	 * @return
	 */
	public static Date getEndTime(){
		Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();  
	}
	
	/**
	 * 获取一天的结束时间
	 * @return
	 */
	public static Date getLastTime(int secs){
		Calendar todayEnd = Calendar.getInstance();
        todayEnd.add(Calendar.SECOND, secs*-1);
        return todayEnd.getTime();  
	}
	
	public static void noCacheResponse(HttpServletResponse response){
		response.setDateHeader("Expires",0);
        response.setHeader("Buffer","True");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Expires","0");
        response.setHeader("ETag", String.valueOf(System.currentTimeMillis()));
        response.setHeader("Pragma","no-cache");
        response.setHeader("Date", String.valueOf(new Date()));
        response.setHeader("Last-Modified", String.valueOf(new Date()));
	}
	

	/**
	 * 
	 * @param osname
	 * @return
	 */
	public static String processOs(String osname) {
		String group = null ;
		if(!StringUtils.isBlank(osname) && osname.toLowerCase().indexOf("windows")>=0) {
			group = "Windows" ;
		}else if(!StringUtils.isBlank(osname) && osname.toLowerCase().indexOf("android")>=0) {
			group = "Android" ;
		}else if(!StringUtils.isBlank(osname) && osname.toLowerCase().indexOf("ios")>=0) {
			group = "IOS" ;
		}else if(!StringUtils.isBlank(osname) && osname.toLowerCase().indexOf("linux")>=0) {
			group = "Linux" ;
		}else if(!StringUtils.isBlank(osname) && osname.toLowerCase().indexOf("mac os")>=0) {
			group = "Mac OS" ;
		}else {
			group = osname ;
		}
		return group ;
	}
	/**
	 * 
	 * @param browser
	 * @return
	 */
	public static String processBrowser(String browser) {
		String group = null ;
		if(!StringUtils.isBlank(browser) && browser.toLowerCase().indexOf("firefox")>=0) {
			group = "Firefox" ;
		}else if(!StringUtils.isBlank(browser) && browser.toLowerCase().indexOf("chrome")>=0) {
			group = "Chrome" ;
		}else if(!StringUtils.isBlank(browser) && browser.toLowerCase().indexOf("internet explorer")>=0) {
			group = "IE" ;
		}else if(!StringUtils.isBlank(browser) && browser.toLowerCase().indexOf("edge")>=0) {
			group = "Edge" ;
		}else if(!StringUtils.isBlank(browser) && browser.toLowerCase().indexOf("sofari")>=0) {
			group = "Sofari" ;
		}else {
			group = browser ;
		}
		return group ;
	}


	
	public static Map<String, Object> transBean2Map(Object obj) {
		  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法 
                	
                    Method readMethod = property.getReadMethod();
                    
                    if (readMethod != null) {  
                    	Object value = readMethod.invoke(obj);
                    	if(value instanceof Date){
                    		value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value) ;
                    	}
	                    map.put(key, value);
	                }  
                }  
            }  
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }  
  
        return map;  
  
    }
	

	
	public static byte[] toBytes(Object object) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput = new ObjectOutputStream(out);
		objectOutput.writeObject(object);
		return out.toByteArray();
	}

	public static Object toObject(byte[] data) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(input);
		return objectInput.readObject();
	}
	

    public static String getTopic(String snsid , String msgtype , String eventype , String eventkey , String msg){
    	StringBuffer strb = new StringBuffer() ;
		strb.append(snsid) ;
		strb.append(".").append(msgtype) ;
		if(msgtype.equals("text")){
			strb.append(".").append(msg) ;
		}else if(msgtype.equals("event")){
			strb.append(".").append(eventype.toLowerCase()) ;
			if(!StringUtils.isBlank(eventkey)){
				strb.append(".").append(eventkey) ;
			}
		}else{
			strb.append(".").append(msgtype) ;
		}
		return strb.toString() ;
    }
    
    public static String getTopic(String snsid , String msgtype , String eventype){
    	StringBuffer strb = new StringBuffer() ;
		strb.append(snsid) ;
		strb.append(".").append(msgtype) ;
		if(msgtype.equals("text")){
			strb.append(".").append(msgtype) ;
		}else if(msgtype.equals("event")){
			strb.append(".").append(eventype.toLowerCase()) ;
		}else{
			strb.append(".").append(msgtype) ;
		}
		return strb.toString() ;
    }

    public static int dayForWeek(){  
    	 Calendar c = Calendar.getInstance();
    	 int dayForWeek = 0;  
    	 if(c.get(Calendar.DAY_OF_WEEK) == 1){
    	  dayForWeek = 7;  
    	 }else{  
    	  dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
    	 }  
    	 return dayForWeek;   
    }  

	
	public static String getIpAddr(HttpServletRequest request) {
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}
	
	/**
     * 16进制字符串转换为字符串 
     *  
     * @param
     * @return 
     */  
	public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {  
            int ch = (int) strPart.charAt(i);  
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);  
        }  
        return hexString.toString();  
    }
	

	
	public static String encode(Object obj) {
		Base64 base64 = new Base64();
    	try {
			return base64.encodeToString(UKTools.toBytes(obj)) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T decode(String str, Class<T> clazz) {
		Base64 base64 = new Base64();
    	try {
			return (T)UKTools.toObject(base64.decode(str)) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	public static String processContentEncode(String str) throws Exception {
		return Base64.encodeBase64String(str.getBytes("UTF-8")).replaceAll("\\+", "-");
	}
	public static String processContentDecode(String str) throws Exception {
		return new String(Base64.decodeBase64(str.replaceAll("-", "\\+").getBytes()) , "UTF-8");
	}
	

}
