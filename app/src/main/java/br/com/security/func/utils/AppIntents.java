package br.com.security.func.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppIntents {

    public static Uri startCameraCapture(Fragment fragment, int REQUEST_CODE) throws IOException {
        Uri uriImageSaved = getOutputMediaFileUri(fragment.getContext());

        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImageSaved);
        fragment.startActivityForResult(takePictureIntent, REQUEST_CODE);

        return uriImageSaved;
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(Context context) throws IOException {
        File fileImage = getOutputMediaFile();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, "br.com.security.fileprovider", fileImage);
        } else {
            return Uri.fromFile(fileImage);
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() throws IOException {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SecurityCheckin");

        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                Log.v(AppIntents.class.getCanonicalName(), "Falha ao criar diretorio SecurityCheckin");
                return null;
            }
        }

        String prefix = "IMG_";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = prefix + timeStamp;

        return File.createTempFile(fileName, ".jpg", mediaStorageDir);
    }
}
