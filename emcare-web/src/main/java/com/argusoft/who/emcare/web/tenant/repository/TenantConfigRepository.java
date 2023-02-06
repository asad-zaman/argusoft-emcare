package com.argusoft.who.emcare.web.tenant.repository;

import com.argusoft.who.emcare.web.tenant.entity.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 06/02/23  12:13 pm
 */
@Repository
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Integer> {
}
