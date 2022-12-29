package com.argusoft.who.emcare.web.indicators.indicator.repository;

import com.argusoft.who.emcare.web.indicators.codes.entity.EmCareCustomCodeSystem;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
