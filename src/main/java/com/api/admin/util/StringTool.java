package com.api.admin.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chentao.ji
 */
public class StringTool {

    public static final String EMPTY = "";

    public static boolean isBlank(final String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }


    public static String[] split(final String str, final String separatorChars) {
        if (isBlank(str)) {
            return null;
        }
        if (isBlank(separatorChars)) {
            return new String[]{str.trim()};
        }
        List<String> list = new ArrayList<>();
        for (String item : str.split(separatorChars)) {
            if (isNotBlank(item)) {
                list.add(item.trim());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static String join(final String[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
}
