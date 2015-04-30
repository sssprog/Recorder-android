package com.sssprog.recorder.ui;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewStub;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sssprog.commons.helpers.PackageManagerUtils;
import com.sssprog.recorder.Config;
import com.sssprog.recorder.R;
import com.sssprog.recorder.utils.RateTimeService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends ActionBarActivity implements MediaControllerListener {

    private static final String STATE_IS_RECORDING = "is_recording";

    private boolean isRecording;

    @InjectView(R.id.switchToRecording)
    View switchToRecording;
    @InjectView(R.id.seekBar)
    View seekBar;
    @InjectView(R.id.rate)
    View rateView;
    @InjectView(R.id.rateContainer)
    View rateContainer;
    @InjectView(R.id.bannerViewStub)
    ViewStub bannerViewStub;

    private MediaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initRateView();

        isRecording = savedInstanceState != null ? savedInstanceState.getBoolean(STATE_IS_RECORDING) : true;
        updateController();
        if (savedInstanceState != null) {
            controller.restoreState(savedInstanceState);
        }
        initAds();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_RECORDING, isRecording);
        controller.saveState(outState);
    }

    private void initRateView() {
        rateContainer.setVisibility(RateTimeService.canShowSuggestion() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.rate)
    public void onRateClicked() {
        hideRateBar();
        RateTimeService.doNotShowAnymore();
        startActivity(PackageManagerUtils.getMarketIntent(getPackageName()));
    }

    @OnClick(R.id.dismissRate)
    public void onDismissRateClicked() {
        hideRateBar();
        RateTimeService.doNotShowAnymore();
    }

    private void hideRateBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rateContainer.animate()
                        .translationY(-rateContainer.getHeight())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                rateContainer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .setDuration(300)
                        .start();
            }
        }, 300);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onFinished() {
        isRecording = !isRecording;
        updateController();
    }

    @Override
    public void onRecordingInitiated() {
        isRecording = !isRecording;
        updateController();
        RecordingController recordingController = (RecordingController) controller;
        recordingController.startRecording();
    }

    private void updateController() {
        switchToRecording.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        seekBar.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        if (isRecording) {
            controller = new RecordingController(this, this);
        } else {
            controller = new PlaybackController(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onActivityPause();
    }

    private void initAds() {
        if (Config.showAds()) {
            final View root = bannerViewStub.inflate();
            AdView adView = (AdView) root.findViewById(R.id.banner);
            AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
            Config.addTestDevices(adRequestBuilder);

            try {
                adView.loadAd(adRequestBuilder.build());
            } catch (Throwable e) {
                root.setVisibility(View.GONE);
                return;
            }
        }
    }
}
