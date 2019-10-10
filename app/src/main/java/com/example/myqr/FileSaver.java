package com.example.myqr;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSaver {

    public static final File getOutputMediaFile(Context c,int i,String path){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/QRProject/"
                + c.getApplicationContext().getPackageName()
                + "/"+path);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+timeStamp+"count "+i;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

}
