package com.argusoft.who.emcare.web.fhir.dto;

/**
 *
 * @author parth
 */
public class QuestionnaireDto {

    private String id;
    private String name;
    private String title;
    private String description;

    public QuestionnaireDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
