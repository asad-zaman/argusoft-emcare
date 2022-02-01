package com.argusoft.who.emcare.web.language.service;

import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;

import java.util.List;

public interface LanguageService {

    public List<LanguageTranslation> getAllLanguageTranslation();

    public LanguageTranslation addOrUpdateLanguageTranslation(LanguageDto language);
}
