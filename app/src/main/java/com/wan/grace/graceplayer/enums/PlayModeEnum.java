package com.wan.grace.graceplayer.enums;

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
public enum PlayModeEnum {
    LOOP(0),
    SHUFFLE(1),
    SINGLE(2),
    LIST(3);

    private int value;

    PlayModeEnum(int value) {
        this.value = value;
    }

    public static PlayModeEnum valueOf(int value) {
        switch (value) {
            case 1:
                return SHUFFLE;
            case 2:
                return SINGLE;
            case 3:
                return LIST;
            case 0:
            default:
                return LOOP;
        }
    }

    public int value() {
        return value;
    }
}
