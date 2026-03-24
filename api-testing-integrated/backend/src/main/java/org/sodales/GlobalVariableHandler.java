package org.sodales;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GlobalVariableHandler extends JSONFilePathHandler {

    List<String> totalvariables = new ArrayList<>();

    public void setGlobalVariable(String jsonorxmlpath, String globalvariablename, String contenttoread, String xmlorjson) {
        try {
            if (xmlorjson.equalsIgnoreCase("json")) {
                if (!jsonorxmlpath.equalsIgnoreCase("na")) {
                    System.setProperty(globalvariablename, readJsonPath(contenttoread, jsonorxmlpath));
                    System.out.println(System.getProperty(globalvariablename));
                    totalvariables.add(globalvariablename);
                }
            } else if (xmlorjson.equalsIgnoreCase("xml")) {
                if (!jsonorxmlpath.equalsIgnoreCase("na")) {
                    System.setProperty(globalvariablename, readXmlPath(contenttoread, jsonorxmlpath));
                    System.out.println(System.getProperty(globalvariablename));
                    totalvariables.add(globalvariablename);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Either there is an error while setting the global variable as a property or jsonpath is not correct or content is not correct");
        }
    }

    public String globalvariablevaluereplacer(String body) {
        try {

            Properties current = System.getProperties();
            for (String key : current.stringPropertyNames()) {
                if (!totalvariables.isEmpty()) {
                    for (String variable : totalvariables) {
                        if (key.equalsIgnoreCase(variable)) {
                            System.out.println(key + " = " + current.getProperty(key));
                            body = body.replace(key, current.getProperty(key));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }
}
