package com.adaptor.notion.domin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Represents a Markdown block converted from a Notion block.
 * Each block contains its unique identifier, type, content,
 * and optional child blocks for nested content structures.
 */
@Data
@AllArgsConstructor
public class MdBlocks {
    /**
     * Unique identifier of the block, inherited from Notion block
     */
    private String blockId;

    /**
     * Type of the block
     */
    private String type;

    /**
     * Markdown-formatted content of the block
     */
    private String content;

    /**
     * List of child blocks, if any
     */
    private List<MdBlocks> children;
}
