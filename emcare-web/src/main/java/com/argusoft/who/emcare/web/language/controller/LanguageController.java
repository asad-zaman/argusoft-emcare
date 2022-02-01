package com.argusoft.who.emcare.web.language.controller;

import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/language")
public class LanguageController {

    @Autowired
    LanguageService languageService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllLanguage() {
        return ResponseEntity.ok(languageService.getAllLanguageTranslation());
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addOrUpdateLanguageTranslation(@RequestBody LanguageDto language) {
        return ResponseEntity.ok(languageService.addOrUpdateLanguageTranslation(language));
    }

}
