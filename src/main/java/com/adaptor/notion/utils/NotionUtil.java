package com.adaptor.notion.utils;

import com.adaptor.notion.domin.MdBlocks;
import com.adaptor.notion.domin.MdElement;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting Notion blocks to Markdown format.
 * Provides functionality to parse Notion blocks, extract text content,
 * and generate markdown representation.
 */
public class NotionUtil {
    private final NotionClient notionClient;

    /**
     * Constructs a NotionUtil with the specified NotionClient.
     *
     * @param notionClient The NotionClient instance to use for API calls
     * @throws IllegalArgumentException if notionClient is null
     */
    public NotionUtil(NotionClient notionClient){
        if (notionClient == null) {
            throw new IllegalArgumentException("NotionClient cannot be null");
        }
        this.notionClient = notionClient;
    }

    /**
     * Converts a list of Notion blocks to markdown blocks.
     *
     * @param notionBlocks List of Notion blocks to convert
     * @return List of converted markdown blocks
     * @throws IllegalArgumentException if notionBlocks is null
     */
    public List<MdBlocks> notionBlocksToMdBlocks(List<Block> notionBlocks){
        if (notionBlocks == null) {
            throw new IllegalArgumentException("Notion blocks cannot be null");
        }

        List<MdBlocks> mdBlocks = new ArrayList<>();

        notionBlocks.forEach(block -> {
            String id = block.getId();
            String type = block.getType().toString();
            String content = markdownParser(block);
            List<MdBlocks> children = new ArrayList<>();

            // if (Boolean.TRUE.equals(block.getHasChildren())){
            //     List<Block> children = getNotionBlocks(block.getId());
            // }

            mdBlocks.add(new MdBlocks(id, type, content, children));
        });

        return mdBlocks;
    }

    /**
     * Retrieves child blocks for a given block ID using the Notion API.
     *
     * @param blockId ID of the parent block
     * @return List of child blocks
     * @throws IllegalArgumentException if blockId is null or empty
     */
    public List<Block> getNotionBlocks(String blockId){
        if (blockId == null || blockId.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        return notionClient.retrieveBlockChildren(blockId, null, null).getResults();
    }

    /**
     * Converts a Notion block to its markdown representation.
     *
     * @param block Notion block to parse
     * @return Markdown formatted string
     * @throws IllegalArgumentException if block is null
     */
    public String markdownParser(Block block){
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        return switch (block.getType()) {
            case HeadingOne -> MdElement.HEADING1.format(getPlainText(block));
            case HeadingTwo -> MdElement.HEADING2.format(getPlainText(block));
            case HeadingThree -> MdElement.HEADING3.format(getPlainText(block));
            case Paragraph -> MdElement.PARAGRAPH.format(getPlainText(block));
            default -> "";
        };
    }

    /**
     * Extracts plain text content from a Notion block.
     *
     * @param block Notion block to extract text from
     * @return Plain text content of the block
     * @throws IllegalArgumentException if block is null
     */
    public String getPlainText(Block block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        return switch (block.getType()) {
            case HeadingOne -> {
                var richText = block.asHeadingOne().getHeading1().getRichText();
                yield richText.isEmpty() ? "" : richText.getFirst().getPlainText();
            }
            case HeadingTwo -> {
                var richText = block.asHeadingTwo().getHeading2().getRichText();
                yield richText.isEmpty() ? "" : richText.getFirst().getPlainText();
            }
            case HeadingThree -> {
                var richText = block.asHeadingThree().getHeading3().getRichText();
                yield richText.isEmpty() ? "" : richText.getFirst().getPlainText();
            }
            case Paragraph -> {
                var richText = block.asParagraph().getParagraph().getRichText();
                yield richText.isEmpty() ? "" : richText.getFirst().getPlainText();
            }
            default -> "";
        };
    }

    /**
     * Generates markdown content from a list of markdown blocks.
     *
     * @param mdBlocks List of markdown blocks to convert
     * @return Markdown formatted string
     * @throws IllegalArgumentException if mdBlocks is null
     */
    public String generateMarkdown(List<MdBlocks> mdBlocks) {
        StringBuilder markdown = new StringBuilder();
        for (MdBlocks block : mdBlocks) {
            appendBlockContent(block, markdown);
        }
        return markdown.toString();
    }

    /**
     * Appends the content of a markdown block to a StringBuilder.
     *
     * @param block    The markdown block to append
     * @param markdown The StringBuilder to append to
     */
    private void appendBlockContent(MdBlocks block, StringBuilder markdown) {
        markdown.append(block.getContent()).append("\n");
        
        // If block has children, recursively process them
        // if (!block.children().isEmpty()) {
        //     for (MdBlocks child : block.children()) {
        //         appendBlockContent(child, markdown);
        //     }
        // }

        markdown.append("\n");
    }
}

