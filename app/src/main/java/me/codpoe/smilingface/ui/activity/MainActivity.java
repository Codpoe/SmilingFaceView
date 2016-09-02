package me.codpoe.smilingface.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.codpoe.smilingface.R;
import me.codpoe.smilingface.ui.view.SmilingFaceView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.smiling_face_view_toolbar)
    Toolbar mSmilingFaceViewToolbar;
    @BindView(R.id.smiling_face_view)
    SmilingFaceView mSmilingFaceView;
    @BindView(R.id.smiling_start_btn)
    Button mSmilingStartBtn;
    @BindView(R.id.smiling_stop_btn)
    Button mSmilingStopBtn;
    @BindView(R.id.smiling_color_alpha_spinner)
    Spinner mColorAlphaSpinner;
    @BindView(R.id.smiling_alpha_tv)
    TextView mAlphaTv;
    @BindView(R.id.smiling_alpha_seekbar)
    SeekBar mAlphaSeekbar;
    @BindView(R.id.smiling_duration_tv)
    TextView mDurationTv;
    @BindView(R.id.smiling_duration_seekbar)
    SeekBar mDurationSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mColorAlphaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mSmilingFaceView.setColorAndAlpha(getResources().getColor(R.color.colorPrimary), 127);
                        break;
                    case 1:
                        mSmilingFaceView.setColorAndAlpha(getResources().getColor(R.color.colorPrimaryDark), 127);
                        break;
                    case 2:
                        mSmilingFaceView.setColorAndAlpha(getResources().getColor(R.color.colorAccent), 127);
                        break;
                    case 3:
                        mSmilingFaceView.setColorAndAlpha(Color.RED, 127);
                        break;
                    case 4:
                        mSmilingFaceView.setColorAndAlpha(Color.GREEN, 127);
                        break;
                    case 5:
                        mSmilingFaceView.setColorAndAlpha(Color.BLUE, 127);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mAlphaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAlphaTv.setText(String.format(getString(R.string.smiling_alpha), progress));
                mSmilingFaceView.setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDurationTv.setText(String.format(getString(R.string.smiling_duration), progress));
                mSmilingFaceView.setDuration(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({
            R.id.smiling_start_btn,
            R.id.smiling_stop_btn,
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.smiling_start_btn:
                mSmilingFaceView.start();
                break;
            case R.id.smiling_stop_btn:
                mSmilingFaceView.stop();
                break;
        }
    }

}
