package adaptor.notion;

import adaptor.notion.domain.MdBlocks;
import adaptor.notion.log.LoggerFactoryWrapper;
import adaptor.notion.utils.NotionClientWrapper;
import adaptor.notion.log.NotionLoggerWrapper;
import adaptor.notion.utils.NotionUtil;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.pages.PageProperty;
import org.slf4j.Logger;
import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Converts Notion pages to Markdown format
 */

public class MarkdownConverter implements Closeable {
    private static final Logger log = LoggerFactoryWrapper.getLogger(MarkdownConverter.class);
    private static volatile MarkdownConverter instance;
    private final NotionClientWrapper clientWrapper;

    /**
     * Creates a new MarkdownConverter
     * @param notionApiSecret Notion API secret key
     * @throws IllegalArgumentException if API secret is null/empty
     */
    private MarkdownConverter(final String notionApiSecret) {
        if (notionApiSecret == null || notionApiSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("API secret cannot be null or empty");
        }
        this.clientWrapper = NotionClientWrapper.builder()
                .token(notionApiSecret)
                .logger(new NotionLoggerWrapper())
                .build();
    }

    /**
     * Gets singleton instance of MarkdownConverter
     * @param notionApiSecret Notion API secret key
     * @return MarkdownConverter instance
     */
    public static MarkdownConverter getInstance(final String notionApiSecret) {
        if (instance == null) {
            synchronized (MarkdownConverter.class) {
                if (instance == null) {
                    instance = new MarkdownConverter(notionApiSecret);
                }
            }
        }
        return instance;
    }

    /**
     * Converts a Notion page to markdown blocks
     * @param pageId Notion page ID
     * @return List of markdown blocks
     * @throws IllegalArgumentException if page ID is invalid
     */
    public List<MdBlocks> pageToMarkdownBlocks(String pageId) {
        if (pageId == null || pageId.trim().isEmpty()) {
            log.error("Page ID cannot be null or empty");
            throw new IllegalArgumentException("Page ID cannot be null or empty");
        }
        List<Block> notionBlocks = NotionUtil.getNotionBlocks(pageId, clientWrapper.getClient());
        Map<String, PageProperty> pageInfo = NotionUtil.getNotionPageInfo(pageId, clientWrapper.getClient());
        return NotionUtil.notionPageToMdBlocks(notionBlocks, pageInfo);
    }

    /**
     * Converts markdown blocks to a markdown string
     * @param mdBlocks List of markdown blocks
     * @return Markdown string
     * @throws IllegalArgumentException if blocks are null
     */
    public String toMarkdownString(List<MdBlocks> mdBlocks) {
        if (mdBlocks == null) {
            log.error("MdBlocks cannot be null");
            throw new IllegalArgumentException("MdBlocks cannot be null");
        }
        return NotionUtil.generateMarkdownString(mdBlocks);
    }

    @Override
    public void close() {
        clientWrapper.close();
    }
}
