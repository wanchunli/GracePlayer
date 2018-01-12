package com.wan.grace.graceplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cleveroad.audiovisualization.AudioVisualization;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wan.grace.graceplayer.R;
import com.wan.grace.graceplayer.bean.PlayList;
import com.wan.grace.graceplayer.bean.Song;
import com.wan.grace.graceplayer.event.PlaySongEvent;
import com.wan.grace.graceplayer.manager.AppContext;
import com.wan.grace.graceplayer.music.MusicPlayerContract;
import com.wan.grace.graceplayer.music.MusicPlayerPresenter;
import com.wan.grace.graceplayer.player.IPlayback;
import com.wan.grace.graceplayer.player.PlayMode;
import com.wan.grace.graceplayer.player.PlaybackService;
import com.wan.grace.graceplayer.source.AppRepository;
import com.wan.grace.graceplayer.source.PreferenceManager;
import com.wan.grace.graceplayer.ui.main.MainActivity;
import com.wan.grace.graceplayer.ui.play.PlayActivity;
import com.wan.grace.graceplayer.utils.BlurUtils;
import com.wan.grace.graceplayer.utils.RxBus;
import com.wan.grace.graceplayer.utils.wave.AudioRecorder;
import com.wan.grace.graceplayer.utils.wave.AudioRecordingDbmHandler;

import jp.wasabeef.glide.transformations.BlurTransformation;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GraceControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraceControlFragment extends BaseFragment implements MusicPlayerContract.View, IPlayback.Callback {

    private ProgressBar mProgress;
    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    private ImageView mControlBg;
    private ImageView mPlayPause;
    private TextView mTitle;
    private TextView mArtist;
    private SimpleDraweeView mAlbumArt;
    private View rootView;
    private ImageView playQueue, next;
    private String TAG = "QuickControlsFragment";
    private static GraceControlFragment fragment;

    private AudioRecordingDbmHandler handler;
    private AudioRecorder audioRecorder;
    private AudioVisualization audioVisualization;

    public GraceControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GraceControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraceControlFragment newInstance() {
        GraceControlFragment fragment = new GraceControlFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grace_control, container, false);
        this.rootView = rootView;
        mControlBg = rootView.findViewById(R.id.grace_control_image);
//        String filePath="file:///android_asset/loading_bg.jpg";
        Resources r = this.getContext().getResources();
        Bitmap bitmap=BitmapFactory.decodeResource(r, R.mipmap.loading_bg);
//        Bitmap bitmap= BitmapFactory.decodeFile(filePath);
        Bitmap newBitmap = BlurUtils.fastblur(bitmap,16);
        mControlBg.setImageBitmap(newBitmap);
//        loadGraceBg(mControlBg);
        mPlayPause = (ImageView) rootView.findViewById(R.id.control);
        mProgress = (ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mTitle = (TextView) rootView.findViewById(R.id.playbar_info);
        mArtist = (TextView) rootView.findViewById(R.id.playbar_singer);
        mAlbumArt = (SimpleDraweeView) rootView.findViewById(R.id.playbar_img);
        next = (ImageView) rootView.findViewById(R.id.play_next);
        playQueue = (ImageView) rootView.findViewById(R.id.play_list);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayAction(v);
                mPlayPause.setImageResource(mPlayer.isPlaying() ? R.drawable.playbar_btn_pause
                        : R.drawable.playbar_btn_play);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayNextAction(v);
            }
        });
        playQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "队列", Toast.LENGTH_LONG).show();
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppContext.getInstance(), PlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppContext.getInstance().startActivity(intent);
            }
        });
        new MusicPlayerPresenter(getActivity(), AppRepository.getInstance(), this).subscribe();
        audioVisualization = (AudioVisualization) rootView.findViewById(R.id.visualizer_view);
        startWave();
        return rootView;
    }

    private void startWave() {

        audioRecorder = new AudioRecorder();
        handler = new AudioRecordingDbmHandler();
        audioRecorder.recordingCallback(handler);
        audioVisualization.linkTo(handler);
        audioRecorder.startRecord();
    }

    private void loadGraceBg(ImageView graceImage) {
        Glide.with(getActivity())
                .load("file:///android_asset/loading_bg.jpg")
                .error(R.drawable.me)
                .placeholder(R.drawable.me)
                .crossFade(1000)
                .bitmapTransform(new BlurTransformation(getActivity(), 15, 3))
                .into(graceImage);
    }

    private IPlayback mPlayer;

    private Handler mHandler = new Handler();

    private MusicPlayerContract.Presenter mPresenter;

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (mPlayer.isPlaying()) {
                int progress = (int) (mProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                if (progress >= 0 && progress <= mProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mProgress.setProgress(progress, true);
                    } else {
                        mProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof PlaySongEvent) {
                            onPlaySongEvent((PlaySongEvent) o);
                        }
//                        else if (o instanceof PlayListNowEvent) {
//                            onPlayListNowEvent((PlayListNowEvent) o);
//                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onPlaySongEvent(PlaySongEvent event) {
        Song song = event.song;
        playSong(song);
    }

//    private void onPlayListNowEvent(PlayListNowEvent event) {
//        PlayList playList = event.playList;
//        int playIndex = event.playIndex;
//        playSong(playList, playIndex);
//    }

    // Music Controls
    private void playSong(Song song) {
        PlayList playList = new PlayList(song);
        playSong(playList, 0);
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / mProgress.getMax()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
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
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onDestroyView() {
        mPresenter.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void setPresenter(MusicPlayerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void handleError(Throwable error) {

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

    //    @Override
    public void onSongUpdated(@Nullable Song song) {
        if (song == null) {
            mProgress.setProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }
        // Step 1: Song name and artist
        mTitle.setText(song.getDisplayName());
        mArtist.setText(song.getArtist());
        Glide.with(getActivity()).load(song.getAlbum())
                .into(mAlbumArt);
        // Step 2: favorite
//        buttonFavoriteToggle.setImageResource(song.isFavorite() ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
        // Step 3: Duration
//        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        // Step 4: Keep these things updated
        // - Album rotation
        // - Progress(textViewProgress & seekBarProgress)
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mHandler.post(mProgressCallback);
        }

    }

    @Override
    public void updatePlayMode(PlayMode playMode) {

    }

    @Override
    public void updatePlayToggle(boolean play) {
        mPlayPause.setImageResource(play ? R.drawable.playbar_btn_pause
                : R.drawable.playbar_btn_play);
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

    public void onPlayNextAction(View view) {
        if (mPlayer == null) return;

        mPlayer.playNext();
    }

    public void onPlayAction(View view) {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
    }
}
