package com.argusoft.who.emcare.web.fhir.dao.impl;

import com.argusoft.who.emcare.web.common.dao.GenericRepositoryImpl;
import com.argusoft.who.emcare.web.fhir.dao.ObservationCustomResourceRepository;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
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
    public List<Map<String, Object>> findByPublished() {
        String queryString = "with custom_code as (select cast(text AS json)->'code'->'coding'->0->>'code' as code,\n" +
                " cast(cast(cast(text AS json)->>'valueBoolean' as text) AS BOOLEAN) as valueText\n" +
                " from observation_resource)\n" +
                "select * from custom_code where code = 'EmCare.B12S2.DE01' and valueText = false;;";
        Query query = getSession().createNativeQuery(queryString);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> maps = query.list();
        return maps;
    }
}
