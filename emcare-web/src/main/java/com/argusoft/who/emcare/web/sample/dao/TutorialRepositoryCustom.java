package com.argusoft.who.emcare.web.sample.dao;

import com.argusoft.who.emcare.web.sample.model.Tutorial;

import java.util.List;

public interface TutorialRepositoryCustom {

    List<Tutorial> findByPublished(boolean published);

    List<Tutorial> findByTitleContaining(String title);
}