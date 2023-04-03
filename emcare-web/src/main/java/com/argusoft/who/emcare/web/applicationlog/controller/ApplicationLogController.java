package com.argusoft.who.emcare.web.applicationlog.controller;

import com.argusoft.who.emcare.web.applicationlog.service.ApplicationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 03/04/23  9:14 pm
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/application/log")
public class ApplicationLogController {

    @Autowired
    ApplicationLogService applicationLogService;

    @PostMapping("/add")
    public ResponseEntity<Object> addNewApplication(@RequestParam("file") MultipartFile multipartFile,
                                                    @RequestParam("log") String logData) throws Exception {
        return ResponseEntity.ok().body(applicationLogService.addApplicationLog(multipartFile, logData));
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllApplicationLog() throws Exception {
        return ResponseEntity.ok().body(applicationLogService.getAllApplicationLogs());
    }
}
