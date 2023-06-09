package com.argusoft.who.emcare.web.fhir.dao.impl;

import com.argusoft.who.emcare.web.common.dao.GenericRepositoryImpl;
import com.argusoft.who.emcare.web.fhir.dao.EmcareResourceCustomRepository;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class EmcareResourceCustomRepositoryImpl extends GenericRepositoryImpl implements EmcareResourceCustomRepository {
    @Override
    public List<Map<String, Object>> getPatientsList(String queryString) {
        Query fquery = getSession().createNativeQuery(queryString);
        fquery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return fquery.list();
    }

    @Override
    public List<Map<String, Object>> getPatientsList(String queryString, int pageNo) {
        Query tquery = getSession().createNativeQuery(queryString);
        tquery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return tquery.list();
    }

    @Override
    public List<Map<String, Object>> getPatientsList(String searchString,String queryString,int pageNo) {
        Query query = getSession().createNativeQuery(queryString);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
