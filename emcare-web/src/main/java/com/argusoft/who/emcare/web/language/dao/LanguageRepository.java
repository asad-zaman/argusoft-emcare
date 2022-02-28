package com.argusoft.who.emcare.web.language.dao;

import com.argusoft.who.emcare.web.language.model.LanguageTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<LanguageTranslation, Integer> {

    LanguageTranslation findByLanguageCode(String languageCode);

    List<LanguageTranslation> findByLanguageCodeNot(String languageCode);
}
