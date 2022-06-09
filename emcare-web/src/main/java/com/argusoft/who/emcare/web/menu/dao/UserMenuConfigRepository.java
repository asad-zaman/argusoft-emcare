package com.argusoft.who.emcare.web.menu.dao;

import com.argusoft.who.emcare.web.menu.dto.UserFeatureJson;
import com.argusoft.who.emcare.web.menu.model.UserMenuConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMenuConfigRepository extends JpaRepository<UserMenuConfig, Integer> {

    @Query(value = "select mc.id as \"id\",mc.menu_name AS \"menuName\", mc.parent as \"parent\", \n" +
            "CASE WHEN umc.feature_json is NULL\n" +
            "THEN mc.feature_json\n" +
            "ELSE umc.feature_json\n" +
            "END AS \"featureJson\" \n" +
            "from user_menu_config as umc \n" +
            "left join menu_config as mc on umc.menu_id = mc.id \n" +
            "where umc.role_id in :roles\n" +
            "or umc.user_id = :userId \n" +
            "group by mc.id,umc.feature_json,mc.feature_json", nativeQuery = true)
    public List<UserFeatureJson> getMenuByUser(@Param("roles") List<String> roles, @Param("userId") String userId);

    @Query(value = "select umc.id,umc.menu_id ,umc.role_id ,umc.user_id ,\n" +
            "umc.created_by ,umc.modified_by, umc.created_on ,\n" +
            "umc.modified_on,\n" +
            "CASE WHEN umc.feature_json is NULL\n" +
            "THEN mc.feature_json\n" +
            "ELSE umc.feature_json\n" +
            "END AS \"feature_json\" \n" +
            "from user_menu_config as umc \n" +
            "left join menu_config as mc on umc.menu_id = mc.id \n" +
            "where mc.id = :menuId", nativeQuery = true)
    public List<UserMenuConfig> getMenuConfigByMenuId(@Param("menuId") Integer menuId);
}
