package adaptor.notion.utils;

import adaptor.notion.behavior.EnumBehaviorManager;
import adaptor.notion.domain.MdBlocks;
import adaptor.notion.domain.SerialNumberedListBlock;

import lombok.extern.slf4j.Slf4j;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.blocks.NumberedListItemBlock;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PageProperty.RichText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for converting Notion blocks to Markdown format.
 * Provides functionality to parse Notion blocks, extract text content,
 * and generate markdown representation.
 */
@Slf4j
public class NotionUtil {
    /**
     * Converts a list of Notion blocks and page properties to markdown blocks.
     *
     * @param notionBlocks List of Notion blocks to convert
     * @param pageInfo Map of page properties containing title and other metadata
     * @return List of converted markdown blocks, including page title as first block
     * @throws IllegalArgumentException if notionBlocks or pageInfo is null
     */
    public static List<MdBlocks> notionPageToMdBlocks(List<Block> notionBlocks, Map<String, PageProperty> pageInfo) {
        if (notionBlocks == null || pageInfo == null) {
            log.error("Notion blocks or page info cannot be null");
            throw new IllegalArgumentException("Notion blocks or page info cannot be null");
        }
        List<MdBlocks> mdBlocks = new ArrayList<>();

        PageProperty titleProperty = pageInfo.get("title");
        if (titleProperty != null) {
            String titleContent = "# " + richTextParser(titleProperty.getTitle());
            mdBlocks.add(new MdBlocks("0", "pageTitle", titleContent, new ArrayList<>(0)));
            log.debug("Added page title: {}", titleContent);
        } else {
            log.warn("No title found in page properties");
        }

        for (Block block : notionBlocks) {
            String id = block.getId();
            String type = block.getType().toString();
            String content = markdownParser(block);
            
            if (content != null && !content.isEmpty()) {
                mdBlocks.add(new MdBlocks(id, type, content, new ArrayList<>(0)));
                log.trace("Added block - Type: {}, ID: {}", type, id);
            } else {
                log.debug("Skipped empty block - Type: {}, ID: {}", type, id);
            }
        }

        log.info("Successfully converted {} Notion blocks to {} markdown blocks", notionBlocks.size(), mdBlocks.size());
        return mdBlocks;
    }

    /**
     * Retrieves child blocks for a given block ID using the Notion API.
     *
     * @param blockId ID of the parent block to retrieve children for
     * @param notionClient NotionClient instance to use for API calls
     * @return List of child blocks, empty list if no children found
     * @throws IllegalArgumentException if blockId is null/empty or notionClient is null
     */
    public static List<Block> getNotionBlocks(String blockId, NotionClient notionClient) {
        if (blockId == null || blockId.trim().isEmpty()) {
            log.error("Block ID cannot be null or empty");
            throw new IllegalArgumentException("Block ID cannot be null or empty");
        }
        log.debug("Block ID validation passed");
    
        if (notionClient == null) {
            log.error("NotionClient cannot be null");
            throw new IllegalArgumentException("NotionClient cannot be null");
        }
        log.debug("NotionClient validation passed");
    
        List<Block> results;
        try {
            results = notionClient.retrieveBlockChildren(blockId, null, null).getResults();
            log.debug("Raw blocks retrieved: {}", results);
            modifyNumberedList(results);
            log.debug("Numbered list modification completed");
            
        } catch (Exception e) {
            log.error("Failed to retrieve blocks for blockId: {}", blockId, e);
            throw e;
        }
        return results;
    }

    /**
     * Retrieves page properties from Notion API for a given page ID.
     *
     * @param pageId The ID of the Notion page to retrieve
     * @param notionClient The Notion API client
     * @return Map of page properties
     * @throws IllegalArgumentException if pageId is null/empty or notionClient is null
     */
    public static Map<String, PageProperty> getNotionPageInfo(String pageId, NotionClient notionClient) {
        log.info("Retrieving Notion page info for pageId: {}", pageId);

        if (pageId == null || pageId.trim().isEmpty()) {
            log.error("Page ID cannot be null or empty");
            throw new IllegalArgumentException("Page ID cannot be null or empty");
        }
        log.debug("Page ID validation passed");

        if (notionClient == null) {
            log.error("NotionClient cannot be null");
            throw new IllegalArgumentException("NotionClient cannot be null");
        }

        try {
            Page page = notionClient.retrievePage(pageId, null);
            Map<String, PageProperty> properties = page.getProperties();
            log.debug("Page properties: {}", properties.keySet());
            
            return properties;
        } catch (Exception e) {
            log.error("Failed to retrieve page info for pageId: {}", pageId, e);
            throw e;
        }
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
            log.error("Block cannot be null");
            throw new IllegalArgumentException("Block cannot be null");
        }
        log.debug("Block validation passed");

