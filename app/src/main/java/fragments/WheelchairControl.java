package fragments;


import android.app.Fragment;
import android.media.Image;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.kesar.swayam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WheelchairControl extends Fragment {

    LinearLayout linearLayout;
    private ImageButton up, down, forward, backward, stop, power_on, power_off;
    private Button power_on_again, power_off_again;

    private View mMainView;

    public WheelchairControl() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_wheelchair_control, container, false);

        up = (ImageButton) mMainView.findViewById(R.id.main_up);
        down = (ImageButton) mMainView.findViewById(R.id.main_downward);
        forward = (ImageButton) mMainView.findViewById(R.id.main_forward);
        backward = (ImageButton) mMainView.findViewById(R.id.main_backward);
        stop = (ImageButton) mMainView.findViewById(R.id.main_stop);
        power_on = (ImageButton) mMainView.findViewById(R.id.main_power_on);
        power_off = (ImageButton) mMainView.findViewById(R.id.main_power_off);

        power_on_again = (Button) mMainView.findViewById(R.id.main_power_on_again);
        power_off_again = (Button) mMainView.findViewById(R.id.main_power_off_again);

        linearLayout = (LinearLayout) mMainView.findViewById(R.id.main_layout);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Wheelchair will move in Upward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Wheelchair will move in Downward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Wheelchair will move in Forward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Wheelchair will move in Backward Direction", Snackbar.LENGTH_LONG).show();

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Wheelchair will Stop moving", Snackbar.LENGTH_LONG).show();

            }
        });

        power_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is ON", Snackbar.LENGTH_LONG).show();

            }
        });

        power_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is OFF", Snackbar.LENGTH_LONG).show();

            }
        });

        power_on_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is ON", Snackbar.LENGTH_LONG).show();

            }
        });

        power_off_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(linearLayout, "Power is OFF", Snackbar.LENGTH_LONG).show();

            }
        });

        return mMainView;
    }

}
