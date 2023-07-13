package com.argusoft.who.emcare.web.user.dao;

import com.argusoft.who.emcare.web.user.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/03/23  5:06 pm
 */
@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Integer> {

    public RoleEntity findByRoleName(String roleName);
}
