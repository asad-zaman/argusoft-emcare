package com.argusoft.who.emcare.web.indicators.indicator.repository;

import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorEquation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h1> Add heading here </h1>
 * <p>
 * Add Description here.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  2:15 pm
 */
@Repository
public interface IndicatorEquationRepository extends JpaRepository<IndicatorEquation, Long> {
}
