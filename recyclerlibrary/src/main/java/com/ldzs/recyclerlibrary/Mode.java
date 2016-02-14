package com.ldzs.recyclerlibrary;

/**
 * Created by cz on 16/1/20
 * 刷新模式设定
 */
public enum Mode {

    BOTH(0x0), PULL_FROM_START(0x1), PULL_FROM_END(0x2),DISABLED(0x3);

    static Mode getDefault() {
        return PULL_FROM_START;
    }

    private int mIntValue;

    Mode(int modeInt) {
        mIntValue = modeInt;
    }

    /**
     * @return 是否启用刷新
     */
    public boolean disable() {
        return !(this == DISABLED);
    }

    /**
     * @return 是否启用刷新头
     */
    public boolean disableHeader() {
        return this == PULL_FROM_START || this == BOTH;
    }

    /**
     * @return 是否启用刷新尾
     */
    public boolean disableFooter() {
        return this == PULL_FROM_END || this == BOTH;
    }

}