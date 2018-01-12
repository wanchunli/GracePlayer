package com.wan.grace.graceplayer.ui.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.MVPBaseActivity;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.constants.Actions;
import com.wan.grace.graceplayer.music.MusicPlayerContract;
import com.wan.grace.graceplayer.music.MusicPlayerPresenter;
import com.wan.grace.graceplayer.player.IPlayback;
import com.wan.grace.graceplayer.player.PlayMode;
import com.wan.grace.graceplayer.player.PlaybackService;
import com.wan.grace.graceplayer.source.AppRepository;
import com.wan.grace.graceplayer.source.PreferenceManager;
import com.wan.grace.graceplayer.utils.FileUtils;
import com.wan.grace.graceplayer.utils.SystemUtils;
import com.wan.grace.graceplayer.utils.TimeUtils;
import com.wan.grace.graceplayer.utils.wave.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.wcy.lrcview.LrcView;

public class PlayActivity extends MVPBaseActivity<PlayView, PlayPresenter> implements
        MusicPlayerContract.View, IPlayback.Callback, PlayView, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, LrcView.OnPlayClickListener {

    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    @BindView(R.id.ll_content)
    private LinearLayout llContent;
    @BindView(R.id.iv_play_page_bg)
    private ImageView ivPlayingBg;
    //    @BindView(R.id.iv_back)
    private ImageView ivBack;
    //    @BindView(R.id.tv_title)
    private TextView tvTitle;
    //    @BindView(R.id.tv_artist)
    private TextView tvArtist;
    //    @BindView(R.id.vp_play_page)
    private SeekBar sbProgress;
    //    @BindView(R.id.tv_current_time)
    private TextView tvCurrentTime;
    //    @BindView(R.id.tv_total_time)
    private TextView tvTotalTime;
    @BindView(R.id.iv_mode)
    private ImageView ivMode;
    //    @BindView(R.id.iv_play)
    private ImageView ivPlay;
    //    @BindView(R.id.iv_next)
    private ImageView ivNext;
    //    @BindView(R.id.iv_prev)
    private ImageView ivPrev;
    private LrcView mLrcViewSingle;
    private LrcView mLrcViewFull;
    private SeekBar sbVolume;

    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mLastProgress;
    private boolean isDraggingProgress;
    private Handler mHandler = new Handler();
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                int progress = (int) (sbProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress());
                if (progress >= 0 && progress <= sbProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        sbProgress.setProgress(progress, true);
                    } else {
                        sbProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    private void updateProgressTextWithDuration(int duration) {
        tvCurrentTime.setText(TimeUtils.formatDuration(duration));
    }

    private IPlayback mPlayer;
    private MusicPlayerContract.Presenter mPlayPresenter;

    private AudioRecordingDbmHandler handler;
    private AudioRecorder audioRecorder;
    private AudioVisualization audioVisualization;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_play;
    }

    @Override
    protected PlayPresenter createPresenter() {
        return new PlayPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        audioVisualization.onResume();
        IntentFilter filter = new IntentFilter(Actions.VOLUME_CHANGED_ACTION);
        registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioVisualization.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showQuickControl(false);
        new MusicPlayerPresenter(PlayActivity.this, AppRepository.getInstance(), this).subscribe();
        initView();
//        initViewPager();
//      ilIndicator.create(mViewPagerContent.size());
        initPlayMode();
        if (mPlayer != null) {
            onChangeImpl(mPlayer.getPlayingSong());
        }
        startWave();
    }

    public void initView() {
        audioVisualization = (AudioVisualization) findViewById(R.id.visualizer_view);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        sbProgress = findViewById(R.id.sb_progress);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        ivPlay = findViewById(R.id.iv_play);
        ivNext = findViewById(R.id.iv_next);
        ivPrev = findViewById(R.id.iv_prev);
        ivMode = findViewById(R.id.iv_mode);
        //歌词相关
        mLrcViewSingle = (LrcView) findViewById(R.id.lrc_view_single);
        mLrcViewFull = (LrcView) findViewById(R.id.lrc_view_full);
        sbVolume = (SeekBar) findViewById(R.id.sb_volume);
        initVolume();
        //监听事件
        setListener();
    }

    private void startWave() {
        audioRecorder = new AudioRecorder();
        handler = new AudioRecordingDbmHandler();
        audioRecorder.recordingCallback(handler);
        audioVisualization.linkTo(handler);
        audioRecorder.startRecord();
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / sbProgress.getMax()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    private void initPlayMode() {
        int mode = PreferenceManager.lastPlayMode(this).getValue();
        ivMode.setImageLevel(mode);
    }

    private void onChangeImpl(Song song) {
        if (song == null) {
            return;
        }

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        sbProgress.setProgress((int) mPlayer.getProgress());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) song.getDuration());
        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(song.getDuration()));
        setLrc(song);
        if (mPlayer.isPlaying()) {
            ivPlay.setSelected(true);
        } else {
            ivPlay.setSelected(false);
        }
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        mMusicDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (v.getId()) {
                    case R.id.iv_back:
                        onBackPressed();
                        break;
                    case R.id.iv_mode:
                        switchPlayMode();
                        break;
                    case R.id.iv_play:
                        onPlayAction();
                        ivPlay.setSelected(mPlayer.isPlaying());
                        break;
                    case R.id.iv_next:
                        onPlayNextAction();
                        break;
                    case R.id.iv_prev:
                        onLastPlayAction();

                        break;
                }
            }
        }, 500);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
            if (fromUser) {
                updateProgressTextWithDuration(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mProgressCallback);
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            seekTo(getDuration(seekBar.getProgress()));
            if (mPlayer.isPlaying()) {
                int progress = seekBar.getProgress();
                mHandler.removeCallbacks(mProgressCallback);
                mHandler.post(mProgressCallback);
                if (mLrcViewSingle.hasLrc()) {
                    mLrcViewSingle.updateTime(progress);
                    mLrcViewFull.updateTime(progress);
                }
            }
        } else if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    @Override
    public boolean onPlayClick(long time) {
        if (mPlayer.isPlaying() || mPlayer.pause()) {
            mPlayer.seekTo((int) time);
            return true;
        }
        return false;
    }

    @Override
    public void setPresenter(MusicPlayerContract.Presenter presenter) {
        mPlayPresenter = presenter;
    }

    @Override
    public void handleError(Throwable error) {

    }

    @Override
    protected void onDestroy() {
        if (mPlayPresenter != null) {
            mPlayPresenter.unsubscribe();
        }
        audioVisualization.release();
        unregisterReceiver(mVolumeReceiver);
        super.onDestroy();
    }

    @Override
    public void onPlaybackServiceBound(PlaybackService service) {
        mPlayer = service;
        mPlayer.registerCallback(this);
        onChangeImpl(mPlayer.getPlayingSong());
    }

    @Override
    public void onPlaybackServiceUnbound() {
        mPlayer.unregisterCallback(this);
        mPlayer = null;
    }

    @Override
    public void onSongSetAsFavorite(@NonNull Song song) {

    }

    @Override
    public void onSongUpdated(@Nullable Song song) {
        if (song == null) {
            sbProgress.setProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }
        tvTitle.setText(song.getDisplayName());
        tvArtist.setText(song.getArtist());
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mHandler.post(mProgressCallback);
        }
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    @Override
    public void updatePlayMode(PlayMode playMode) {
        if (playMode == null) {
            playMode = PreferenceManager.lastPlayMode(this);
        }
        PreferenceManager.setPlayMode(PlayActivity.this, playMode);
    }

    private void switchPlayMode() {
        PlayMode playMode = PreferenceManager.lastPlayMode(this);
        PlayMode newMode = PlayMode.switchNextMode(playMode);
        PreferenceManager.setPlayMode(PlayActivity.this, newMode);
        mPlayer.setPlayMode(newMode);
        updatePlayMode(newMode);
        initPlayMode();
        switch (newMode) {
            case LOOP:
                showTips(getString(R.string.mode_loop));
                break;
            case SHUFFLE:
                showTips(getString(R.string.mode_shuffle));
                break;
            case SINGLE:
                showTips(getString(R.string.mode_single));
                break;
            case LIST:
                showTips(getString(R.string.mode_list));
                break;
        }
    }

    @Override
    public void updatePlayToggle(boolean play) {
        if (play) {
            ivPlay.setSelected(true);
        } else {
            ivPlay.setSelected(false);
        }
    }

    @Override
    public void updateFavoriteToggle(boolean favorite) {

    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        onSongUpdated(last);
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(@Nullable Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onStartStatus(@Nullable Song curSong) {
        onSongUpdated(curSong);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        updatePlayToggle(isPlaying);
        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    public void onPlayNextAction() {
        if (mPlayer == null) return;

        mPlayer.playNext();
    }

    public void onPlayAction() {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
    }

    public void onLastPlayAction() {
        if (mPlayer == null) return;

        mPlayer.playLast();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ivBack.setEnabled(false);
        mBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivBack.setEnabled(true);
            }
        }, 300);
    }

    private void setLrc(Song song) {
        if (song.getType() == Song.Type.LOCAL) {
            String lrcPath = FileUtils.getLrcFilePath(song);
            if (!TextUtils.isEmpty(lrcPath)) {
//                loadLrc(lrcPath);
            } else {

            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(song.getArtist(), song.getTitle());
//            loadLrc(lrcPath);
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewSingle.loadLrc(file);
        mLrcViewFull.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        mLrcViewSingle.setLabel(label);
        mLrcViewFull.setLabel(label);
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };

}
