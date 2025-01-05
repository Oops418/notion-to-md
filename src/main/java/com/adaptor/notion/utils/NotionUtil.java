package com.adaptor.notion.utils;

import com.adaptor.notion.behavior.EnumBehaviorManager;
import com.adaptor.notion.domain.MdBlocks;
import com.adaptor.notion.domain.SerialNumberedListBlock;

import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.blocks.NumberedListItemBlock;
import notion.api.v1.model.pages.PageProperty.RichText;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting Notion blocks to Markdown format.
 * Provides functionality to parse Notion blocks, extract text content,
 * and generate markdown representation.
 */
@Slf4j
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
            case HeadingOne -> EnumBehaviorManager.executeBehavior(BlockType.HeadingOne, block);
            case HeadingTwo -> EnumBehaviorManager.executeBehavior(BlockType.HeadingTwo, block);
            case HeadingThree -> EnumBehaviorManager.executeBehavior(BlockType.HeadingThree, block);
            case Paragraph -> EnumBehaviorManager.executeBehavior(BlockType.Paragraph, block);
            case Quote -> EnumBehaviorManager.executeBehavior(BlockType.Quote, block);
            case NumberedListItem -> EnumBehaviorManager.executeBehavior(BlockType.NumberedListItem, block);
            case BulletedListItem -> EnumBehaviorManager.executeBehavior(BlockType.BulletedListItem, block);
            case Code -> EnumBehaviorManager.executeBehavior(BlockType.Code, block);
            case Bookmark -> EnumBehaviorManager.executeBehavior(BlockType.Bookmark, block);
            case Divider -> EnumBehaviorManager.executeBehavior(BlockType.Divider, block);

            default -> {
                log.warn("Unsupported block type: {}, block ID: {}", block.getType(), block.getId());
                yield "";
            }
        };
    }

    public static String richTextParser(List<RichText> richTexts) {
        if (richTexts == null) {
            log.warn("Rich text list is null");
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (RichText richText : richTexts) {
            if (richText == null) {
                log.warn("Rich text is null");
                continue;
            }
            StringBuilder text = new StringBuilder(richText.getPlainText());
            if (Boolean.TRUE.equals(richText.getAnnotations().getBold())) {
                text.insert(0, "**")
                        .insert(text.length(), "**");
            }
            if (Boolean.TRUE.equals(richText.getAnnotations().getItalic())) {
                text.insert(0, "*")
                        .insert(text.length(), "*");
            }
            if (Boolean.TRUE.equals(richText.getAnnotations().getStrikethrough())) {
                text.insert(0, "~~")
                        .insert(text.length(), "~~");
            }
            if (Boolean.TRUE.equals(richText.getAnnotations().getCode())) {
                text.insert(0, "`")
                        .insert(text.length(), "`");
            }
            if (richText.getText().getLink() != null) {
                text.insert(0, "[").
                        insert(text.length(), "](" + richText.getText().getLink().getUrl() + ")");
            }
            result.append(text);
        }
        return result.toString();
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
            markdown.append(block.getContent()).append("\n");
            markdown.append("\n");
        }
        return markdown.toString();
    }

    public static void modifyNumberedList(List<Block> blocks) {
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

    public static String getCodeLanguage(Block block) {
        if (block.getType().equals(BlockType.Code)) {
            return block.asCode().getCode().getLanguage();
        }
        return null;
    }

}

