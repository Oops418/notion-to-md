package adaptor.notion.examples;

import adaptor.notion.MarkdownConverter;
import adaptor.notion.domain.MdBlocks;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class BasicUsage {
    public static void main(String[] args) throws IOException {
        try {
            Properties properties = loadProperties();
            String notionApiSecret = properties.getProperty("notionApiSecret");
            String pageId = properties.getProperty("pageId");
    
            MarkdownConverter converter = MarkdownConverter.getInstance(notionApiSecret);
            List<MdBlocks> mdBlocks = converter.pageToMarkdownBlocks(pageId);
            System.out.println(converter.toMarkdownString(mdBlocks));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Notion page to markdown", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = BasicUsage.class.getClassLoader()
                .getResourceAsStream("local.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("local.properties not found");
            }
            properties.load(inputStream);
            return properties;
        }
    }
}
