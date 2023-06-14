package com.argusoft.who.emcare.web.config.flyway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FlyWayScriptGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FlyWayScriptGenerator.class);
    private static final String fileName = "";

    public static void main(String[] args) throws IOException {
        String path = new File("").getAbsolutePath()
            + File.separator
            + "src"
            + File.separator
            + "main"
            + File.separator
            + "resources"
            + File.separator
            + "db"
            + File.separator
            + "migration"
            + File.separator
            + "V"
            + new Date().getTime()
            + "__"
            + fileName
            + ".sql";
        logger.debug(path);
        File file = new File(path);
//        boolean newFile = file.createNewFile();
//        if (newFile) {
//            logger.debug("File has been created successfully");
//        } else {
//            logger.debug("File already present at the specified location");
//        }
    }
}
