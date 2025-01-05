package adaptor.notion.domain;

import lombok.Getter;
import notion.api.v1.model.blocks.NumberedListItemBlock;

@Getter
public class SerialNumberedListBlock extends NumberedListItemBlock {
    private final int serialNumber;

    public SerialNumberedListBlock(NumberedListItemBlock block, int serialNumber) {
        super(
                block.getObjectType(),
                block.getType(),
                block.getId(),
                block.getCreatedTime(),
                block.getCreatedBy(),
                block.getLastEditedTime(),
                block.getLastEditedBy(),
                block.getHasChildren(),
                block.getArchived(),
                block.getParent(),
                block.getNumberedListItem(),
                block.getRequestId(),
                block.getInTrash()
        );
        this.serialNumber = serialNumber;
    }

}
