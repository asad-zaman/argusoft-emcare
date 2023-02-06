package com.argusoft.who.emcare.web.fhir.dao.impl;

import com.argusoft.who.emcare.web.common.dao.GenericRepositoryImpl;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 02/01/23  11:58 am
 */
@Repository
public class ObservationCustomResourceRepositoryImpl extends GenericRepositoryImpl implements ObservationCustomResourceRepository {

    @Override
    public List<Map<String, Object>> findByPublished(String queryString) {
        Query query = getSession().createNativeQuery(queryString);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
