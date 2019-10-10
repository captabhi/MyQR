package com.example.myqr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.Util;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.example.myqr.FileSaver.getOutputMediaFile;
import static com.example.myqr.FileSaver.getOutputMediaFile;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MY TAG";
    private static int RESULT_LOAD_IMAGE_GALLERY = 1;
    private static int RESULT_LOAD_IMAGE_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return;
        }
    }

    public void scan(View a)
    {
        Intent intent = new Intent(this, ContinuousCaptureActivity.class);
        startActivity(intent);
    }

    public void send(View a)
    {
        Intent i = new Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select Picture (Less than 100kb)"),RESULT_LOAD_IMAGE_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_OK) return;

        if(requestCode==RESULT_LOAD_IMAGE_CAMERA)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
        }
        if(requestCode==RESULT_LOAD_IMAGE_GALLERY)
        {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
                ByteArrayOutputStream bos=new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                if(bos.size()>FileSplitter.BUF_SIZE)
                {
                    Toast.makeText(this,"Choose Small File",Toast.LENGTH_LONG).show();
                    return;
                }
                if(bos.size()>FileSplitter.BUF_SIZE/2)
                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,bos);
                // BYTE[] of Loaded Image
                byte bytedata[]=bos.toByteArray();
                FileSplitter fs=new FileSplitter(bytedata);
                bytedata=null;
                System.out.println("Packets READY ! No. of Packets = "+fs.getPackets());

                //          SETTING VARIABLES FOR GIF ENCODING      //
                System.out.println("Setting Variables.");
                int delay=100;
                File file=getOutputMediaFile(this,0,"gifs");

                if(!file.exists())
                    file.mkdir();
                file.mkdir();
                System.out.println(file.canWrite());
                System.out.println(file.exists());
                GifEncoder gifEncoder=new GifEncoder();
                gifEncoder.init(300,300,file.getPath()+".gif", GifEncoder.EncodingType.ENCODING_TYPE_FAST);
                System.out.println("Variables Set");

                //          FOR EACH SPLITTED BYTE ARRAY CREATE AN IMAGE AND ADD TO GIF     //

                for(int i=0;i<fs.getPackets();i++)
                {
                    bytedata=fs.getIthSpliited(i);
                    // Base64 Converted String
                    // Ready to be Sent.
                    String bytestring = Base64.encodeToString(bytedata, Base64.DEFAULT);
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    MultiFormatReader multiFormatReader = new MultiFormatReader();
                    Result res = null;
                    try {

                        //         ENCODING        //

                        BitMatrix bitMatrix = multiFormatWriter.encode(bytestring, BarcodeFormat.QR_CODE, 300, 300);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap qrbitmap = barcodeEncoder.createBitmap(bitMatrix);



                        gifEncoder.encodeFrame(qrbitmap, delay);


                        //         DECODING        //
                        /*
                        int pixels[] = new int[qrbitmap.getWidth() * qrbitmap.getHeight()];
                        qrbitmap.getPixels(pixels, 0, qrbitmap.getWidth(), 0, 0, qrbitmap.getWidth(), qrbitmap.getHeight());

                        LuminanceSource ls = new RGBLuminanceSource(qrbitmap.getWidth(), qrbitmap.getHeight(), pixels);
                        BinaryBitmap qrbinary = new BinaryBitmap(new HybridBinarizer(ls));

                        res = multiFormatReader.decode(qrbinary);
                        String decodedstring = res.getText();
                        byte decodedbytes[] = Base64.decode(decodedstring, Base64.DEFAULT);*/
                    }
                    catch (WriterException e) {
                        e.printStackTrace();
                    }
                }


                //          Set Image       //

                ImageView imageView = (ImageView) findViewById(R.id.fetchedimage);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);


            } catch (IOException | FileSizeException e) {
                e.printStackTrace();
            }
        }
    }

}