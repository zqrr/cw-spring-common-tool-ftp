package cn.cloudwalk.common.tool.ftp.config;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
*
* ClassName: FTPProperties <br/>
* Description: FTP服务参数配置对象. <br/>
* Date: 2017年10月26日 下午4:49:49 <br/>
* 
* @author zq
* @version 1.0.0
* @since 1.7
*/
@Component
@ConfigurationProperties(prefix = "cw.ftp")
public class FTPProperties implements Serializable{

	private static final long serialVersionUID = -8687067332135180094L;

	private String host;
	
	private String port;
	
	private String userName;
	
	private String password;
	
	private String path;

	private String local;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
	
}
