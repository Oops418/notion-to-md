package com.adaptor.notion.utils;

import com.adaptor.notion.domain.MdBlocks;
import com.adaptor.notion.enums.MdElementEnum;
import com.adaptor.notion.domain.SerialNumberedListBlock;

import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.blocks.NumberedListItemBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting Notion blocks to Markdown format.
 * Provides functionality to parse Notion blocks, extract text content,
 * and generate markdown representation.
 */
public class NotionUtil {
    /**
     * Converts a list of Notion blocks to markdown blocks.
     *
     * @param notionBlocks List of Notion blocks to convert
     * @return List of converted markdown blocks
     * @throws IllegalArgumentException if notionBlocks is null
     */
    public static List<MdBlocks> notionBlocksToMdBlocks(List<Block> notionBlocks) {
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
    public static List<Block> getNotionBlocks(String blockId, NotionClient notionClient) {
        if (blockId == null || blockId.trim().isEmpty()) {
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        List<Block> results = notionClient.retrieveBlockChildren(blockId, null, null).getResults();

        modifyNumberedList(results);
        return results;
    }

    /**
     * Converts a Notion block to its markdown representation.
     *
     * @param block Notion block to parse
     * @return Markdown formatted string
     * @throws IllegalArgumentException if block is null
     */
    public static String markdownParser(Block block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        return switch (block.getType()) {
            case HeadingOne -> MdElementEnum.HEADING1.format(getPlainText(block));
            case HeadingTwo -> MdElementEnum.HEADING2.format(getPlainText(block));
            case HeadingThree -> MdElementEnum.HEADING3.format(getPlainText(block));
            case Paragraph -> MdElementEnum.PARAGRAPH.format(getPlainText(block));
            case Quote -> MdElementEnum.QUOTE.format(getPlainText(block));
            case NumberedListItem ->
                    ((SerialNumberedListBlock) block).getSerialNumber() +
                    ". " +
                    MdElementEnum.NUMBERED_LIST_ITEM.format(getPlainText(block));
            case BulletedListItem ->  MdElementEnum.BULLETED_LIST_ITEM.format(getPlainText(block));

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
    public static String getPlainText(Block block) {
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
            case Quote -> {
                var richText = block.asQuote().getQuote().getRichText();
                if (richText.isEmpty()) {
                    yield "";
                }
                String plainText = richText.getFirst().getPlainText();
                String[] lines = plainText.split("\n");
                yield String.join("\n> ", lines);
            }
            case NumberedListItem -> {
                var richText = block.asNumberedListItem().getNumberedListItem().getRichText();
                yield richText.isEmpty() ? "" : richText.getFirst().getPlainText();
            }
            case BulletedListItem -> {
                var richText = block.asBulletedListItem().getBulletedListItem().getRichText();
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
    public static String generateMarkdown(List<MdBlocks> mdBlocks) {
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
    private static void appendBlockContent(MdBlocks block, StringBuilder markdown) {
        markdown.append(block.getContent()).append("\n");

        // If block has children, recursively process them
        // if (!block.children().isEmpty()) {
        //     for (MdBlocks child : block.children()) {
        //         appendBlockContent(child, markdown);
        //     }
        // }

        markdown.append("\n");
    }

    private static void modifyNumberedList(List<Block> blocks) {
        int serialNumber = 1;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block.getType().equals(BlockType.NumberedListItem)) {
                SerialNumberedListBlock serialBlock = new SerialNumberedListBlock((NumberedListItemBlock) block, serialNumber++);
                blocks.set(i, serialBlock);
            } else {
                serialNumber = 1;
            }
        }
    }
}

