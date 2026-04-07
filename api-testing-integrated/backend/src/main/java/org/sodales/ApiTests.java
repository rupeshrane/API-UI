package org.sodales;

import model.ApiTestCase;
import model.AuthContext;
import model.TestRunContext;
import reader.ExcelReader;
import runner.ApiTestRunner;

import java.time.LocalDateTime;
import java.util.List;

public class ApiTests {

    public static void run(TestRunContext context, AuthContext authContext) {
        LocalDateTime testcaseexecutionstarttime = LocalDateTime.now();

        GlobalVariableHandler variableHandler = new GlobalVariableHandler();
        List<ApiTestCase> tests = ExcelReader.readTestCases(context.excelPath);

        System.out.println("Execution of the Test cases has been started..");
        System.out.println("Execution Start time: " + testcaseexecutionstarttime);
        System.out.println("Using Excel file: " + context.excelPath);
        System.out.println("Run ID: " + context.runId);

        for (ApiTestCase test : tests) {
            if (test.skip != null && test.skip.equalsIgnoreCase("yes")) {
                System.out.println("Skipping the test " + test.testName);
            } else {
                System.out.println("Currently Executing: " + test.testName);
                ApiTestRunner.run(test, context, variableHandler, authContext);
            }
        }

        System.out.println("Execution of the Test cases has been completed..");
        LocalDateTime testcaseexecutionstoptime = LocalDateTime.now();
        System.out.println("Execution Stop time: " + testcaseexecutionstoptime);
    }
}