package com.argusoft.who.emcare.web.sample.service.impl;

import com.argusoft.who.emcare.web.sample.dao.TutorialRepository;
import com.argusoft.who.emcare.web.sample.model.Tutorial;
import com.argusoft.who.emcare.web.sample.service.TutorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TutorialServiceImpl implements TutorialService {

    @Autowired
    private TutorialRepository tutorialRepository;

    @Override
    public List<Tutorial> getTutorials() {
        return tutorialRepository.findByPublished(true);
    }
}
