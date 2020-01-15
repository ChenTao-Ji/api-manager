package com.api.admin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author chentao.ji
 */
public class ThrowableUtil {

    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String errorMsg = stringWriter.toString();
        return errorMsg;
    }
}
