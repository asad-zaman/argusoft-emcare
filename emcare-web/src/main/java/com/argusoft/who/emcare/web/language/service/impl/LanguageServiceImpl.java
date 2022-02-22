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
        String translateTo = "en-".concat(languageAddDto.getLanguageCode()) ;
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

        TranslateOptions translateOptions = new TranslateOptions.Builder()
                .text(translateLabel)
                .modelId(translateTo)
                .build();

        result = languageTranslator.translate(translateOptions)
                .execute().getResult().getTranslations();

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
}
