package com.adaptor.notion.domin;

public enum MdElement {
    HEADING1 {
        @Override
        public String format(String content) {
            return "# " + content;
        }
    },
    HEADING2 {
        @Override
        public String format(String content) {
            return "## " + content;
        }
    },
    HEADING3 {
        @Override
        public String format(String content) {
            return "### " + content;
        }
    },
    PARAGRAPH {
        @Override
        public String format(String content) {
            return content;
        }
    };
    public abstract String format(String content);
}
