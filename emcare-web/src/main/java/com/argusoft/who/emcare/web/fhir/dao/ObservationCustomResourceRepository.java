package com.argusoft.who.emcare.web.fhir.dao;

import java.util.List;
import java.util.Map;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 02/01/23  11:57 am
 */
public interface ObservationCustomResourceRepository {

    public List<Map<String, Object>> findByPublished();
}
