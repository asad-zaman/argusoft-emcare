package com.argusoft.who.emcare.web.user.mapper;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserListDto;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;

public class UserMapper {

    private UserMapper() {
    }


    public static UserLocationMapping userDtoToUserLocationMappingEntityForSignup(UserDto userDto, String userId) {
        UserLocationMapping user = new UserLocationMapping();
        user.setUserId(userId);
        user.setLocationId(userDto.getLocationId());
        user.setIsFirst(true);
        if (userDto.getRegRequestFrom().equalsIgnoreCase(UserConst.MOBILE)) {
            user.setRegRequestFrom(UserConst.MOBILE);
            user.setState(false);
        } else {
            user.setRegRequestFrom(UserConst.WEB);
            user.setState(false);
        }
        return user;
    }

    public static UserLocationMapping userDtoToUserLocationMappingEntity(UserDto userDto, String userId) {
        UserLocationMapping user = new UserLocationMapping();
        user.setUserId(userId);
        user.setLocationId(userDto.getLocationId());
        user.setIsFirst(true);
        if (userDto.getRegRequestFrom().equalsIgnoreCase(UserConst.MOBILE)) {
            user.setRegRequestFrom(UserConst.MOBILE);
            user.setState(false);
        } else {
            user.setRegRequestFrom(UserConst.WEB);
            user.setState(true);
        }
        return user;
    }

    public static UserMasterDto getMasterUser(AccessToken token, LocationMaster locationMaster, UserRepresentation userInfo) {
        UserMasterDto userMaster = new UserMasterDto();
        userMaster.setUserName(token.getName());
        userMaster.setFirstName(userInfo.getFirstName());
        userMaster.setLastName(userInfo.getLastName());
        userMaster.setLocation(locationMaster);
        userMaster.setRoles(token.getRealmAccess().getRoles().toArray(new String[0]));
        userMaster.setUserId(token.getSubject());
        userMaster.setEmail(token.getEmail());
        if (userInfo.getAttributes() == null || userInfo.getAttributes().isEmpty()) {
            userMaster.setLanguage(CommonConstant.ENGLISH);
        } else {
            userMaster.setLanguage(userInfo.getAttributes().get(CommonConstant.LANGUAGE_KEY).get(0));
        }
        return userMaster;
    }

    public static UserListDto getUserListDto(UserRepresentation userRepresentation, LocationMaster locationMaster) {
        UserListDto user = new UserListDto();
        user.setId(userRepresentation.getId());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setUserName(userRepresentation.getUsername());
        user.setEmail(userRepresentation.getEmail());
        user.setEnabled(userRepresentation.isEnabled());
        user.setRealmRoles(userRepresentation.getRealmRoles());
        user.setLocationName(locationMaster != null ? locationMaster.getName() : null);
        user.setLocationId(locationMaster != null ? locationMaster.getId() : null);
        user.setLocationType(locationMaster != null ? locationMaster.getType() : null);
        return user;
    }
}
