package com.argusoft.who.emcare.web.user.mapper;

import com.argusoft.who.emcare.web.user.cons.UserConst;
import com.argusoft.who.emcare.web.user.dto.UserDto;
import com.argusoft.who.emcare.web.user.model.User;

public class UserMapper {

    public static User userDtoToUserEntity(UserDto userDto, String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setLocationId(userDto.getLocationId());
        if (userDto.getRegRequestFrom().equalsIgnoreCase(UserConst.MOBILE)) {
            user.setRegRequestFrom(UserConst.MOBILE);
            user.setRegStatus(UserConst.REGISTRATION_PENDING);
        } else {
            user.setRegRequestFrom(UserConst.WEB);
            user.setRegStatus(UserConst.REGISTRATION_COMPLETED);
        }
        return user;
    }
}
