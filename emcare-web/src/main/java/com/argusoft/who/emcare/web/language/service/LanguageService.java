package com.argusoft.who.emcare.web.language.service;

import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;
import com.ibm.watson.language_translator.v3.model.Languages;

import java.util.List;

public interface LanguageService {

    public List<LanguageTranslation> getAllLanguageTranslation();

    public Languages getAvailableLanguageList();

    public LanguageTranslation createNewLanguageTranslation(LanguageAddDto languageAddDto);

    public LanguageTranslation addOrUpdateLanguageTranslation(LanguageDto language);
}
