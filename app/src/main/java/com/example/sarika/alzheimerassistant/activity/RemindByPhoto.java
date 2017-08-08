package com.example.sarika.alzheimerassistant.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarika.alzheimerassistant.BuildConfig;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.config.OkHttp3Stack;
import com.example.sarika.alzheimerassistant.other.ActivityResultBus;
import com.example.sarika.alzheimerassistant.other.ActivityResultEvent;
import com.example.sarika.alzheimerassistant.other.ImageManager;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpHeaders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.os.Build.VERSION_CODES.M;

public class RemindByPhoto extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.remindByPhoto)
    Button remindByPhoto;
    @BindView(R.id.reminder)
    TextView reminder;
    @BindView(R.id.remindByPhotoViewer)
    ImageView remindByPhotoViewer;

    private Bitmap bitmap;
    private ImageManager imageManager;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = RemindByPhoto.class.getSimpleName();
    private File photoFile;
    private ProgressDialog dialog;
    private String token;
    private File uploadedFile;
    private OkHttpClient client;

    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_by_photo);
        ButterKnife.bind(this);
        remindByPhoto.setOnClickListener(this);
        client = new OkHttpClient();
        imageManager = new ImageManager(this);
    }

    //Click Pics Selector
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.remindByPhoto:
                clickPhoto();
                break;
        }

    }
    //Create Image Photo Path
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }


    //click Pic
    private void clickPhoto() {
            Log.d(TAG, "In upload");

            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoURI=null;
                    if (Build.VERSION.SDK_INT > M) {
                         photoURI = FileProvider.getUriForFile(RemindByPhoto.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                Toast.makeText(getApplication(), "Camera Not supported", Toast.LENGTH_LONG).show();
            }
            setPhotoFile(photoFile);
        }


    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            Log.d(TAG, "In upload");
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "In Activity Result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            uploadClickedPic();
        }
    }

    public void uploadClickedPic() {
        bitmap = BitmapFactory.decodeFile(getPhotoFile().getAbsolutePath());
        remindByPhotoViewer.setImageBitmap(bitmap);
        if (bitmap != null) {
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setMessage("Uploading...");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
            new Thread(new Runnable() {
                public void run() {
                    upload(bitmap);

                }
            }).start();
        } else {
            Toast.makeText(getApplicationContext(), "Could not take Pic", Toast.LENGTH_LONG).show();
        }
    }


    private void upload(Bitmap bitmap) {
        setToken(PreferenceManager.getTokenFromsharedPreference(getApplicationContext()));
        postFacialImage(bitmap);
    }


    private void postFacialImage(Bitmap bitmap) {
        remindByPhoto.setEnabled(true);
        setUploadedFile(getfileFromBitmap(bitmap));
        if (getUploadedFile() == null) {
            Toast.makeText(getApplicationContext(), "Invalid file", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Util.SERVER_URL + "/imageReminder/getReminderByImage";

        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadedFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadedFile", uploadedFile.getName(), requestBodyFile)
                .build();

        Log.d(TAG, requestBody.toString());

        final Request request = new Request.Builder()
                .header(HttpHeaders.AUTHORIZATION, token)
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            reminderFetchFailed();
                        }
                    });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        String msg = null;
                        try {
                            msg = response.body().string();
                            //Get Array of Reminders
                            if(response.code()==200) {
                                JSONArray Jarray = new JSONArray(msg);
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject Jobject = Jarray.getJSONObject(i);
                                    String remMsg = Jobject.getString("message");
                                    if (remMsg.equals("") && remMsg == null) {
                                        reminder.setText("No Reminder Saved Corresponding to Image");
                                    } else {
                                        reminder.setText(remMsg);
                                    }
                                }
                            }else {
                                if(response.code()==404){
                                    reminder.setText("No Reminder Saved Corresponding to Image");
                                }else{
                                    Toast.makeText(getApplicationContext(),"Please Reupload Image",Toast.LENGTH_LONG).show();
                                }
                            }
                            Log.d(TAG,msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
        App.cancelAllRequests(TAG);
    }

    private void reminderFetchFailed(){
        Toast.makeText(getApplicationContext(),"Error in Fetching Reminders",Toast.LENGTH_LONG).show();
    }

    public File getfileFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        File file = null;
        try {
            file = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            file = writeBytesToFile(byteArray, file.getAbsolutePath());
        }
        return file;
    }

    private File writeBytesToFile(byte[] bFile, String fileDest) {
        FileOutputStream fileOuputStream = null;
        File file = null;
        try {
            file = new File(fileDest);
            fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(bFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;

    }

}
