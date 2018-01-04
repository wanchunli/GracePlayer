package com.wan.grace.graceplayer.utils;

/**
 * Created by 开发部 on 2018/1/3.
 */

public interface IConstants {
    int MUSICOVERFLOW = 0;

    //歌手和专辑列表点击都会进入MyMusic 此时要传递参数表明是从哪里进入的
    int START_FROM_ARTIST = 1;
    int START_FROM_ALBUM = 2;
    int START_FROM_LOCAL = 3;
    int START_FROM_FOLDER = 4;
    int START_FROM_FAVORITE = 5;
}
