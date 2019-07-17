package com.example.xyzreader2.utils;

import java.util.ArrayList;
import java.util.List;

public class MyStringUtils {

    public static List<String> divideString(String inputStr, int divider) {
        ArrayList<String> resultStringElements = new ArrayList<>();
        // if inputStr null or empty
        if (inputStr == null || inputStr.length() <= 0) {
            return resultStringElements;
        }
        // if elementLength zero or negative
        if (divider <= 0) {
            resultStringElements.add(inputStr);
            return resultStringElements;
        }

        if (inputStr.length() <= divider) {
            resultStringElements.add(inputStr);
        } else {
            int lastElementLength = inputStr.length() % divider;
            for (int i = 0; i < inputStr.length() - lastElementLength; ) {
                int endIndex = i + divider;
                resultStringElements.add(inputStr.substring(i, endIndex));
                i = endIndex;
            }
            resultStringElements.add(inputStr.substring(inputStr.length() - lastElementLength));
        }

        return resultStringElements;
    }
}
