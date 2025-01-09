package adaptor.notion.behavior;

import adaptor.notion.domain.SerialNumberedListBlock;
import adaptor.notion.utils.NotionUtil;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.pages.PageProperty.RichText;

import java.util.EnumMap;
import java.util.List;

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
        behaviorMap.put(BlockType.Code, new CodeBehavior());
        behaviorMap.put(BlockType.Bookmark, new BookmarkBehavior());
        behaviorMap.put(BlockType.Divider, new DividerBehavior());
        behaviorMap.put(BlockType.Image, new ImageBehavior());

    }

    public static String executeBehavior(BlockType type, Block block) {
        BehaviorStrategy strategy = behaviorMap.get(type);
        if (strategy != null) {
            return strategy.format(block);
        } else {
            throw new IllegalArgumentException("No behavior found for element: " + type);
        }
    }

    public interface BehaviorStrategy {
        String format(Block block);
    }

    public static class ParagraphBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asParagraph().getParagraph().getRichText();
            return NotionUtil.richTextParser(richTexts);
        }
    }

    public static class Heading1Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asHeadingOne().getHeading1().getRichText();
            return "# " + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class Heading2Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asHeadingTwo().getHeading2().getRichText();
            return "## " + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class Heading3Behavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asHeadingThree().getHeading3().getRichText();
            return "### " + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class QuoteBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asQuote().getQuote().getRichText();
            return "> " + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class NumberedListBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asNumberedListItem().getNumberedListItem().getRichText();
            return ((SerialNumberedListBlock) block).getSerialNumber()
                    + ". "
                    + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class CodeBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asCode().getCode().getRichText();
            return "```"
                    + NotionUtil.getCodeLanguage(block) + "\n"
                    + NotionUtil.richTextParser(richTexts) + "\n"
                    + "```";
        }
    }

    public static class BulletedListBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            List<RichText> richTexts = block.asBulletedListItem().getBulletedListItem().getRichText();
            return "- " + NotionUtil.richTextParser(richTexts);
        }
    }

    public static class BookmarkBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "[Bookmark]("
                    + block.asBookmark().getBookmark().getUrl()
                    + ")";
        }
    }

    public static class DividerBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "---";
        }
    }

    public static class ImageBehavior implements BehaviorStrategy {
        @Override
        public String format(Block block) {
            return "![Image]("
                    + block.asImage().getImage().getFile().getUrl()
                    + ")";
        }
    }
}
