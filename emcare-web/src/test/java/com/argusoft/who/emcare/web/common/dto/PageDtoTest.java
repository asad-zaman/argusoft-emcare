package com.argusoft.who.emcare.web.common.dto;

import com.argusoft.who.emcare.web.common.service.impl.CommonServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ContextConfiguration(classes = {CommonServiceImpl.class})
public class PageDtoTest {

    @Test
    void testGetList() {

        List<String> testData = Arrays.asList("Item 1", "Item 2", "Item 3");
        PageDto pageDto = new PageDto();
        pageDto.setList(testData);

        List<?> resultList = pageDto.getList();

        assertEquals(testData, resultList);
    }

    @Test
    void testSetList() {
        List<String> testData = Arrays.asList("Item 1", "Item 2", "Item 3");
        PageDto pageDto = new PageDto();

        pageDto.setList(testData);

        assertEquals(testData, pageDto.getList());
    }

    @Test
    void testGetTotalCount() {
        Long totalCount = 10L;
        PageDto pageDto = new PageDto();
        pageDto.setTotalCount(totalCount);

        Long resultTotalCount = pageDto.getTotalCount();

        assertEquals(totalCount, resultTotalCount);
    }

    @Test
    void testSetTotalCount() {
        Long totalCount = 10L;
        PageDto pageDto = new PageDto();

        pageDto.setTotalCount(totalCount);

        assertEquals(totalCount, pageDto.getTotalCount());
    }

    @Test
    void testEmptyPageDto() {
        PageDto pageDto = new PageDto();

        assertNull(pageDto.getList());
        assertNull(pageDto.getTotalCount());
    }

    @Test
    void testPageDtoWithNullList() {
        PageDto pageDto = new PageDto();
        pageDto.setTotalCount(5L);

        assertNull(pageDto.getList());
        assertEquals(5L, pageDto.getTotalCount());
    }

    @Test
    void testPageDtoWithEmptyList() {
        PageDto pageDto = new PageDto();
        List<?> emptyList = Collections.emptyList();
        pageDto.setList(emptyList);
        pageDto.setTotalCount(0L);

        assertEquals(emptyList, pageDto.getList());
        assertEquals(0L, pageDto.getTotalCount());
    }

}
