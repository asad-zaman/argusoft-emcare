package com.argusoft.who.emcare.web.fhir.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bundle_sync_resource")
public class BundleSyncResource implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "bundle_text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "synced_on")
    private Date sync_on;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSync_on() {
        return sync_on;
    }

    public void setSync_on(Date sync_on) {
        this.sync_on = sync_on;
    }
}
