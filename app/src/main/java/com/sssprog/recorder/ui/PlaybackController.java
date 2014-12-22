package com.sssprog.recorder.ui;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sssprog.recorder.Config;
import com.sssprog.recorder.R;
import com.sssprog.recorder.utils.LogHelper;
import com.sssprog.recorder.utils.Utils;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PlaybackController extends MediaController {

    private static final String TAG = LogHelper.getTag(PlaybackController.class);
    private static final long TIMER_DELAY = 50;
    private static final String STATE_POSITION = "playback_position";

    @InjectView(R.id.time)
    TextView timeView;
    @InjectView(R.id.record_play)
    ImageButton btnRecord;
    @InjectView(R.id.seekBar)
    SeekBar seekBar;

    private boolean isPlaying;
    private Handler handler;
    private MediaPlayer mediaPlayer;
    private int duration;
    private int lastPosition;
    private boolean isPrepared;

    public PlaybackController(Activity activity, MediaControllerListener listener) {
        super(activity, listener);
        ButterKnife.inject(this, activity);
        handler = new Handler();
        setupPlayer();
        updateButtonState();
        updateTimeView();
        initSeekBar();
    }

    @OnClick(R.id.record_play)
    public void onPlayClicked() {
        if (!isPlaying) {
            resumePlayback();
        } else {
            pausePlayback();
        }
    }

    @OnClick(R.id.switchToRecording)
    public void onSwitchToRecordingClicked() {
        pausePlayback();
        listener.onFinished();
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            boolean wasPlaying;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isPrepared && fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateTimeView();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isPrepared) {
                    wasPlaying = isPlaying;
                    pausePlayback();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (wasPlaying) {
                    resumePlayback();
                }
            }
        });
    }

    private void updateButtonState() {
        btnRecord.setImageResource(isPlaying ? R.drawable.ic_pause_white_48dp : R.drawable.ic_play_arrow_white_48dp);
    }

    private void updateTimeView() {
        timeView.setText(Utils.formatPlayTime(getCurrentPosition()) + " / " + Utils.formatPlayTime(duration));
    }

    private void updateSeekBar() {
        seekBar.setProgress(getCurrentPosition());
    }

    private int getCurrentPosition() {
        return isPrepared ? mediaPlayer.getCurrentPosition() : 0;
    }

    private void resumePlayback() {
        isPlaying = true;
        if (isPrepared) {
            mediaPlayer.start();
        }
        startTimeUpdater();
        updateButtonState();
        keepScreenOn();
    }

    private void pausePlayback() {
        isPlaying = false;
        if (isPrepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        stopTimeUpdater();
        updateButtonState();
        keepScreenOff();
    }

    private void setupPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogHelper.i(TAG, "onError ");
                return false;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                LogHelper.i(TAG, "onPrepared");
                duration = mediaPlayer.getDuration();
                seekBar.setMax(duration);
                isPrepared = true;
                restorePlaybackPosition();
                if (isPlaying) {
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                LogHelper.i(TAG, "OnCompletionListener");
                pausePlayback();
                mediaPlayer.seekTo(0);
                updateTimeView();
                updateSeekBar();
            }
        });
        try {
            mediaPlayer.setDataSource(Config.getAudioFilePath());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        isPrepared = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            updateTimeView();
            updateSeekBar();
            handler.postDelayed(this, TIMER_DELAY);
        }
    };

    private void startTimeUpdater() {
        timeUpdater.run();
    }

    private void stopTimeUpdater() {
        handler.removeCallbacks(timeUpdater);
    }

    @Override
    public void onActivityResume() {
        setupPlayer();
    }

    @Override
    public void onActivityPause() {
        lastPosition = getCurrentPosition();
        pausePlayback();
        releasePlayer();
    }

    @Override
    public void saveState(Bundle state) {
        super.saveState(state);
        state.putInt(STATE_POSITION, lastPosition);
    }

    @Override
    public void restoreState(Bundle state) {
        super.restoreState(state);
        lastPosition = state.getInt(STATE_POSITION);
        if (isPrepared) {
            restorePlaybackPosition();
        }
    }

    private void restorePlaybackPosition() {
        mediaPlayer.seekTo(lastPosition);
        seekBar.setProgress(lastPosition);
        updateTimeView();
        updateSeekBar();
    }
}
