package com.argusoft.who.emcare.web.tenant.dto;

import com.argusoft.who.emcare.web.language.dto.LanguageAddDto;
import com.argusoft.who.emcare.web.location.dto.HierarchyMasterDto;
import com.argusoft.who.emcare.web.location.dto.LocationMasterDto;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Organization;

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
    private String tenantId;
    private String username;
    private String domain;
    private Location facility;
    private Organization organization;
    private UserDto userDto;
    private LanguageAddDto language;
    private HierarchyMasterDto hierarchy;
    private LocationMasterDto location;

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

    public Location getFacility() {
        return facility;
    }

    public void setFacility(Location facility) {
        this.facility = facility;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
