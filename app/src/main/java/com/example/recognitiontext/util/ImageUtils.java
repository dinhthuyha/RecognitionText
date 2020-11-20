package com.example.recognitiontext.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Random;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    public static String folderName = "DrawImage";
    private static File tempFile;
    private static String imageName;

    public  static String saveImage(Bitmap bitmap, Context context) {

        //1. create new folder to save image
        String root = Environment.getExternalStorageDirectory().toString();
        Log.d(TAG, "saveImage: " + root);

        File folder = new File(root, folderName);
        folder.mkdirs();

        //2. create empty file (.png)
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        imageName = "Image-" + n ;
       // String  = Calendar.getInstance().getTime().toString() + ".jpg";
        Log.d(TAG, "saveImage: " + imageName);
        File imageFile = new File(folder.toString(), imageName+".jpg");
       // String path = imageFile.get;
        //3. use fileOutputStream to save image
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();

            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();

            MediaScannerConnection.scanFile(
                    context,
                    new String[]{imageFile.getAbsolutePath()},
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
        }
       return imageFile.getAbsolutePath();
    }

    public static String getImage() {
        File folder= new File( Environment.getExternalStorageDirectory().toString(), folderName);
        File[] listImage= folder.listFiles();
        if(listImage!=null){
            Log.d(TAG, "getImage: "+ listImage[0].getName());
            Log.d(TAG, "getImage: "+folder.getAbsolutePath()+"/"+ listImage[0].getName());
            return listImage[0].getPath();
        }else{
            return null;
        }

    }

}
