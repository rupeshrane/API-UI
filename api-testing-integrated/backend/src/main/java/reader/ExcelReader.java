package reader;

import model.ApiTestCase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {
    // This method is used to read the excel file and store them in the local variables as per the model mention C:\Users\SohamPatel\Documents\Code\apiautomationjava\src\main\java\model\ApiTestCase.java.
    public static List<ApiTestCase> readTestCases(String path) {
        List<ApiTestCase> cases = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(path))) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            rows.next(); // skip header
            while (rows.hasNext()) {
                Row row = rows.next();
                ApiTestCase tc = new ApiTestCase();
                tc.testName = row.getCell(0).getStringCellValue();
                tc.method = row.getCell(1).getStringCellValue();
                tc.url = row.getCell(2).getStringCellValue();
                tc.headers = row.getCell(3).getStringCellValue();
                tc.payload = row.getCell(4).getStringCellValue();
                tc.expectedStatus = row.getCell(5).toString().replaceAll("\\.0$", "");
                tc.expectedResponse = row.getCell(6).getStringCellValue();
                tc.skip=row.getCell(7).getStringCellValue();
                tc.globalvariablejsonxmlpath=row.getCell(8).getStringCellValue();
                tc.globalvariable=row.getCell(9).getStringCellValue();
                tc.xmlorjson=row.getCell(10).getStringCellValue();
                cases.add(tc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cases;
    }
}