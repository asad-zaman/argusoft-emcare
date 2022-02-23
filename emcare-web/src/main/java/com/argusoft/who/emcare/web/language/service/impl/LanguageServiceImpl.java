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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    Environment environment;

    @Override
    public List<LanguageTranslation> getAllLanguageTranslation() {
        return languageRepository.findAll();
    }

    @Override
    public Languages getAvailableLanguageList() {
        LanguageTranslator languageTranslator = IBMConfig.getLanguageTranslatorInstance();
        Languages languages = languageTranslator.listLanguages().execute().getResult();
        return languages;
    }

    @Override
    public LanguageTranslation createNewLanguageTranslation(LanguageAddDto languageAddDto) {
        JSONObject jsonObject = new JSONObject();
        Iterator<String> keys = null;
        List<String> translateLabel = new ArrayList<>();
        List<String> actualList = new ArrayList<>();
        List<Translation> result = new ArrayList<>();
        String translateTo = "en-".concat(languageAddDto.getLanguageCode());
        JSONObject newJson = new JSONObject();
        LanguageTranslation languageTranslation = new LanguageTranslation();

        LanguageTranslator languageTranslator = IBMConfig.getLanguageTranslatorInstance();
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

        }
        return languageTranslation;
    }


    @Override
    public LanguageTranslation addOrUpdateLanguageTranslation(LanguageDto language) {
        return languageRepository.save(LanguageMapper.getLanguageTranslation(language));
    }

    @Transactional
    public void translateNewlyAddedLabels() {
        System.out.println("-----------Start Translating Newly Added Labels---------------- ");
        LanguageTranslator languageTranslator = IBMConfig.getLanguageTranslatorInstance();
        List<String> englishKeys = new ArrayList<>();
        JSONObject englishJson = new JSONObject();
        LanguageTranslation englishLanguage = languageRepository.findByLanguageCode("en");
        List<LanguageTranslation> otherLanguage = languageRepository.findByLanguageCodeNot("en");
        try {
            englishJson = new JSONObject(englishLanguage.getLanguageData());
            englishKeys = Lists.newArrayList(englishJson.keys());
        } catch (Exception ex) {
            System.out.println("Can't parse English Json");
        }
        for (LanguageTranslation otherLanguageTranslation : otherLanguage) {
            String translateTo = "en-".concat(otherLanguageTranslation.getLanguageCode());
            System.out.println("Translating English to ---> " + otherLanguageTranslation.getLanguageName());
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
                if (newKeys.size() > 0) {
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
                System.out.println(ex.getMessage());
                System.out.println("Can't parse Json");
            }
        }
        System.out.println("-------- Translation Completed Server Up SuccessFully --------- ");
    }

}
