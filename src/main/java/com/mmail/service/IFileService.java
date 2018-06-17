package com.mmail.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by cys on 2018/5/29.
 */
public interface IFileService {
    String upLoad(MultipartFile file, String path);
}
