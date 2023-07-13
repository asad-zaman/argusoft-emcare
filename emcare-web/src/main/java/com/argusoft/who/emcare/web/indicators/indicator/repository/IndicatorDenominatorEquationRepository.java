package com.argusoft.who.emcare.web.indicators.indicator.repository;

import com.argusoft.who.emcare.web.indicators.indicator.entity.IndicatorDenominatorEquation;
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
 * @since - 29/12/22  11:47 am
 */
@Repository
public interface IndicatorDenominatorEquationRepository extends JpaRepository<IndicatorDenominatorEquation, Long> {


    @Modifying
    @Query(value = "delete from indicator_denominator_equation where indicator_id = :indicatorId", nativeQuery = true)
    public void deleteByDenominatorIndicator(Long indicatorId);

}
