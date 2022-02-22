package com.argusoft.who.emcare.web.config;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import org.springframework.stereotype.Component;

@Component
public class IBMConfig {

    public static final String IBM_KEY = "mOtkmk184J0EipDrfQAwi4MQv_zS3oSrWWd1RUVgrBsa";
    public static final String IBM_URL = "https://api.au-syd.language-translator.watson.cloud.ibm.com/instances/48aeb1e3-672a-405b-b769-ead24a6dd824";

    public static LanguageTranslator getLanguageTranslatorInstance() {
        IamAuthenticator authenticator = new IamAuthenticator(IBM_KEY);
        LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
        languageTranslator.setServiceUrl(IBM_URL);
        return languageTranslator;
    }
}
