import org.apache.commons.csv.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        String inputCsv = "src/main/resources/testmigration.csv";
        String outputCsv = "src/main/resources/transformed_for_import.csv";

        try (
                Reader reader = new InputStreamReader(new FileInputStream(inputCsv), StandardCharsets.UTF_8);
                Writer writer = new OutputStreamWriter(new FileOutputStream(outputCsv), StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
                CSVPrinter printer = new CSVPrinter(writer,
                        CSVFormat.DEFAULT.withHeader(parser.getHeaderMap().keySet().toArray(new String[0])))
        ) {
            for (CSVRecord record : parser) {
                Map<String, String> cleanedRecord = new LinkedHashMap<>();
                for (String header : parser.getHeaderMap().keySet()) {
                    String value = record.get(header);
                    if (header.equalsIgnoreCase("Description")) {
                        cleanedRecord.put(header, convertHtmlToMarkdown(value));
                    } else {
                        cleanedRecord.put(header, value);
                    }
                }
                printer.printRecord(cleanedRecord.values());
            }

            System.out.println("CSV transformation completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertHtmlToMarkdown(String html) {
        Document doc = Jsoup.parse(html);

        // Replace basic block elements with line breaks
        doc.select("br").append("\\n");
        doc.select("p").prepend("\\n");

        // Convert headings to markdown
        for (int i = 1; i <= 6; i++) {
            for (Element heading : doc.select("h" + i)) {
                heading.prepend("\\n" + "#".repeat(i) + " ");
            }
        }

        // Bold and italic
        doc.select("b, strong").forEach(e -> e.replaceWith(new TextNode("**" + e.text() + "**")));
        doc.select("i, em").forEach(e -> e.replaceWith(new TextNode("_" + e.text() + "_")));

        // Links
        doc.select("a").forEach(a -> {
            String markdown = "[" + a.text() + "](" + a.attr("href") + ")";
            a.replaceWith(new TextNode(markdown));
        });

        // Lists
        doc.select("li").forEach(li -> li.prepend("• ").append("\\n"));
        doc.select("ul, ol").forEach(ul -> ul.prepend("\\n"));

        // Blockquotes
        doc.select("blockquote").forEach(e -> e.prepend("> ").append("\\n"));

        // Code blocks
        doc.select("code").forEach(e -> e.replaceWith(new TextNode("`" + e.text() + "`")));
        doc.select("pre").forEach(e -> e.replaceWith(new TextNode("\\n```\n" + e.text() + "\n```\\n")));

        // Convert HTML to plain text
        String markdown = doc.text();

        // Cleanup newlines
        markdown = markdown.replaceAll("\\\\n", "\n").trim();

        return markdown;
    }
}
