package com.tigerit.soa.loginsecurity.entity.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@AttributeOverride(name = "version", column = @Column(name = "INTERNAL_VERSION"))
public abstract class ActivityCommon extends EntityCommon implements Serializable {
    @Column(name = "ACTIVITY_USER")
    protected String activityUser;

    @Column(name = "ACTIVITY_ACTION")
    protected int activityAction;

    @Column(name = "ACTIVITY_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date activityTime;

    @Column(name = "INTERNAL_VERSION")
    protected Long version;

    public abstract Long getId();

    public String getActivityUser() {
        return activityUser;
    }

    public void setActivityUser(String activityUser) {
        this.activityUser = activityUser;
    }

    public int getActivityAction() {
        return activityAction;
    }

    public void setActivityAction(int activityAction) {
        this.activityAction = activityAction;
    }

    public Date getActivityTime() {
        return this.activityTime;
    }

    public void setActivityTime(Date activityTime) {
        this.activityTime = activityTime;
    }
}
