package com.argusoft.who.emcare.web.common.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PageDtoTest {

    @Test
    void testSetAndGetList() {
        List<String> testData = Arrays.asList("Item 1", "Item 2", "Item 3");
        PageDto pageDto = new PageDto();
        pageDto.setList(testData);

        assertEquals(testData,  pageDto.getList());
    }


    @Test
    void testSetAndGetTotalCount() {
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
        pageDto.setList(Collections.emptyList());
        pageDto.setTotalCount(0L);

        assertEquals(emptyList, pageDto.getList());
        assertEquals(0L, pageDto.getTotalCount());
    }
}
