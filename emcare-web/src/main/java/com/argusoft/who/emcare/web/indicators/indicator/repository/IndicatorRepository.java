package com.argusoft.who.emcare.web.indicators.indicator.repository;

import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
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
 * @since - 28/12/22  12:04 pm
 */
@Repository
public interface IndicatorRepository extends JpaRepository<Indicator, Long> {

    Page<Indicator> findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String code,
            String name,
            String description,
            Pageable pageable);

    List<Indicator> findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String code,
            String name,
            String description);
}
