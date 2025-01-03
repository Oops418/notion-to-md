package com.adaptor.notion.behavior;

import com.adaptor.notion.domain.SerialNumberedListBlock;
import com.adaptor.notion.utils.NotionUtil;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;

import java.util.EnumMap;

public class EnumBehaviorManager {
    private static final EnumMap<BlockType, BehaviorStrategy> behaviorMap = new EnumMap<>(BlockType.class);

    static {
        behaviorMap.put(BlockType.Paragraph, new ParagraphBehavior());
        behaviorMap.put(BlockType.HeadingOne, new Heading1Behavior());
        behaviorMap.put(BlockType.HeadingTwo, new Heading2Behavior());
        behaviorMap.put(BlockType.HeadingThree, new Heading3Behavior());
        behaviorMap.put(BlockType.Quote, new QuoteBehavior());
        behaviorMap.put(BlockType.BulletedListItem, new BulletedListBehavior());
        behaviorMap.put(BlockType.NumberedListItem, new NumberedListBehavior());
    }

    public static String executeBehavior(BlockType type, Block block) {
        BehaviorStrategy strategy = behaviorMap.get(type);
        if (strategy != null) {
            return strategy.format(block);
        }else {
            throw new IllegalArgumentException("No behavior found for element: " + type);
        }
    }

    public interface BehaviorStrategy {
        String format(Block block);
    }

    public static class ParagraphBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return NotionUtil.getPlainText(block);
        }
    }

    public static class Heading1Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "# " + NotionUtil.getPlainText(block);
        }
    }

    public static class Heading2Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "## " + NotionUtil.getPlainText(block);
        }
    }

    public static class Heading3Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "### " + NotionUtil.getPlainText(block);
        }
    }

    public static class QuoteBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "> " + NotionUtil.getPlainText(block);
        }
    }

    public static class NumberedListBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return ((SerialNumberedListBlock) block).getSerialNumber()
                    + ". "
                    + NotionUtil.getPlainText(block);
        }
    }

    public static class BulletedListBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "- " + NotionUtil.getPlainText(block);
        }
    }
}
