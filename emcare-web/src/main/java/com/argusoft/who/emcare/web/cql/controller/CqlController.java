package com.argusoft.who.emcare.web.cql.controller;

import com.argusoft.who.emcare.web.cql.EmCareCqlEngine;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin(origins = "**")
@RestController
@RequestMapping("/api/cql")
public class CqlController {

    @GetMapping("/execute")
    public Object executeCql() throws IOException {
        String str = "library Test version '1.0.0'\ndefine X:\n5+5";
        return EmCareCqlEngine.execute(str);
    }
}
