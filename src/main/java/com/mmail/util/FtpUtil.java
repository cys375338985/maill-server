package com.mmail.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by cys on 2018/5/29.
 */
public class FtpUtil {
    private static final Logger log = LoggerFactory.getLogger(FTPClient.class);

    private  static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private  static String ftpuser=PropertiesUtil.getProperty("ftp.user");
    private  static String ftppass=PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private  int pord;
    private  String user;
    private  String pwd;
    private  FTPClient ftpClient;
    public static boolean uploadFile(List<File> fileList) throws Exception {
         FtpUtil ftpUtil = new FtpUtil (ftpIp,21,ftpuser,ftppass);
        boolean result = ftpUtil.uploadFile("img",fileList);
        return result;
    }

    private boolean uploadFile(String remotPath, List<File> fileList) throws Exception {
        boolean uploaded = true;
        FileInputStream fis = null;
        if (connectServer()) try {
            ftpClient.changeWorkingDirectory(remotPath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalActiveMode();
            for (File fileItem : fileList) {
                try {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                    fis.close();
                } catch (IOException e) {
                    log.error("上传文件异常: 文件名: " + fileItem.getName() + " ", e.toString());
                }
            }
        } catch (Exception e) {
            log.error("上传文件，异常", e);
            return false;
        } finally {
            try {
                ftpClient.disconnect();

            } catch (IOException e) {
                throw new Exception(e);
            }
        }
        return  true;
    }

    private boolean connectServer() {
        boolean isSusscess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSusscess=ftpClient.login(user,pwd);
        } catch (IOException e) {
            log.error("连接FTP服务器异常",e);
            e.printStackTrace();
        }
        return  isSusscess;
    }

    private FtpUtil(String ip, int pord, String user, String pwd) {
        this.ip = ip;
        this.pord = pord;
        this.user = user;
        this.pwd = pwd;

    }
}
