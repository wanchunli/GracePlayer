package com.wan.grace.graceplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Media;

import com.github.promeg.pinyinhelper.Pinyin;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.info.MusicInfo;
import com.wan.grace.graceplayer.utils.IConstants;
import com.wan.grace.graceplayer.utils.PreferencesUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 开发部 on 2018/1/3.
 */

public class MusicUtils implements IConstants {

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private static String[] proj_music = new String[]{
            Media._ID, Media.TITLE,
            Media.DATA, Media.ALBUM_ID,
            Media.ALBUM, Media.ARTIST,
            Media.ARTIST_ID, Media.DURATION, Media.SIZE};

    /**
     * @param context
     * @param from    不同的界面进来要做不同的查询
     * @return
     */
    public static List<MusicInfo> queryMusic(Context context, int from) {
        return queryMusic(context, null, from);
    }


    public static ArrayList<MusicInfo> queryMusic(Context context, String id, int from) {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();

        StringBuilder select = new StringBuilder(" 1=1 and title != ''");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);

        String selectionStatement = "is_music=1 AND title != ''";
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();


        switch (from) {
            case START_FROM_LOCAL:
                ArrayList<MusicInfo> list3 = getMusicListCursor(cr.query(uri, proj_music,
                        select.toString(), null,
                        songSortOrder));
                return list3;
//            case START_FROM_ARTIST:
//                select.append(" and " + Media.ARTIST_ID + " = " + id);
//                return getMusicListCursor(cr.query(uri, proj_music, select.toString(), null,
//                        PreferencesUtility.getInstance(context).getArtistSongSortOrder()));
//            case START_FROM_ALBUM:
//                select.append(" and " + Media.ALBUM_ID + " = " + id);
//                return getMusicListCursor(cr.query(uri, proj_music,
//                        select.toString(), null,
//                        PreferencesUtility.getInstance(context).getAlbumSongSortOrder()));
//            case START_FROM_FOLDER:
//                ArrayList<MusicInfo> list1 = new ArrayList<>();
//                ArrayList<MusicInfo> list = getMusicListCursor(cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_music,
//                        select.toString(), null,
//                        null));
//                for (MusicInfo music : list) {
//                    if (music.data.substring(0, music.data.lastIndexOf(File.separator)).equals(id)) {
//                        list1.add(music);
//                    }
//                }
//                return list1;
            default:
                return null;
        }
    }

    public static ArrayList<MusicInfo> getMusicListCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<MusicInfo> musicList = new ArrayList<>();
        while (cursor.moveToNext()) {
            MusicInfo music = new MusicInfo();
            music.songId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            music.albumId = cursor.getInt(cursor
                    .getColumnIndex(Media.ALBUM_ID));
            music.albumName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            music.albumData = getAlbumArtUri(music.albumId) + "";
            music.duration = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            music.musicName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE));
            music.artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            music.artistId = cursor.getLong(cursor.getColumnIndex(Media.ARTIST_ID));
            String filePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            music.data = filePath;
            music.folder = filePath.substring(0, filePath.lastIndexOf(File.separator));
            music.size = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));
            music.islocal = true;
            music.sort = Pinyin.toPinyin(music.musicName.charAt(0)).substring(0, 1).toUpperCase();
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }

    /**
     * @param context
     * @param from    不同的界面进来要做不同的查询
     * @return
     */
    public static List<Song> querySongs(Context context, int from) {
        return querySong(context, null, from);
    }


    public static ArrayList<Song> querySong(Context context, String id, int from) {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();

        StringBuilder select = new StringBuilder(" 1=1 and title != ''");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);

        String selectionStatement = "is_music=1 AND title != ''";
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();


        switch (from) {
            case START_FROM_LOCAL:
                ArrayList<Song> list3 = getSongListCursor(cr.query(uri, proj_music,
                        select.toString(), null,
                        songSortOrder));
                return list3;
//            case START_FROM_ARTIST:
//                select.append(" and " + Media.ARTIST_ID + " = " + id);
//                return getMusicListCursor(cr.query(uri, proj_music, select.toString(), null,
//                        PreferencesUtility.getInstance(context).getArtistSongSortOrder()));
//            case START_FROM_ALBUM:
//                select.append(" and " + Media.ALBUM_ID + " = " + id);
//                return getMusicListCursor(cr.query(uri, proj_music,
//                        select.toString(), null,
//                        PreferencesUtility.getInstance(context).getAlbumSongSortOrder()));
//            case START_FROM_FOLDER:
//                ArrayList<MusicInfo> list1 = new ArrayList<>();
//                ArrayList<MusicInfo> list = getMusicListCursor(cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_music,
//                        select.toString(), null,
//                        null));
//                for (MusicInfo music : list) {
//                    if (music.data.substring(0, music.data.lastIndexOf(File.separator)).equals(id)) {
//                        list1.add(music);
//                    }
//                }
//                return list1;
            default:
                return null;
        }
    }

    public static ArrayList<Song> getSongListCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<Song> musicList = new ArrayList<Song>();
        while (cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID)));
//            song.setAlbum(cursor.getString(cursor
//                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            song.setAlbum(getAlbumArtUri( cursor.getInt(cursor
                    .getColumnIndex(Media.ALBUM_ID)))+"");
            song.setArtist(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.setDisplayName(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.setDuration(cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION)));
            song.setPath(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA)));
            song.setTitle(cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicList.add(song);
        }
        cursor.close();
        return musicList;
    }

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
}
