package com.wan.grace.graceplayer.event;


import com.wan.grace.graceplayer.bean.Song;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 9:15 AM
 * Desc: FavoriteChangeEvent
 */
public class FavoriteChangeEvent {

    public Song song;

    public FavoriteChangeEvent(Song song) {
        this.song = song;
    }
}
