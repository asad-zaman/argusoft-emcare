package com.argusoft.who.emcare.web.language.service.impl;

import com.argusoft.who.emcare.web.config.IBMConfig;
import com.argusoft.who.emcare.web.language.dao.LanguageRepository;
import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.mapper.LanguageMapper;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;
import com.argusoft.who.emcare.web.language.service.LanguageService;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.Languages;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.Translation;
import org.apache.commons.compress.utils.Lists;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    static final Logger LOGGER = Logger.getLogger("LanguageServiceImpl");

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    IBMConfig ibmConfig;

    @Override
    public List<LanguageTranslation> getAllLanguageTranslation() {
        return languageRepository.findAll();
    }

    @Override
    public Languages getAvailableLanguageList() {
        LanguageTranslator languageTranslator = ibmConfig.getLanguageTranslatorInstance();
        try {
            System.out.println("===============================");
            Languages s = languageTranslator.listLanguages().execute().getResult();
        }catch (Exception exception){
            System.out.println("=================================");
            exception.printStackTrace();
        }
        return languageTranslator.listLanguages().execute().getResult();
    }

    @Override
    public LanguageTranslation createNewLanguageTranslation(LanguageAddDto languageAddDto) {
        JSONObject jsonObject = new JSONObject();
        Iterator<String> keys = null;
        List<String> translateLabel = new ArrayList<>();
        List<String> actualList = new ArrayList<>();
        List<Translation> result;
        String translateTo = "en-".concat(languageAddDto.getLanguageCode());
        JSONObject newJson = new JSONObject();
        LanguageTranslation languageTranslation = new LanguageTranslation();

        LanguageTranslator languageTranslator = ibmConfig.getLanguageTranslatorInstance();
        LanguageTranslation englishLanguage = languageRepository.findByLanguageCode("en");
        try {
            jsonObject = new JSONObject(englishLanguage.getLanguageData());
            keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                actualList.add(key);
                translateLabel.add(jsonObject.get(key).toString());
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }

        TranslateOptions translateOptions = new TranslateOptions.Builder().text(translateLabel).modelId(translateTo).build();

        result = languageTranslator.translate(translateOptions).execute().getResult().getTranslations();

        try {
            for (int i = 0; i < actualList.size(); i++) {
                newJson.put(actualList.get(i), result.get(i).getTranslation());
            }
            LanguageDto languageDto = LanguageMapper.getLanguageDto(languageAddDto, newJson.toString());
            languageTranslation = addOrUpdateLanguageTranslation(languageDto);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
        return languageTranslation;
    }


    @Override
    public LanguageTranslation addOrUpdateLanguageTranslation(LanguageDto language) {
        return languageRepository.saveAndFlush(LanguageMapper.getLanguageTranslation(language));
    }

    @Transactional
    public void translateNewlyAddedLabels() {
        LOGGER.info("-----------Start Translating Newly Added Labels---------------- ");
        LanguageTranslator languageTranslator = ibmConfig.getLanguageTranslatorInstance();
        List<String> englishKeys = new ArrayList<>();
        JSONObject englishJson = new JSONObject();
        LanguageTranslation englishLanguage = languageRepository.findByLanguageCode("en");
        List<LanguageTranslation> otherLanguage = languageRepository.findByLanguageCodeNot("en");
        try {
            englishJson = new JSONObject(englishLanguage.getLanguageData());
            englishKeys = Lists.newArrayList(englishJson.keys());
        } catch (Exception ex) {
            LOGGER.info("Can't parse English Json");
        }
        for (LanguageTranslation otherLanguageTranslation : otherLanguage) {
            String translateTo = "en-".concat(otherLanguageTranslation.getLanguageCode());
            LOGGER.info("Translating English to ---> " + otherLanguageTranslation.getLanguageName());
            try {
                JSONObject otherLangJson = new JSONObject(otherLanguageTranslation.getLanguageData());
                List<String> otherLangKeys = Lists.newArrayList(otherLangJson.keys());
                List<String> newKeys = new ArrayList<>();
                List<String> translatableValue = new ArrayList<>();
                for (String key : englishKeys) {
                    if (!otherLangKeys.contains(key)) {
                        newKeys.add(key);
                        translatableValue.add(englishJson.get(key).toString());
                    }
                }
                if (!newKeys.isEmpty()) {
                    TranslateOptions translateOptions = new TranslateOptions.Builder().text(translatableValue).modelId(translateTo).build();
                    List<Translation> result = languageTranslator.translate(translateOptions).execute().getResult().getTranslations();

                    for (int i = 0; i < newKeys.size(); i++) {
                        otherLangJson.put(newKeys.get(i), result.get(i).getTranslation());
                    }
                    LanguageDto languageDto = new LanguageDto();
                    languageDto.setLanguageName(otherLanguageTranslation.getLanguageName());
                    languageDto.setLanguageTranslation(otherLangJson.toString());
                    languageDto.setLanguageCode(otherLanguageTranslation.getLanguageCode());
                    languageDto.setId(otherLanguageTranslation.getId());
                    addOrUpdateLanguageTranslation(languageDto);
                }
            } catch (Exception ex) {
                LOGGER.info(ex.getMessage());
                LOGGER.info("Can't parse Json");
            }
        }
        LOGGER.info("-------- Translation Completed Server Up SuccessFully --------- ");
    }

}
