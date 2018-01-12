package com.wan.grace.graceplayer.fragment;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.adapter.BaseAdapter;
import com.wan.grace.graceplayer.adapter.LocalMusicAdapter;
import com.wan.grace.graceplayer.bean.PlayList;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.event.PlaySongEvent;
import com.wan.grace.graceplayer.manager.Constants;
import com.wan.grace.graceplayer.music.MusicPlayerContract;
import com.wan.grace.graceplayer.music.MusicPlayerPresenter;
import com.wan.grace.graceplayer.player.IPlayback;
import com.wan.grace.graceplayer.player.PlayMode;
import com.wan.grace.graceplayer.player.PlaybackService;
import com.wan.grace.graceplayer.source.AppRepository;
import com.wan.grace.graceplayer.source.PreferenceManager;
import com.wan.grace.graceplayer.utils.IConstants;
import com.wan.grace.graceplayer.utils.MusicUtils;
import com.wan.grace.graceplayer.utils.RxBus;
import com.wan.grace.graceplayer.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment implements MusicPlayerContract.View, IPlayback.Callback{

    private LocalMusicAdapter musicAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Song> musicList = new ArrayList<>();
    private long artistID = -1;

    private IPlayback mPlayer;

    private MusicPlayerContract.Presenter mPresenter;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new LocalMusicAdapter(getActivity(), musicList);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);
        setItemDecoration();
        if(hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            reloadAdapter();
        }else{
            requestPersions(Constants.READ_EXTERNAL_CODE,Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        musicAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                Song song = musicList.get(position);
                playSong(musicList.get(position),position);
            }
        });
        new MusicPlayerPresenter(getActivity(), AppRepository.getInstance(), this).subscribe();
        return view;
    }

    //设置分割线
    private void setItemDecoration() {
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST);
//        recyclerView.addItemDecoration(itemDecoration);
    }

    //更新adapter界面
    public void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                ArrayList<Song> artistList = (ArrayList) MusicUtils.querySong(mContext, artistID + "", IConstants.START_FROM_LOCAL);
                musicList.clear();
                musicList.addAll(artistList);
                musicAdapter.setList(artistList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                musicAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void playSong(Song song,int playIndex) {
        PlayList playList = new PlayList(song);
        playList.addSong(musicList,0);
        playSong(playList, playIndex);
    }

    private void playSong(PlayList playList, int playIndex) {

        if (playList == null) return;
        playList.setPlayMode(PreferenceManager.lastPlayMode(getActivity()));
        // boolean result =
        mPlayer.play(playList, playIndex);
        Song song = playList.getCurrentSong();
        onSongUpdated(song);
    }

    @Override
    public void setPresenter(MusicPlayerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaybackServiceBound(PlaybackService service) {
        mPlayer = service;
        mPlayer.registerCallback(this);
    }

    @Override
    public void onPlaybackServiceUnbound() {
        mPlayer.unregisterCallback(this);
        mPlayer = null;
    }

    @Override
    public void onDestroyView() {
        mPresenter.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onSongSetAsFavorite(@NonNull Song song) {

    }

    @Override
    public void onSongUpdated(@Nullable Song song) {

    }

    @Override
    public void updatePlayMode(PlayMode playMode) {

    }

    @Override
    public void updatePlayToggle(boolean play) {

    }

    @Override
    public void updateFavoriteToggle(boolean favorite) {

    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {

    }

    @Override
    public void onStartStatus(@Nullable Song curSong) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {

    }
}
