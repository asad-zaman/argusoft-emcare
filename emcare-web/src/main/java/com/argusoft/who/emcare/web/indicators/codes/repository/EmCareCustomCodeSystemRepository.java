package com.argusoft.who.emcare.web.indicators.codes.repository;

import com.argusoft.who.emcare.web.device.model.DeviceMaster;
import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 27/12/22  11:38 am
 */
@Repository
public interface EmCareCustomCodeSystemRepository extends JpaRepository<EmCareCustomCodeSystem, Long> {

    public EmCareCustomCodeSystem findByCode(String code);

    public EmCareCustomCodeSystem findByCodeAndCodeIdNot(String code, Long codeId);

    public Page<EmCareCustomCodeSystem> findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(
            String code,
            String description,
            Pageable pageable);

    public List<EmCareCustomCodeSystem> findByCodeContainingIgnoreCaseOrCodeDescriptionContainingIgnoreCase(
            String code,
            String description);
}
