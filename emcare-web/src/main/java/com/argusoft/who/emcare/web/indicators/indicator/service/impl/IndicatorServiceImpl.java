package com.argusoft.who.emcare.web.indicators.indicator.service.impl;

import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.common.dto.PageDto;
import com.argusoft.who.emcare.web.indicators.indicator.dto.IndicatorDto;
import com.argusoft.who.emcare.web.indicators.indicator.entity.Indicator;
import com.argusoft.who.emcare.web.indicators.indicator.mapper.IndicatorMapper;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorDenominatorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorNumeratorEquationRepository;
import com.argusoft.who.emcare.web.indicators.indicator.repository.IndicatorRepository;
import com.argusoft.who.emcare.web.indicators.indicator.service.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <h1> Indicator Service like Add, Update, and Get.</h1>
 * <p>
 * Apis.
 * </p>
 *
 * @author - jaykalariya
 * @since - 28/12/22  10:43 am
 */
@Service
public class IndicatorServiceImpl implements IndicatorService {

    @Autowired
    IndicatorRepository indicatorRepository;

    @Autowired
    IndicatorNumeratorEquationRepository indicatorNumeratorEquationRepository;

    @Autowired
    IndicatorDenominatorEquationRepository indicatorDenominatorEquationRepository;

    /**
     * @param indicatorDto
     * @return
     */
    @Override
    public ResponseEntity<Object> addOrUpdateIndicator(IndicatorDto indicatorDto) {
        Indicator indicator = indicatorRepository.save(IndicatorMapper.getIndicator(indicatorDto));
        indicatorNumeratorEquationRepository.saveAll(indicator.getNumeratorEquation());
        indicatorDenominatorEquationRepository.saveAll(indicator.getDenominatorEquation());
        return ResponseEntity.status(HttpStatus.OK).body(indicator);
    }

    /**
     * @return All Indicator
     */
    @Override
    public ResponseEntity<Object> getAllIndicatorData() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findAll());
    }

    /**
     * @param pageNo     Integer
     * @param searchText String
     * @return PageDto Model
     */
    @Override
    public PageDto getIndicatorDataPage(Integer pageNo, String searchText) {
        Sort sort = Sort.by("createdOn").descending();
        Pageable page = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, sort);
        Long totalCount;
        Page<Indicator> indicators;
        if (searchText != null && !searchText.isEmpty()) {
            totalCount = (long) indicatorRepository
                    .findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            searchText,
                            searchText,
                            searchText
                    ).size();
            indicators = indicatorRepository
                    .findByIndicatorCodeContainingIgnoreCaseOrIndicatorNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            searchText,
                            searchText,
                            searchText,
                            page);
        } else {
            totalCount = indicatorRepository.count();
            indicators = indicatorRepository.findAll(page);
        }
        PageDto pageDto = new PageDto();
        pageDto.setList(indicators.getContent());
        pageDto.setTotalCount(totalCount);
        return pageDto;
    }
}
