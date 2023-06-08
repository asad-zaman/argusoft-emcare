package com.argusoft.who.emcare.web.questionnaireresponse.model;

import com.argusoft.who.emcare.web.common.model.EntityAuditInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_sync_log")
public class UserSyncLog extends EntityAuditInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "sync_attempt_time", nullable = false)
    private Date syncAttemptTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSyncAttemptTime() {
        return syncAttemptTime;
    }

    public void setSyncAttemptTime(Date syncAttemptTime) {
        this.syncAttemptTime = syncAttemptTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
