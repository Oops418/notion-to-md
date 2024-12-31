package com.adaptor.notion;

import com.adaptor.notion.domin.MdBlocks;
import com.adaptor.notion.utils.NotionClientWrapper;
import com.adaptor.notion.utils.NotionLoggerWrapper;
import com.adaptor.notion.utils.NotionUtil;

import java.io.Closeable;
import java.util.List;

/**
 * Converts Notion pages to Markdown format
 */
public class MarkdownConverter implements Closeable {
    private static volatile MarkdownConverter instance;
    private final NotionUtil notionUtil;
    private final NotionClientWrapper wrapper;

    /**
     * Creates a new MarkdownConverter
     * @param notionApiSecret Notion API secret key
     * @throws IllegalArgumentException if API secret is null/empty
     */
    private MarkdownConverter(final String notionApiSecret) {
        if (notionApiSecret == null || notionApiSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("API secret cannot be null or empty");
        }
        this.wrapper = NotionClientWrapper.builder()
                .token(notionApiSecret)
                .logger(new NotionLoggerWrapper())
                .build();
        this.notionUtil = new NotionUtil(wrapper.getClient());
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
            throw new IllegalArgumentException("Page ID cannot be null or empty");
        }
        return notionUtil.notionBlocksToMdBlocks(notionUtil.getNotionBlocks(pageId));
    }

    /**
     * Converts markdown blocks to a markdown string
     * @param mdBlocks List of markdown blocks
     * @return Markdown string
     * @throws IllegalArgumentException if blocks are null
     */
    public String toMarkdownString(List<MdBlocks> mdBlocks) {
        if (mdBlocks == null) {
            throw new IllegalArgumentException("MdBlocks cannot be null");
        }
        return notionUtil.generateMarkdown(mdBlocks);
    }

    @Override
    public void close() {
        wrapper.close();
    }
}
