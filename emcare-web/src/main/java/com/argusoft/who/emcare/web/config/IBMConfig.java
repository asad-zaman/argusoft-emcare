package com.argusoft.who.emcare.web.config;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import org.springframework.stereotype.Component;

@Component
public class IBMConfig {

    public static final String IBM_KEY = "WXI-fgndQ07mYNqMEHcTC5CnqZvqfxy2IjgD-4fgY9z5";
    public static final String IBM_URL = "https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/98adcf9c-495d-4334-834a-a6b3b6f527ca";

    public static LanguageTranslator getLanguageTranslatorInstance() {
        IamAuthenticator authenticator = new IamAuthenticator(IBM_KEY);
        LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
        languageTranslator.setServiceUrl(IBM_URL);
        return languageTranslator;
    }
}
