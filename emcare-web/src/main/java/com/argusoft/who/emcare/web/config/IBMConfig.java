package com.argusoft.who.emcare.web.config;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class IBMConfig {

    private IBMConfig() {
    }

    @Autowired
    private Environment env;

    public LanguageTranslator getLanguageTranslatorInstance() {
        @SuppressWarnings("deprecation") IamAuthenticator authenticator = new IamAuthenticator(env.getProperty("ibm.access-key"));
        LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
        languageTranslator.setServiceUrl(env.getProperty("ibm.access-url"));
        return languageTranslator;
    }
}
