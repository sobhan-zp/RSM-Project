package pro.rasht.museum.ar.Fragment;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import pro.rasht.museum.ar.R;


public class FragmentMusic extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckbox;

    private FragmentActivity Context;
    private View view;

    public static FragmentMusic newInstance() {

        Bundle args = new Bundle();
        FragmentMusic fragment = new FragmentMusic();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music, container, false);
        Context = getActivity();
        //java code

        flashlightCheckbox = (CheckBox)view.findViewById(R.id.flashlight_checkbox);


        qrCodeReaderView = (QRCodeReaderView) view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(5000L);
        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);
        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();
        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();


        flashlightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                qrCodeReaderView.setTorchEnabled(b);

            }
        });



        ///java code
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Toast.makeText(Context, ""+text, Toast.LENGTH_SHORT).show();
    }
}
