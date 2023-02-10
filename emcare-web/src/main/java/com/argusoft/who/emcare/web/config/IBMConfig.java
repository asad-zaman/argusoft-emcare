package com.argusoft.who.emcare.web.config;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class IBMConfig {

    @Autowired
    private Environment env;

    private IBMConfig() {
    }

    public LanguageTranslator getLanguageTranslatorInstance() {
        @SuppressWarnings("deprecation") IamAuthenticator authenticator = new IamAuthenticator(env.getProperty(CommonConstant.IBM_ACCESS_KEY));
        LanguageTranslator languageTranslator = new LanguageTranslator(CommonConstant.IBM_VERSION_DATE, authenticator);
        languageTranslator.setServiceUrl(env.getProperty(CommonConstant.IBM_ACCESS_URL));
        return languageTranslator;
    }
}
