package org.sodales;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@CrossOrigin("*")
public class ApiController {

    private static final String TEMP_DIR =
            System.getProperty("java.io.tmpdir") + File.separator + "api-testing";

    private static final String EXCEL_TARGET =
            TEMP_DIR + File.separator + "api_test_data.xlsx";

    private static final String HTML_REPORT =
            TEMP_DIR + File.separator + "api_debug_report.html";

    private static final String EXTENT_HTML_REPORT =
            TEMP_DIR + File.separator + "api_extent_debug_report.html";

    private static final String CSV_REPORT =
            TEMP_DIR + File.separator + "api_debug_report.csv";

    @PostMapping("/run-tests")
    public ResponseEntity<byte[]> runTests(
            @RequestParam("file") MultipartFile file,
            @RequestParam String authType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String clientId,
            @RequestParam(required = false) String clientSecret,
            @RequestParam(required = false) String tokenUrl,
            @RequestParam(required = false) String grantType
    ) {
        try {
            LogCollector.clear();
            LogCollector.log("=== /run-tests called ===");
            LogCollector.log("Authentication Type: " + authType);
            LogCollector.log("Uploaded File: " + (file != null ? file.getOriginalFilename() : "null"));

            File tempFolder = new File(TEMP_DIR);
            if (!tempFolder.exists()) {
                tempFolder.mkdirs();
                LogCollector.log("Temp directory created: " + TEMP_DIR);
            } else {
                LogCollector.log("Using temp directory: " + TEMP_DIR);
            }

            if (file == null || file.isEmpty()) {
                LogCollector.log("Excel file is missing.");
                return ResponseEntity.badRequest()
                        .body("Excel file is required".getBytes());
            }

            String lowerName = file.getOriginalFilename() == null
                    ? ""
                    : file.getOriginalFilename().toLowerCase();

            if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls")) {
                LogCollector.log("Invalid file type uploaded.");
                return ResponseEntity.badRequest()
                        .body("Only Excel files are allowed".getBytes());
            }

            LogCollector.log("Validating authentication inputs...");
            validateAuthInputs(authType, username, password, clientId, clientSecret, tokenUrl);

            LogCollector.log("Saving Excel to: " + EXCEL_TARGET);
            file.transferTo(new File(EXCEL_TARGET));

            LogCollector.log("Clearing old reports...");
            clearOldReports();

            LogCollector.log("Updating framework.properties...");
            updateProperties(authType, username, password, clientId, clientSecret, tokenUrl, grantType);

            LogCollector.log("Setting dynamic runtime paths...");
            ApiTests.setExcelPath(EXCEL_TARGET);
            ApiTestRunnerPaths.setHtmlReportPath(HTML_REPORT);
            ApiTestRunnerPaths.setCsvReportPath(CSV_REPORT);
            ApiTestRunnerPaths.setExtentHtmlReportPath(EXTENT_HTML_REPORT);

            LogCollector.log("Starting API test execution...");
            ApiTests.main(null);

            File htmlReportFile = new File(HTML_REPORT);
            File csvReportFile = new File(CSV_REPORT);
            File extentHtmlReportFile = new File(EXTENT_HTML_REPORT);

            if (!htmlReportFile.exists() && !csvReportFile.exists() && !extentHtmlReportFile.exists()) {
                LogCollector.log("Report files are not generated.");
                return ResponseEntity.internalServerError()
                        .body("Report files are not generated".getBytes());
            }

            LogCollector.log("Preparing ZIP download...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);

            addFileToZip(zipOut, htmlReportFile, "api_debug_report.html");
            addFileToZip(zipOut, csvReportFile, "api_debug_report.csv");
            addFileToZip(zipOut, extentHtmlReportFile, "api_extent_debug_report.html");

            zipOut.close();

            LogCollector.log("Execution completed. Report downloaded.");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=api-test-results.zip")
                    .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                    .body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            LogCollector.log("Execution failed: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(("Error: " + e.getClass().getName() + " - " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        return ResponseEntity.ok(LogCollector.getLogs());
    }

    private void validateAuthInputs(String authType,
                                    String username,
                                    String password,
                                    String clientId,
                                    String clientSecret,
                                    String tokenUrl) {

        if ("BASIC".equalsIgnoreCase(authType)) {
            if (isBlank(username) || isBlank(password)) {
                throw new RuntimeException("Username and Password are required for BASIC authentication");
            }
        } else if ("OAUTH2".equalsIgnoreCase(authType) || "OAUTH".equalsIgnoreCase(authType)) {
            if (isBlank(clientId) || isBlank(clientSecret) || isBlank(tokenUrl)) {
                throw new RuntimeException("Token URL, Client ID and Client Secret are required for OAUTH2");
            }
        } else {
            throw new RuntimeException("Unsupported Authentication Type: " + authType);
        }
    }

    private void updateProperties(String authType,
                                  String username,
                                  String password,
                                  String clientId,
                                  String clientSecret,
                                  String tokenUrl,
                                  String grantType) throws Exception {

        Properties props = new Properties();
        File propertiesFile = new File("src/main/resources/framework.properties");

        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            props.load(fis);
        }

        if ("BASIC".equalsIgnoreCase(authType)) {
            String encoded = Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes());

            props.setProperty("Authentication_Type", "BASIC");
            props.setProperty("Authorization", "Basic " + encoded);
            props.setProperty("TOKEN_URL", tokenUrl == null ? "" : tokenUrl);
            props.setProperty("CLIENT_ID", username);
            props.setProperty("CLIENT_SECRET", password);
            props.setProperty("GRANT_TYPE", grantType == null ? "" : grantType);

        } else {
            String encoded = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());

            props.setProperty("Authentication_Type", "OAUTH");
            props.setProperty("Authorization", "Basic " + encoded);
            props.setProperty("TOKEN_URL", tokenUrl);
            props.setProperty("CLIENT_ID", clientId);
            props.setProperty("CLIENT_SECRET", clientSecret);
            props.setProperty("GRANT_TYPE", isBlank(grantType) ? "client_credentials" : grantType);
        }

        try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
            props.store(fos, "Updated dynamically from UI");
        }
    }

    private void clearOldReports() {
        deleteIfExists(HTML_REPORT);
        deleteIfExists(CSV_REPORT);
        deleteIfExists(EXTENT_HTML_REPORT);
    }

    private void deleteIfExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void addFileToZip(ZipOutputStream zipOut, File file, String fileName) throws IOException {
        if (file != null && file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }
                zipOut.closeEntry();
            }
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}