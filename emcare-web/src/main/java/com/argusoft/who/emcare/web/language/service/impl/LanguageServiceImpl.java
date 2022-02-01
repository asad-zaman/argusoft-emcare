package com.argusoft.who.emcare.web.language.service.impl;

import com.argusoft.who.emcare.web.language.dao.LanguageRepository;
import com.argusoft.who.emcare.web.language.dto.LanguageDto;
import com.argusoft.who.emcare.web.language.mapper.LanguageMapper;
import com.argusoft.who.emcare.web.language.model.LanguageTranslation;
import com.argusoft.who.emcare.web.language.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    @Override
    public List<LanguageTranslation> getAllLanguageTranslation() {
        return languageRepository.findAll();
    }

    @Override
    public LanguageTranslation addOrUpdateLanguageTranslation(LanguageDto language) {
        return languageRepository.save(LanguageMapper.getLanguageTranslation(language));
    }
}
