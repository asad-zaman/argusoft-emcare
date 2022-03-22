package com.argusoft.who.emcare.web.menu.dto;

public class FeatureJSON {

    private Boolean canAdd;
    private Boolean canEdit;
    private Boolean canView;
    private Boolean canDelete;

    public FeatureJSON() {
    }

    public FeatureJSON(Boolean canAdd, Boolean canEdit, Boolean canView, Boolean canDelete) {
        this.canAdd = canAdd;
        this.canEdit = canEdit;
        this.canView = canView;
        this.canDelete = canDelete;
    }

    public Boolean getCanAdd() {
        return canAdd;
    }

    public void setCanAdd(Boolean canAdd) {
        this.canAdd = canAdd;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    @Override
    public String toString() {
        return "{\"canEdit\":" + canEdit + ",\"canDelete\":" + canDelete + ",\"canAdd\":" + canAdd + ",\"canView\":" + canView + "}";
    }
}
