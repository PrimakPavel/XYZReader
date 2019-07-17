package com.example.xyzreader2.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MyStringUtilsTest {
    private static final String STR_1 = "123456789";
    private static final String STR_2 = "123456789 123456789 123456789 123456789 123456789 123456789 string end;"; //71 symbols

    @Test
    public void zeroStringInput() {
        List<String> result = MyStringUtils.divideString(null, 10);
        assertEquals(0, result.size());
    }
    @Test
    public void emptyStringInput() {
        List<String> result = MyStringUtils.divideString("", 10);
        assertEquals(0, result.size());
    }

    @Test
    public void dividerIsZero() {
        List<String> result = MyStringUtils.divideString(STR_1, 0);
        assertEquals(1, result.size());
        assertEquals(STR_1.length(), result.get(0).length());
    }

    @Test
    public void dividerMoreThenInputStrLength(){
        List<String> result = MyStringUtils.divideString(STR_1, 20);
        assertEquals(1, result.size());
    }

    @Test
    public void longStringsTest(){
        List<String> result = MyStringUtils.divideString(STR_2, 3);
        List<String> result2 = MyStringUtils.divideString(STR_2, 70);

        assertEquals(24, result.size());
        assertEquals(2, result2.size());
    }
}
