package com.argusoft.who.emcare.web.indicators.codes.controller;

import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.codes.dto.CustomCodeRequestDto;
import com.argusoft.who.emcare.web.indicators.codes.service.CustomCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:39 am
 */
@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/custom/code")
public class CustomCodeController {


    @Autowired
    CustomCodeService customCodeService;

    @PostMapping("/add")
    public ResponseEntity<Object> addNewCodeSystem(@RequestBody CustomCodeRequestDto customCodeRequestDto) {
        return customCodeService.createCustomCode(customCodeRequestDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateCodeSystem(@RequestBody CustomCodeRequestDto customCodeRequestDto) {
        return customCodeService.updateCustomCodeSystem(customCodeRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllCustomCode() {
        return customCodeService.getAllCustomCodeSystem();
    }

    @GetMapping("/{codeId}")
    public ResponseEntity<Object> getCustomCodeById(@PathVariable(value = "codeId") Long codeId) {
        return customCodeService.getCustomCodeById(codeId);
    }

    @GetMapping("/page")
    public PageDto getCustomCodePage(@RequestParam(value = "pageNo") Integer pageNo,
                                     @Nullable @RequestParam(value = "search", required = false) String searchString) {
        return customCodeService.getCustomCodeWithPagination(pageNo, searchString);
    }
}
