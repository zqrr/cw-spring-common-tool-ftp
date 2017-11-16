package cn.cloudwalk.common.tool.ftp;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cloudwalk.common.tool.ftp.config.FTPProperties;




public class FTPUtil {

	private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);
	
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
	public FTPUtil() {}
	
	/**
	 * Constructor
	 * @param properties
	 */
	public FTPUtil(FTPProperties properties) {
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
	public FTPUtil(String host, String port, String userName, String password, String path, String local) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.path = path;
		this.local = local;
	}
	
	/**
	 * Connect and Login.
	 * @param url  IP Address.
	 * @param port  
	 * @param userName
	 * @param password
	 */
	private FTPClient connect(){
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(host, Integer.valueOf(port));
			ftpClient.login(userName, password);
			ftpClient.enterLocalPassiveMode();
			//ftpClient.setBufferSize(10000);
			ftpClient.setControlKeepAliveTimeout(2000000);
			logger.info("Connected the FTPServer Successfully, IP:" + 
								ftpClient.getRemoteAddress().getHostName());
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ftpClient;
	}
	
	/**
	 * Disconnect from the FTPServer.
	 */
	private void close (FTPClient ftpClient) {
		try {
			if (ftpClient != null) {
				ftpClient.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * DownLoad file the files from path to local.
	 * @param fileName
	 */
	public void downloadFile (String fileName) {
		FTPClient ftpClient = this.connect();
		long start = System.currentTimeMillis();
		try {
			ftpClient.changeWorkingDirectory(this.path);
			File localFile = new File(this.local + fileName);
			FileOutputStream fos = new FileOutputStream(localFile);
			ftpClient.retrieveFile(fileName,fos);
			fos.close();
			logger.info(fileName + "文件下载完毕,耗时:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(fileName + "文件下载失败.");
		} finally {
			this.close(ftpClient);
		}
		
	}
	
	/**
	 * Upload file from local to path.
	 * @param fileName
	 */
	public void uploadFile (String fileName) {
		FTPClient ftpClient = this.connect();
		long start = System.currentTimeMillis();
		try {
			if (!ftpClient.changeWorkingDirectory(this.path)) {
				ftpClient.makeDirectory(this.path);
				ftpClient.changeWorkingDirectory(this.path);
			}
			InputStream input = new FileInputStream(this.local + fileName);
			ftpClient.storeFile(fileName, input);
			input.close();
			logger.info(fileName + "文件上传完毕,耗时:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(fileName + "文件上传失败.");
		} finally {
			if (ftpClient != null) {
				this.close(ftpClient);
			}
		}
	}
	
	/**
	 * Delete file from the path in FTP Server.
	 * @param fileName
	 */
	public void deleteFile (String fileName) {
		FTPClient ftpClient = this.connect();
		long start = System.currentTimeMillis();
		try {
			ftpClient.changeWorkingDirectory(this.path);
			ftpClient.deleteFile(fileName);
			logger.info(fileName + "文件删除完毕,耗时:" + (System.currentTimeMillis() - start) + "ms.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(fileName + "文件删除失败.");
		} finally {
			if (ftpClient != null) {
				this.close(ftpClient);
			}
		}
	}
	
    /**
     * Show all the files in the path.
     * @param path
     * @return 
     */
	public List<String> showFiles () {
		
		List<String> ret = new ArrayList<String>();
		FTPClient ftpClient = this.connect();
		try {
			ftpClient.changeWorkingDirectory(this.path);
			FTPFile [] files = ftpClient.listFiles(path);
			for (int i = 1; i <= files.length; i++) {
				ret.add(files[i-1].getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	FTPUtil test = new FTPUtil("localhost", "21", "zq", "123456", "/sftp/cc/", "d:/");
    	test.uploadFile("install.log");
    	//test.downloadFile("install.log");
    	//test.showFiles();
    }
}
