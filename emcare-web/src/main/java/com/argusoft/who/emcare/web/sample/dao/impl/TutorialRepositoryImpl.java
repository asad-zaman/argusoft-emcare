package com.argusoft.who.emcare.web.sample.dao.impl;

import com.argusoft.who.emcare.web.common.dao.GenericRepositoryImpl;
import com.argusoft.who.emcare.web.sample.dao.TutorialRepositoryCustom;
import com.argusoft.who.emcare.web.sample.model.Tutorial;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;

import java.util.List;

public class TutorialRepositoryImpl extends GenericRepositoryImpl implements TutorialRepositoryCustom {

    @Override
    public List<Tutorial> findByPublished(boolean published) {
        String queryString = "select * from tutorials where published = true;";
        Query query = getSession().createSQLQuery(queryString);
        return query.setResultTransformer(Transformers.aliasToBean(Tutorial.class)).list();
    }

    @Override
    public List<Tutorial> findByTitleContaining(String title) {
        String queryString = "select * from tutorials where title ilike %" + title + "%;";
        Query query = getSession().createSQLQuery(queryString);
        return query.list();
    }
}
