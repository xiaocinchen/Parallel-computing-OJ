package com.offer.oj.domain.enums;

public enum SeparatorEnum {
    /**
     * 逗号
     */
    COMMA(","),
    /**
     * 冒号
     */
    COLON(":"),
    /**
     * 分号
     */
    SEMICOLON(";"),
    /**
     * 线
     */
    LINE("-"),
    /**
     * 下划线
     */
    UNDERLINE("_"),
    /**
     * 竖线
     */
    VERTICAL_LINE("|"),
    /**
     * &
     */
    AND("&"),
    /**
     * .
     */
    DOT("."),
    /**
     * 空格
     */
    SPACE(" "),
    /**
     * 空字符串
     */
    EMPTY(""),
    /**
     * @
     */
    AT("@"),

    /**
     * 斜线
     */
    SLASH("/"),
    /**
     * 波浪线
     */
    WAVE("~"),
    /**
     * ...
     */
    ELLIPSIS("..."),

    /**
     * %
     */
    PERCENT("%"),

    /**
     * $
     */
    DOLLAR("$"),

    /**
     * *
     */
    STAR("*"),

    /**
     * 、
     */
    PAUSE("、"),

    /**
     * 、
     */
    POUND("#"),

    /**
     * 、
     */
    EQUALS("="),
    //-----------------------------------------------
    ;

    /**
     * 分隔符
     */
    private String separator;


    SeparatorEnum(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    /**
     * 封装成用特定符号分隔的数据
     */
    public String packData(Object... args) {
        StringBuilder dataSb = new StringBuilder();
        for (Object obj : args) {
            dataSb.append(getSeparator()).append(obj.toString());
        }
        return dataSb.length() > 0 ? dataSb.substring(1) : "";

    }
}
