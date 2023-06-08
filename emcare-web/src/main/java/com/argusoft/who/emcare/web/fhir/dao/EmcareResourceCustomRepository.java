package com.argusoft.who.emcare.web.fhir.dao;

import java.util.List;
import java.util.Map;

public interface EmcareResourceCustomRepository {

    public List<Map<String,Object>> getPatientsList(String query);
    public List<Map<String,Object>> getPatientsList(String query,int pageNo);

    public List<Map<String,Object>> getPatientsList(String searchString,String query,int pageNo);
}
