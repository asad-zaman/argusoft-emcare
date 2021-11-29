package com.argusoft.who.emcare.web.fhir.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "emcare_resources")
public class EmcareResource {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	@Column(name = "text", columnDefinition="TEXT")
    private String text;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public EmcareResource(Integer id, String text) {
		this.id = id;
		this.text = text;
	}

	public EmcareResource() {
		// Default Constructor
	}
	
	
}
