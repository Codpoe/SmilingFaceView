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
    @BindView(R.id.smiling_color_spinner)
    Spinner mColorSpinner;
    @BindView(R.id.duration_tv)
    TextView mDurationTv;
    @BindView(R.id.smiling_set_duration_seekbar)
    SeekBar mDurationSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mSmilingFaceView.setColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 1:
                        mSmilingFaceView.setColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;
                    case 2:
                        mSmilingFaceView.setColor(getResources().getColor(R.color.colorAccent));
                        break;
                    case 3:
                        mSmilingFaceView.setColor(Color.RED);
                        break;
                    case 4:
                        mSmilingFaceView.setColor(Color.GREEN);
                        break;
                    case 5:
                        mSmilingFaceView.setColor(Color.BLUE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDurationTv.setText((String.valueOf(progress)));
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
