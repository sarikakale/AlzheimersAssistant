package com.example.sarika.alzheimerassistant.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.BuildConfig;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.RemindByPhoto;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.FacialLibraryApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.other.ActivityResultBus;
import com.example.sarika.alzheimerassistant.other.ActivityResultEvent;
import com.example.sarika.alzheimerassistant.other.ImageManager;
import com.example.sarika.alzheimerassistant.other.LocationTracker;
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

import static android.os.Build.VERSION_CODES.M;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveByPhotoFragment.OnPhotoUpload} interface
 * to handle interaction events.
 * Use the {@link SaveByPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveByPhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile;
    private String token;
    private ProgressDialog dialog;
    private OkHttpClient client;
    private static final String TAG = SaveByPhotoFragment.class.getSimpleName();
    private Bitmap bitmap;
    private Location location;
    private OnPhotoUpload mListener;
    private Geocoder geocoder;
    private String locationDescription;
    private LocationTracker mLocationTracker;
    private File uploadedFile;
    private ImageManager imageManager;

    @BindView(R.id.saveAndUploadPhotoFriend)
    Button saveAndUploadFriendPhoto;

    @BindView(R.id.friendName)
    EditText friendName;

    @BindView(R.id.reminderMessage)
    EditText reminderMessage;

    @BindView(R.id.saveFriendPhotoImageView)
    ImageView savePCamIcon;
    String patientId;

    public SaveByPhotoFragment() {
        client = new OkHttpClient();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SaveByPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveByPhotoFragment newInstance(String param1, String param2) {
        SaveByPhotoFragment fragment = new SaveByPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_save_by_photo, container, false);
        ButterKnife.bind(this, v);
        saveAndUploadFriendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickPic();

            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoUpload) {
            mListener = (OnPhotoUpload) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /*private void clickPic() {
        Log.d(TAG, "In upload");

        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }
        } else {
            Toast.makeText(getActivity().getApplication(), "Camera Not supported", Toast.LENGTH_LONG).show();
        }
    }*/
    private void clickPic() {
        Log.d(TAG, "In upload");

        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI=null;
                if (Build.VERSION.SDK_INT > M) {
                    photoURI = FileProvider.getUriForFile(getActivity(),
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
            Toast.makeText(getActivity().getApplication(), "Camera Not supported", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadClickedPic() {
        bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        savePCamIcon.setImageBitmap(bitmap);
        if (bitmap != null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setIndeterminate(true);
            dialog.setMessage("Uploading...");
            getActivity().runOnUiThread(new Runnable() {
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
            Toast.makeText(getActivity().getApplicationContext(), "Could not take Pic", Toast.LENGTH_LONG).show();
        }
    }

    //Upload Image to the server
    private void upload(Bitmap bitmap) {
        token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        patientId = getActivity().getIntent().getStringExtra(getResources().getString(R.string.patient));
        postFacialImage(bitmap);
    }

    private void setLocationDescription() {
        this.locationDescription = mListener.getLocation();
        Log.d(TAG, locationDescription);
    }

    public void postFacialImage(Bitmap bitmap) {
        savePCamIcon.setEnabled(false);
        uploadedFile = getfileFromBitmap(bitmap);
        if (uploadedFile == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Invalid file", Toast.LENGTH_LONG).show();
            return;
        }

        final String reminderMsgString;


        if (locationDescription != null) {

            reminderMsgString = reminderMessage.getText().toString() + ". Met you on " + new Date() + " at " + locationDescription;

        } else {

            reminderMsgString = reminderMessage.getText().toString() + ". Met you on " + new Date();

        }

        Log.d(TAG, reminderMsgString);

        final String name = friendName.getText().toString();

        String url = Util.SERVER_URL + "/imageReminder/checkIfImageUploaded";
        if(patientId==null){
            patientId="";
        }
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadedFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("patientId",patientId)
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
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        savePCamIcon.setEnabled(true);
                        dialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Could not Upload the file", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                final String jsonString = response.body().string();
                Log.d(TAG, jsonString);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            savePCamIcon.setEnabled(true);
                            dialog.dismiss();
                            try {


                            //Response Handling
                            if (response.code() == 200) {
                                JSONArray jsonArray=new JSONArray(jsonString);
                                JSONObject jsonObject = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject     = jsonArray.getJSONObject(i);
                                }
                                String caregiverId = jsonObject.getString("userId");
                                Log.d(TAG,caregiverId);
                                Log.d(TAG,"AddOnlyReminder");
                                addOnlyReminder(caregiverId,name, reminderMsgString);
                                Toast.makeText(getActivity().getApplicationContext(), "File Successfully Uploaded", Toast.LENGTH_LONG).show();

                            } else {

                                if (response.code() == 404) {
                                    String faceMatchesString = null;

                                    try {

                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        faceMatchesString = jsonObject.getString("FaceMatches");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if(faceMatchesString.equals("Face has not been uploaded previously.")){
                                        Log.d(TAG,"AddImageAndReminder");
                                        addImageandReminder(name, reminderMsgString, uploadedFile);
                                        Toast.makeText(getActivity().getApplicationContext(), "File Successfully Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getActivity().getApplicationContext(), "Could not Upload the File: Please Upload Again", Toast.LENGTH_LONG).show();
                                }
                            }//End Response Handling
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }//End Run
                    });

            }
        });

    }


    public void addImageandReminder(String name, String message, File uploadedFile) {

        String url = Util.SERVER_URL + "/imageReminder/addImageAndReminder";

        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadedFile);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("patientId",patientId)
                .addFormDataPart("name", name)
                .addFormDataPart("message", message)
                .addFormDataPart("uploadedFile", uploadedFile.getName(), requestBodyFile)
                .build();

        Log.d(TAG, requestBody.toString());

        Request request = new Request.Builder()
                .header(HttpHeaders.AUTHORIZATION, token)
                .url(url)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,e.getLocalizedMessage());
                Message msg = handler.obtainMessage();
                msg.arg1 = 2;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                Log.d(TAG, response.message() + response.request());
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        });

    }

    public void addOnlyReminder(String caregiverId, String name, String message) {
        GsonPostRequest gsonPostRequest = FacialLibraryApiRequests.postReminderMessage(
                new Response.Listener<PostResponse>() {
                    @Override
                    public void onResponse(PostResponse response) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 3;
                        handler.sendMessage(msg);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 4;
                        handler.sendMessage(msg);
                    }
                },
                caregiverId,
                name,
                message,
                patientId,
                token
        );
        App.addRequest(gsonPostRequest, TAG);
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getActivity().getApplicationContext(),"File and Reminder uploaded Successfully", Toast.LENGTH_LONG).show();
            if(msg.arg1 == 2)
                Toast.makeText(getActivity().getApplicationContext(),"Could not upload File and Reminder", Toast.LENGTH_LONG).show();
            if(msg.arg1 == 3)
                Toast.makeText(getActivity().getApplicationContext(),"Reminder Saved Successfully", Toast.LENGTH_LONG).show();
            if(msg.arg1 == 4)
                Toast.makeText(getActivity().getApplicationContext(),"Error in Saving Reminder", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
        App.cancelAllRequests(TAG);
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPhotoUpload {
        // TODO: Update argument type and name
        String getLocation();
    }
}
