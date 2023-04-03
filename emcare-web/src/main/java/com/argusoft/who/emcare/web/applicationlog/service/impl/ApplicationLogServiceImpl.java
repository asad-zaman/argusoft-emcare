package com.argusoft.who.emcare.web.applicationlog.service.impl;

import com.argusoft.who.emcare.web.applicationlog.entity.ApplicationLog;
import com.argusoft.who.emcare.web.applicationlog.repository.ApplicationLogRepository;
import com.argusoft.who.emcare.web.applicationlog.service.ApplicationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private final Path root = Paths.get("resources");

    @Autowired
    ApplicationLogRepository applicationLogRepository;

    @Value("${server.url}")
    String serverURL;

    @Override
    public Map<String, String> addApplicationLog(MultipartFile multipartFile, String logData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ApplicationLog applicationLog;
        try {
            Files.copy(multipartFile.getInputStream(), this.root.resolve(multipartFile.getOriginalFilename()));
            applicationLog = objectMapper.readValue(logData, ApplicationLog.class);
            applicationLog.setApplicationName(multipartFile.getOriginalFilename());
            applicationLog.setUrl(serverURL + multipartFile.getOriginalFilename());
            applicationLogRepository.save(applicationLog);
        } catch (Exception ex) {
            throw new Exception();
        }
        return Map.of(
                "message", "Successfully Uploaded",
                "code", HttpStatus.OK.toString()
        );
    }

    @Override
    public List<ApplicationLog> getAllApplicationLogs() {
        return applicationLogRepository.findAllByOrderByCreatedOnDesc();
    }
}
