package com.sssprog.recorder.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sssprog.recorder.App;
import com.sssprog.recorder.Config;
import com.sssprog.recorder.R;
import com.sssprog.recorder.utils.LogHelper;
import com.sssprog.recorder.utils.RateTimeService;
import com.sssprog.recorder.utils.Utils;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class RecordingController extends MediaController {

    private static final String TAG = LogHelper.getTag(RecordingController.class);
    private static final long TIMER_DELAY = 50;

    @InjectView(R.id.time)
    TextView timeView;
    @InjectView(R.id.record_play)
    ImageButton btnRecord;

    private boolean isRecording;
    private Handler handler;
    private MediaRecorder recorder;
    private long startTime;

    public RecordingController(Activity activity, MediaControllerListener listener) {
        super(activity, listener);
        ButterKnife.inject(this, activity);
        handler = new Handler();
        updateButtonState();
        updateTimeView();
    }

    @OnClick(R.id.record_play)
    public void onRecordClicked() {
        if (!isRecording) {
            vibrate();
            startRecording();
        } else {
            stopRecordingAndSwitchToPlayback();
        }
    }

    @OnLongClick(R.id.record_play)
    public boolean onPlayLongClick() {
        if (isRecording) {
            stopRecording();
        }
        startRecording();
        return true;
    }

    private void updateButtonState() {
        btnRecord.setImageResource(isRecording ? R.drawable.ic_stop_white_48dp : R.drawable.ic_mic_white_48dp);
    }

    public void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(Config.getAudioFilePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            LogHelper.e(TAG, "prepare() failed");
            LogHelper.printStackTrace(e);
        }

        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            timeUpdater.run();
            keepScreenOn();
            isRecording = true;
            updateButtonState();
        } catch (Exception e) {
            LogHelper.e(TAG, "start() failed");
            LogHelper.printStackTrace(e);
            Toast.makeText(activity, R.string.recording_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) App.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(10);
        }
    }

    private void stopRecording() {
        isRecording = false;
        updateButtonState();
        handler.removeCallbacks(timeUpdater);

        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
        keepScreenOff();
        RateTimeService.incrementUsageCounter();
    }

    private void stopRecordingAndSwitchToPlayback() {
        stopRecording();
        listener.onFinished();
    }

    private void updateTimeView() {
        long total = isRecording ? System.currentTimeMillis() - startTime : 0;
        timeView.setText(Utils.formatPlayTime(total));
    }

    private Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            updateTimeView();
            handler.postDelayed(this, TIMER_DELAY);
        }
    };

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }
}
