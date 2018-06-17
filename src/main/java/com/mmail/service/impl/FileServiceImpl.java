package com.mmail.service.impl;

import com.google.common.collect.Lists;
import com.mmail.service.IFileService;
import com.mmail.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by cys on 2018/5/29.
 */
@Service("fileService")
public class FileServiceImpl implements IFileService {
    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    public String upLoad(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        File dirFIle = new File(path);
        logger.info("开始上传，上传文件名：{},上传的路径：{},新文件名{}",fileName,path,uploadFileName);
        if (!dirFIle.exists()) {
            dirFIle.setWritable(true);
            dirFIle.mkdirs();
        }
        File targetFile = new File(dirFIle, uploadFileName);
        try {
            file.transferTo(targetFile);
            FtpUtil.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传文件异常",e);
            return null;
        }
        return uploadFileName;
    }

}
