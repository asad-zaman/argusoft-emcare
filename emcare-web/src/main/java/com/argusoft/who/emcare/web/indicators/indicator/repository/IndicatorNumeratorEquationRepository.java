package com.argusoft.who.emcare.web.indicators.indicator.repository;

import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorNumeratorEquation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
public interface IndicatorNumeratorEquationRepository extends JpaRepository<IndicatorNumeratorEquation, Long> {

    @Modifying
    @Query(value = "delete from indicator_numerator_equation where indicator_id = :indicatorId", nativeQuery = true)
    public void deleteByNumeratorIndicator(Long indicatorId);
}
