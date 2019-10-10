package com.example.myqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ContinuousCaptureActivity extends AppCompatActivity
{
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private BarcodeEncoder barcodeEncoder;
    private String resstring="";
    Set<String> textset;
    public ImageView resultimg;
    private BarcodeCallback callback=new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                return; // Prevent duplicate scans
            }

            lastText = result.getText();
            try{
                byte decodedbytes[] = Base64.decode(lastText, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedbytes , 0, decodedbytes.length);
                ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                //System.out.println("Done No exception\n");

            }catch(Exception ex)
            {
                System.out.println(ex);
                System.out.println("Exception\n");

            }

            /*if(!textset.contains(lastText)) {
                textset.add(lastText);
                resstring = resstring + lastText;
            }
            */
            barcodeView.setStatusText(lastText);
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            //ImageView finalView = (ImageView) findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_continuous_capture);
        //Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();

        resultimg=(ImageView)findViewById(R.id.resultimage);
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void showresult(View a)
    {
        byte resbytes[]=resstring.getBytes();
        Bitmap bmp=BitmapFactory.decodeByteArray(resbytes,0,resbytes.length);
        resultimg.setImageBitmap(bmp);
    }
}
