package com.argusoft.who.emcare.web.user.mapper;

import com.argusoft.who.emcare.web.location.model.LocationMaster;
import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.dto.UserMasterDto;
import com.argusoft.who.emcare.web.userLocationMapping.model.UserLocationMapping;
import org.keycloak.representations.AccessToken;

public class UserMapper {

    private UserMapper() {
    }

    public static UserLocationMapping userDtoToUserLocationMappingEntity(UserDto userDto, String userId) {
        UserLocationMapping user = new UserLocationMapping();
        user.setUserId(userId);
        user.setLocationId(userDto.getLocationId());
        if (userDto.getRegRequestFrom().equalsIgnoreCase(UserConst.MOBILE)) {
            user.setRegRequestFrom(UserConst.MOBILE);
            user.setState(false);
        } else {
            user.setRegRequestFrom(UserConst.WEB);
            user.setState(true);
        }
        return user;
    }

    public static UserMasterDto getMasterUser(AccessToken token, LocationMaster locationMaster) {
        UserMasterDto userMaster = new UserMasterDto();
        userMaster.setUserName(token.getName());
        userMaster.setLocation(locationMaster);
        userMaster.setRoles(token.getRealmAccess().getRoles().toArray(new String[0]));
        userMaster.setUserId(token.getSubject());
        userMaster.setEmail(token.getEmail());
        return userMaster;
    }
}
