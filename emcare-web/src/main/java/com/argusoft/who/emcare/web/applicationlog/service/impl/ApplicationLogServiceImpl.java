package com.argusoft.who.emcare.web.applicationlog.service.impl;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import com.argusoft.who.emcare.web.applicationlog.repository.ApplicationLogRepository;
import com.argusoft.who.emcare.web.applicationlog.service.ApplicationLogService;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 03/04/23  9:16 pm
 */
@Service
public class ApplicationLogServiceImpl implements ApplicationLogService {

    @Autowired
    ApplicationLogRepository applicationLogRepository;

    @Override
    public ResponseEntity<Object> addApplicationLog(MultipartFile multipartFile, String logData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ApplicationLog applicationLog;
        try {
            String path = System.getProperty("user.dir") + File.separator + "resources" + File.separator + multipartFile.getOriginalFilename();
            System.out.println("======"+path);
            Files.copy(multipartFile.getInputStream(), Paths.get(path));
            applicationLog = objectMapper.readValue(logData, ApplicationLog.class);
            applicationLog.setApplicationName(multipartFile.getOriginalFilename());
            applicationLog.setUrl(CommonConstant.DOC_DOWNLOAD_PATH + multipartFile.getOriginalFilename());
            applicationLogRepository.save(applicationLog);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(new Response("File already exist!", HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(Map.of(
                "message", "Successfully Uploaded",
                "code", HttpStatus.OK.toString()
        ));
    }

    @Override
    public List<ApplicationLog> getAllApplicationLogs() {
        return applicationLogRepository.findAllByOrderByCreatedOnDesc();
    }

    @Override
    public ApplicationLog getLatestApplicationLogs() {
        ApplicationLog applicationLog = applicationLogRepository.getLatestOne();
        return applicationLog;
    }
}
