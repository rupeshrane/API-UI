package org.sodales;

import model.TestRunContext;

public class ApiTestRunnerPaths {

    public static String getHtmlReportPath(TestRunContext context) {
        return context.htmlPath;
    }

    public static String getCsvReportPath(TestRunContext context) {
        return context.csvPath;
    }

    public static String getExtentHtmlReportPath(TestRunContext context) {
        return context.extentPath;
    }

    public static String getLogPath(TestRunContext context) {
        return context.logPath;
    }
}