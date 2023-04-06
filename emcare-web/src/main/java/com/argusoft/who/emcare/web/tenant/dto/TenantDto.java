package com.argusoft.who.emcare.web.tenant.dto;

import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/02/23  12:26 pm
 */
public class TenantDto {

    private Integer id;
    private String password;
    private String url;
    private String databaseName;
    private String databasePort;
    private String tenantId;
    private String username;
    private String domain;
    private String facility;
    private String organization;
    private UserDto user;
    private LanguageAddDto language;
    private HierarchyMasterDto hierarchy;
    private LocationMasterDto location;
    private String defaultLanguage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public LanguageAddDto getLanguage() {
        return language;
    }

    public void setLanguage(LanguageAddDto language) {
        this.language = language;
    }

    public HierarchyMasterDto getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(HierarchyMasterDto hierarchy) {
        this.hierarchy = hierarchy;
    }

    public LocationMasterDto getLocation() {
        return location;
    }

    public void setLocation(LocationMasterDto location) {
        this.location = location;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String databasePort) {
        this.databasePort = databasePort;
    }
}
