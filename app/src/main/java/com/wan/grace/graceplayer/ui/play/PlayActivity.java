package com.wan.grace.graceplayer.ui.play;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.base.MVPBaseActivity;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.music.MusicPlayerContract;
import com.wan.grace.graceplayer.player.IPlayback;
import com.wan.grace.graceplayer.player.PlayMode;
import com.wan.grace.graceplayer.player.PlaybackService;
import com.wan.grace.graceplayer.source.PreferenceManager;
import com.wan.grace.graceplayer.utils.SystemUtils;
import com.wan.grace.graceplayer.utils.TimeUtils;
import com.wan.grace.graceplayer.widget.AlbumCoverView;
import com.wan.grace.graceplayer.widget.IndicatorLayout;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import me.wcy.lrcview.LrcView;

public class PlayActivity extends MVPBaseActivity<PlayView, PlayPresenter> implements PlayView, View.OnClickListener,
        ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener
        , LrcView.OnPlayClickListener, MusicPlayerContract.View, IPlayback.Callback {

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
    @BindView(R.id.vp_play_page)
    private ViewPager vpPlay;
    //    @BindView(R.id.il_indicator)
    private IndicatorLayout ilIndicator;
    //    @BindView(R.id.sb_progress)
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
    private AlbumCoverView mAlbumCoverView;
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
            if (mPlayer.isPlaying()) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initViewPager();
        initView();
//        ilIndicator.create(mViewPagerContent.size());
        initPlayMode();
//        onChangeImpl(mPlayer.getPlayingSong());
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvArtist = findViewById(R.id.tv_artist);
        ilIndicator = findViewById(R.id.il_indicator);
        sbProgress = findViewById(R.id.sb_progress);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        ivPlay = findViewById(R.id.iv_play);
        ivNext = findViewById(R.id.iv_next);
        ivPrev = findViewById(R.id.iv_prev);
        ivMode = findViewById(R.id.iv_mode);
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
        setCoverAndBg(song);
        setLrc(song);
        if (mPlayer.isPlaying()) {
            ivPlay.setSelected(true);
            mAlbumCoverView.start();
        } else {
            ivPlay.setSelected(false);
            mAlbumCoverView.pause();
        }
    }

//    private void initViewPager() {
//        View coverView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_cover, null);
//        View lrcView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_lrc, null);
//        mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
//        mLrcViewSingle = (LrcView) coverView.findViewById(R.id.lrc_view_single);
//        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
//        sbVolume = (SeekBar) lrcView.findViewById(R.id.sb_volume);
//        mAlbumCoverView.initNeedle(mPlayer.isPlaying());
//        mLrcViewFull.setOnPlayClickListener(this);
//        initVolume();
//
//        mViewPagerContent = new ArrayList<>(2);
//        mViewPagerContent.add(coverView);
//        mViewPagerContent.add(lrcView);
//        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));
//    }

    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    public void setListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
        vpPlay.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                onPlayAction();
                break;
            case R.id.iv_next:
                onPlayNextAction();
                break;
            case R.id.iv_prev:
                onLastPlayAction();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (mPlayer.isPlaying() || mPlayer.pause()) {
                int progress = seekBar.getProgress();
                mPlayer.seekTo(progress);

                if (mLrcViewSingle.hasLrc()) {
                    mLrcViewSingle.updateTime(progress);
                    mLrcViewFull.updateTime(progress);
                }
            } else {
                seekBar.setProgress(0);
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
        mPlayPresenter.unsubscribe();
        super.onDestroy();
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
        // Step 1: Song name and artist
        // Step 2: favorite
//        buttonFavoriteToggle.setImageResource(song.isFavorite() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
        // Step 3: Duration
//        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        if (mPlayer.isPlaying()) {
            ivPlay.setSelected(true);
        }
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
        switch (playMode) {
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
        PreferenceManager.setPlayMode(PlayActivity.this, playMode);
        ivMode.setImageLevel(playMode.getValue());
    }

    private void switchPlayMode() {
        PlayMode playMode = PreferenceManager.lastPlayMode(this);
        PlayMode newMode = PlayMode.switchNextMode(playMode);
        PreferenceManager.setPlayMode(PlayActivity.this, newMode);
        mPlayer.setPlayMode(newMode);
        updatePlayMode(newMode);
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

    private void setCoverAndBg(Song song) {
//        mAlbumCoverView.setCoverBitmap(CoverLoader.getInstance().loadRound(song));
//        ivPlayingBg.setImageBitmap(CoverLoader.getInstance().loadBlur(song));
    }

    private void setLrc(Song song) {
//        if (song.getType() == Song.Type.LOCAL) {
//            String lrcPath = FileUtils.getLrcFilePath(song);
//            if (!TextUtils.isEmpty(lrcPath)) {
//                loadLrc(lrcPath);
//            } else {
//                new SearchLrc(music.getArtist(), music.getTitle()) {
//                    @Override
//                    public void onPrepare() {
//                        // 设置tag防止歌词下载完成后已切换歌曲
//                        vpPlay.setTag(music);
//
//                        loadLrc("");
//                        setLrcLabel("正在搜索歌词");
//                    }
//
//                    @Override
//                    public void onExecuteSuccess(@NonNull String lrcPath) {
//                        if (vpPlay.getTag() != music) {
//                            return;
//                        }
//
//                        // 清除tag
//                        vpPlay.setTag(null);
//
//                        loadLrc(lrcPath);
//                        setLrcLabel("暂无歌词");
//                    }
//
//                    @Override
//                    public void onExecuteFail(Exception e) {
//                        if (vpPlay.getTag() != music) {
//                            return;
//                        }
//
//                        // 清除tag
//                        vpPlay.setTag(null);
//
//                        setLrcLabel("暂无歌词");
//                    }
//                }.execute();
//            }
//        } else {
//            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
//            loadLrc(lrcPath);
//        }
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


}