        try {
            String result = switch (block.getType()) {
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
                case Image -> EnumBehaviorManager.executeBehavior(BlockType.Image, block);

                default -> {
                    log.warn("Unsupported block type: {}, block ID: {}", block.getType(), block.getId());
                    yield "";
                }
            };

            return result;
        } catch (Exception e) {
            log.error("Failed to parse block to markdown", e);
            throw e;
        }
    }

    /**
     * Parses a list of RichText objects into Markdown formatted text.
     * Handles formatting including bold, italic, strikethrough, code, and links.
     *
     * @param richTexts List of RichText objects to parse
     * @return Markdown formatted string
     */
    public static String richTextParser(List<RichText> richTexts) {
        log.debug("Starting rich text parsing for {} elements", richTexts != null ? richTexts.size() : 0);
    
        if (richTexts == null) {
            log.warn("Rich text list is null");
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (RichText richText : richTexts) {
            if (richText == null) {
                log.warn("Rich text element is null, skipping");
                continue;
            }
            log.trace("Processing rich text: {}", richText.getPlainText());
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
        log.debug("Completed rich text parsing. Result length: {}", result.length());
        return result.toString();
    }

    /**
     * Generates a markdown string from a list of markdown blocks. The method handles special
     * formatting for consecutive list items by removing extra line breaks between them.
     *
     * @param mdBlocks List of markdown blocks to convert to a string. Each block represents
     *                 a distinct markdown element (e.g., paragraph, list item, heading)
     * @return A formatted markdown string with appropriate line breaks
     * @throws IllegalArgumentException if mdBlocks is null
     * @see #isConsecutiveListItems(MdBlocks, MdBlocks)
     */
    public static String generateMarkdownString(List<MdBlocks> mdBlocks) {
        if (mdBlocks == null) {
            throw new IllegalArgumentException("mdBlocks cannot be null");
        }
        StringBuilder markdown = new StringBuilder();
        for (int i = 0; i < mdBlocks.size() ; i++) {
            MdBlocks currentBlock = mdBlocks.get(i);
            boolean isLastBlock = (i + 1 == mdBlocks.size());
            boolean isConsecutiveListItem = !isLastBlock && isConsecutiveListItems(currentBlock, mdBlocks.get(i + 1));
            
            markdown.append(currentBlock.getContent())
                   .append("\n");
            
            if (!isConsecutiveListItem) {
                markdown.append("\n");
            }
        }

        return markdown.toString();
    }

    /**
     * Determines if two markdown blocks represent consecutive list items of the same type.
     * This is used to properly format lists without extra line breaks between items.
     *
     * @param current The current markdown block being processed
     * @param next The next markdown block in sequence
     * @return true if both blocks are list items of the same type (bulleted or numbered),
     *         false otherwise
     * @see #generateMarkdownString(List)
     */
    private static boolean isConsecutiveListItems(MdBlocks current, MdBlocks next) {
        return (current.getType().equals("bulleted_list_item") && next.getType().equals("bulleted_list_item")) ||
               (current.getType().equals("numbered_list_item") && next.getType().equals("numbered_list_item"));
    }

    /**
     * Modifies a list of blocks by replacing numbered list items with serial-numbered versions.
     * Resets numbering when encountering non-list items.
     *
     * @param blocks The list of blocks to process
     * @throws IllegalArgumentException if blocks is null
     */
    public static void modifyNumberedList(List<Block> blocks) {
        if (blocks == null) {
            log.warn("Null blocks list provided to modifyNumberedList");
            throw new IllegalArgumentException("blocks cannot be null");
        }
        
        int serialNumber = 1;
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (BlockType.NumberedListItem.equals(block.getType())) {
                if (!(block instanceof NumberedListItemBlock)) {
                    log.error("Invalid block type encountered: {}", block.getClass());
                    throw new IllegalStateException("Block marked as NumberedListItem but is not of correct type");
                }
                SerialNumberedListBlock serialBlock = new SerialNumberedListBlock(
                    (NumberedListItemBlock) block, 
                    serialNumber++
                );
                blocks.set(i, serialBlock);
            } else {
                serialNumber = 1;
            }
        }
    }

    /**
     * Extracts the programming language from a Code block.
     *
     * @param block The block to extract the language from
     * @return Optional containing the language string, or empty if not a Code block
     * @throws IllegalArgumentException if block is null
     */
    public static Optional<String> getCodeLanguage(Block block) {
        log.debug("Attempting to extract code language from block");
        if (block == null) {
            log.warn("Null block provided to getCodeLanguage");
            throw new IllegalArgumentException("block cannot be null");
        }
        
        if (BlockType.Code.equals(block.getType())) {
            return Optional.ofNullable(block.asCode().getCode().getLanguage());
        }

        log.debug("Block is not a Code block, type: {}", block.getType());
        return Optional.empty();
    }
}

