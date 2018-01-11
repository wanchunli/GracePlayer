package com.wan.grace.graceplayer.player;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:48 PM
 * Desc: PlayMode
 */
public enum PlayMode {
    LOOP(0),
    SHUFFLE(1),
    SINGLE(2),
    LIST(3);

    private int value;

    PlayMode(int value) {
        this.value = value;
    }

    public static PlayMode getDefault() {
        return LOOP;
    }

    public static PlayMode getMode(int code) {
        switch(code){
            case 0:
                return LOOP;
            case 1:
                return SHUFFLE;
            case 2:
                return SINGLE;
            case 3:
                return LIST;
        }
        return LOOP;
    }

    public static PlayMode switchNextMode(PlayMode current) {
        if (current == null) return getDefault();

        switch (current) {
            case LOOP:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LIST;
            case LIST:
                return LOOP;
        }
        return getDefault();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
