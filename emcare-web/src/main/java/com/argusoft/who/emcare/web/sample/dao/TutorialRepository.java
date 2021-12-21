package com.argusoft.who.emcare.web.sample.dao;

import com.argusoft.who.emcare.web.sample.model.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TutorialRepository extends JpaRepository<Tutorial, Long>, TutorialRepositoryCustom {

}

