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

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import pro.rasht.museum.ar.R;


public class FragmentMusic extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private QRCodeReaderView qrCodeReaderView;
    private CheckBox flashlightCheckbox;
    private CheckBox enableDecodingCheckBox;
    private boolean isRunning= false;
    private TextView resultTextView;

    private FragmentActivity contInst;
    private View view;
    private LinearLayoutManager mLayoutManager;

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
        contInst = getActivity();
        //java code




        flashlightCheckbox = (CheckBox)view.findViewById(R.id.flashlight_checkbox);
        enableDecodingCheckBox = (CheckBox) view.findViewById(R.id.enable_decoding_checkbox);
        qrCodeReaderView = (QRCodeReaderView)view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(FragmentMusic.this);
        resultTextView = (TextView)view.findViewById(R.id.result_text_view);

        qrCodeReaderView.setQRDecodingEnabled(true);

        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setFrontCamera();
        qrCodeReaderView.setBackCamera();

        flashlightCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                qrCodeReaderView.setTorchEnabled(b);

            }
        });


        enableDecodingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                qrCodeReaderView.setQRDecodingEnabled(isChecked);
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
    public void onQRCodeRead(String text, PointF[] points  )
    {
        if(isRunning)
            return;

        isRunning = true;

        //Your code

        resultTextView.setText(text+"");

        //your code


    }


}
