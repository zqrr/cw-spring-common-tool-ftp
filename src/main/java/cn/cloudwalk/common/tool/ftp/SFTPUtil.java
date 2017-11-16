package cn.cloudwalk.common.tool.ftp;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import cn.cloudwalk.common.tool.ftp.config.FTPProperties;
import cn.cloudwalk.common.tool.ftp.sftp.SFTPChannel;
import cn.cloudwalk.common.tool.ftp.sftp.SFTPConstants;


public class SFTPUtil {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
	
	private static final int DEFAULT_TIME_OUT = 60000;
	
	private String host;
	
	private String port;
	
	private String userName;
	
	private String password;
	
	private String path; 
	
	private String local;
	
	private String fileName;
	
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Default Constructor
	 * @param properties
	 */
	public SFTPUtil() {}
	
	/**
	 * Constructor
	 * @param properties
	 */
	public SFTPUtil(FTPProperties properties) {
		this.host = properties.getHost();
		this.port = properties.getPort();
		this.userName = properties.getUserName();
		this.password = properties.getPassword();
		this.path = properties.getPath();
		this.local = properties.getLocal();
	}
	
	/**
	 * Constructor
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param path
	 */
	public SFTPUtil(String host, String port, String userName, String password, String path, String local) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.path = path;
		this.local = local;
	}
	
	/**
	 * get SFTPChannel
	 * @return
	 */
	public SFTPChannel getSFTPChannel() {
        return new SFTPChannel();
    }
	
	/**
	 * get ChannelSftp
	 * @throws JSchException 
	 */
	public ChannelSftp openChannelSftp(SFTPChannel channel) throws JSchException {
		
		Map<String, String> sftpDetails = new HashMap<String, String>();
		sftpDetails.put(SFTPConstants.SFTP_REQ_HOST, host);
		sftpDetails.put(SFTPConstants.SFTP_REQ_PORT, port);
        sftpDetails.put(SFTPConstants.SFTP_REQ_USERNAME, userName);
        sftpDetails.put(SFTPConstants.SFTP_REQ_PASSWORD, password);
        
		try {
			return channel.getChannel(sftpDetails, DEFAULT_TIME_OUT);
		} catch (JSchException e) {
			e.printStackTrace();
			logger.error("sftp get channel wrong." + e.getMessage());
			throw new JSchException("getChannel exception.", e);
			
		}
	}
	
	/**
	 * Disconnect from the SFTPServer.
	 * @throws Exception 
	 */
	public void close(ChannelSftp chSftp, SFTPChannel channel) throws Exception {
		
		try {
			
			if (chSftp != null) {
				chSftp.quit();
			} 
			
			if (channel != null) {
				channel.closeChannel();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("close ChannelSftp wrong." + e.getMessage());
			throw new JSchException("close ChannelSftp exception.", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("close SFTPChannel wrong." + e.getMessage());
			throw new Exception(e);
		}
	}
	
	/**
	 * DownLoad file the files from Server path to local.
	 * @param fileName
	 * @throws Exception 
	 */
	public void downloadFile(String fileName) throws Exception {
		
		SFTPChannel channel = null;
		ChannelSftp chSftp = null;
		long start = System.currentTimeMillis();
		
		try {
			channel = this.getSFTPChannel();
			chSftp = this.openChannelSftp(channel);
			chSftp.get(path + fileName, local);
			logger.info(fileName + "DownLoad file success,time:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (SftpException e) {
			e.printStackTrace();
			logger.error(fileName + "DownLoad file failed.");
			throw new Exception(e);
		} finally {
			this.close(chSftp, channel);
		}
	}
	
	/**
	 * Upload file from local to Server path.
	 * @param fileName
	 * @throws Exception 
	 */
	public void uploadFile(String fileName) throws Exception {
		
		SFTPChannel channel = null;
		ChannelSftp chSftp = null;
		long start = System.currentTimeMillis();
		
		try {
			
			channel = this.getSFTPChannel();
			chSftp = this.openChannelSftp(channel);
			this.checkPath(path);
			chSftp.put(local + fileName, path + fileName, ChannelSftp.OVERWRITE);
			logger.info(fileName + "UpLoad file success,time:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (SftpException e) {
			e.printStackTrace();
			logger.error(fileName + "UpLoad file failed.");
			throw new Exception(e);
		} finally {
			this.close(chSftp, channel);
		}
	}
	
	/**
	 * Delete file from the path in Server.
	 * @param fileName
	 * @throws Exception 
	 */
	public void deleteFile (String fileName) throws Exception {
		SFTPChannel channel = null;
		ChannelSftp chSftp = null;
		long start = System.currentTimeMillis();
		
		try {
			
			channel = this.getSFTPChannel();
			chSftp = this.openChannelSftp(channel);
			chSftp.rm(path + fileName);
			logger.info(fileName + "Delete file success,time:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (SftpException e) {
			e.printStackTrace();
			logger.error(fileName + "Delete file failed.");
			throw new Exception(e);
		} finally {
			this.close(chSftp, channel);
		}
	}
	
    /**
     * Show all the files in the Server path.
     * @param path
     * @return 
     * @throws Exception 
     */
	public Vector showFiles() throws Exception {
		
		Vector ret = new Vector();
		SFTPChannel channel = null;
		ChannelSftp chSftp = null;
		long start = System.currentTimeMillis();
		try {
			
			channel = this.getSFTPChannel();
			chSftp = this.openChannelSftp(channel);
			ret = chSftp.ls(path);
			logger.info(fileName + "show all files success,time:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (SftpException e) {
			e.printStackTrace();
			logger.error(fileName + "show all files failed.");
			throw new Exception(e);
		} finally {
			this.close(chSftp, channel);
		}
		
		System.out.print(ret.toString());
		return ret;
	}
	
	/**
	 * check path, if not exits , mkdir
	 * @param path
	 * @throws Exception 
	 */
	public void checkPath(String path) throws Exception {
		String[] folders = path.split( "/" );
		SFTPChannel channel = null;
		ChannelSftp chSftp = null;
				
		try {
			
			channel = this.getSFTPChannel();
			chSftp = this.openChannelSftp(channel);
			chSftp.cd("/");
			for (String folder : folders) {
			    if (folder.length() > 0) {
			        try {
			        	chSftp.cd(folder);
			        } catch (SftpException e) {
			        	chSftp.mkdir(folder);
				       	chSftp.cd(folder);
			        }
			    }
			}
		} catch (SftpException e) {
			e.printStackTrace();
			logger.error(fileName + "check path failed.");
			throw new Exception(e);
		} finally {
			this.close(chSftp, channel);
		}
	}
	
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	SFTPUtil test = new SFTPUtil("192.168.10.116", "22", "root", "123456", "/sftp/cc/", "d:/");
    	test.uploadFile("install.log");
    	//test.downloadFile("install.log");
    	//test.showFiles();
    }
}
