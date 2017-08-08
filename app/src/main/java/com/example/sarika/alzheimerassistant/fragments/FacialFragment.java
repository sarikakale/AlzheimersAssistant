package com.example.sarika.alzheimerassistant.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sarika.alzheimerassistant.BuildConfig;
import com.example.sarika.alzheimerassistant.R;
import com.example.sarika.alzheimerassistant.activity.CaregiverDetailsActivity;
import com.example.sarika.alzheimerassistant.activity.CaregiverHomeActivity;
import com.example.sarika.alzheimerassistant.activity.FirstActivity;
import com.example.sarika.alzheimerassistant.activity.MainActivity;
import com.example.sarika.alzheimerassistant.base.App;
import com.example.sarika.alzheimerassistant.bean.Caregiver;
import com.example.sarika.alzheimerassistant.bean.Details;
import com.example.sarika.alzheimerassistant.bean.FacialLibrary;
import com.example.sarika.alzheimerassistant.bean.Login;
import com.example.sarika.alzheimerassistant.bean.Person;
import com.example.sarika.alzheimerassistant.bean.PostResponse;
import com.example.sarika.alzheimerassistant.network.CaregiverApiRequests;
import com.example.sarika.alzheimerassistant.network.GsonPostRequest;
import com.example.sarika.alzheimerassistant.network.LoginApiRequests;
import com.example.sarika.alzheimerassistant.other.ActivityResultBus;
import com.example.sarika.alzheimerassistant.other.ActivityResultEvent;
import com.example.sarika.alzheimerassistant.other.PreferenceManager;
import com.example.sarika.alzheimerassistant.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * {@link FacialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FacialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FacialFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CaregiverDetailsActivity.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String facialId;
    private Bitmap bitmap;
    private Details details;
    private Login login;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_ALBUM = 2;

    private Uri uri;
    private File photoFile;
    private File scaledImage;

    //UserId

    private ProgressDialog dialog ;

    private Person caregiver;


    String token;

    @BindView(R.id.Imageprev)
    ImageView profilePhotoView;

    @BindView(R.id.takePhoto)
    Button takePhoto;

    @BindView(R.id.uploadPhoto)
    Button uploadPhoto;

    @BindView(R.id.registrationDoneButton)
    Button doneRegistration;

    Gson gson = new Gson();
    private Caregiver caregiver1;
    private String userId;
    private final OkHttpClient client;
    private OnFragmentInteractionListener mListener;

    public FacialFragment() {
        // Required empty public constructor
        client = new OkHttpClient();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FacialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FacialFragment newInstance(String param1, String param2) {
        FacialFragment fragment = new FacialFragment();
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
        View view = inflater.inflate(R.layout.fragment_facial, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        String caregiverString = (String) bundle.getSerializable("Person");

        if(PreferenceManager.getRoleFromsharedPreference(getActivity().getApplicationContext()).equals(Util.CAREGIVER)) {
            caregiver = gson.fromJson(caregiverString, Caregiver.class);
        }
        caregiver1 = (Caregiver) caregiver;
        takePhoto.setOnClickListener(this);
        uploadPhoto.setOnClickListener(this);
        doneRegistration.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.takePhoto:
                clickPic();
                break;
            case R.id.uploadPhoto:
                openAlbum();
                break;
            case R.id.registrationDoneButton:
                Intent intent=new Intent(getActivity(),CaregiverHomeActivity.class);
                PreferenceManager.setFirstTimeLogin(getActivity().getApplicationContext(),false);
                mListener.onNextSelected(intent);
            default:
                break;

        }

    }


    //Sending Multiple Requests to the Server
    private void upload(final Bitmap bmp, final Details details, final Login login) {
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage("Uploading...");
        token = PreferenceManager.getTokenFromsharedPreference(getActivity().getApplicationContext());
        userId = PreferenceManager.getUserId(getActivity().getApplicationContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
        new Thread(new Runnable() {
            public void run() {
                postContactDetails(details,bmp);
            }
        }).start();
    }

    private void postContactDetails(Details details, Bitmap bmp) {
        GsonPostRequest gsonPostRequest = CaregiverApiRequests.postContactDetailsRequest(
                new Response.Listener<Details>() {
                    @Override
                    public void onResponse(Details response) {

                        Log.i(TAG,response.toString());
                        caregiver1.getDetails().setDetailId(response.getDetailId());
                        caregiver1.setUserId(userId);
                        postCaregiverDetails(bmp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Message msg = handler.obtainMessage();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }
                },
                details,
                token

        );
        App.addRequest(gsonPostRequest,TAG);

    }

     public void postFacialImage(Bitmap bitmap){

        File uploadedFile = null;
        uploadedFile = getfileFromBitmap(bitmap);

        if(uploadedFile == null){
            Toast.makeText(getActivity().getApplicationContext(),"Invalid file",Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,String> params= new HashMap<>();
        String imageDesc = caregiver.getDetails().getName().getFirstName() +
                " " +
                caregiver.getDetails().getName().getLastName() +
                " " +
                caregiver.getDetails().getEmail();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("imageDescription",imageDesc + " " + "Face Image");
        jsonObject.addProperty("imageName",userId);
        String url =  Util.SERVER_URL+"/facialLibrary/add";

        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), uploadedFile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imageDescription",imageDesc + " " + "Face Image")
                .addFormDataPart("imageName",userId)
                .addFormDataPart("uploadedFile",uploadedFile.getName(),requestBodyFile )
                .build();

        Log.d(TAG,requestBody.toString());

        Request request = new Request.Builder()
                .header(HttpHeaders.AUTHORIZATION,token)
                .url(Util.SERVER_URL+"/facialLibrary/add")
                .post(requestBody)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        uploadPhoto.setEnabled(true);
                        dialog.dismiss();
                       // Toast.makeText(getActivity().getApplicationContext(),"Could not Upload the file",Toast.LENGTH_LONG).show();
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    }
                });

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                Log.d(TAG,response.message() + response.request());
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        uploadPhoto.setEnabled(true);
                        dialog.dismiss();
                        String facialBody=null;
                        try {
                             facialBody =  response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                      //  FacialLibrary facialLibrary = gson.fromJson(facialBody,FacialLibrary.class);
                        //Log.d(TAG,facialLibrary.toString());
                        //caregiver1.setFacialId(facialLibrary.getFacialId());
                      //  Toast.makeText(getActivity().getApplicationContext(),"File Successfully Uploaded",Toast.LENGTH_LONG).show();
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }
                });
            }
        });

    }

    private void postCaregiverDetails(Bitmap bitmap) {

         GsonPostRequest gsonPostRequest = CaregiverApiRequests.postCaregiverRegisterRequest(

                 new Response.Listener<PostResponse>() {
                     @Override
                     public void onResponse(PostResponse response) {
                         Log.d(TAG,response.getSuccess()+ " "+response.getBody());
                         postFacialImage(bitmap);

                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Log.d(TAG,error.getMessage());
                         Message msg = handler.obtainMessage();
                         msg.arg1 = 2;
                         handler.sendMessage(msg);
                     }
                 },
                 caregiver1,
                 token
         );

         App.addRequest(gsonPostRequest,TAG);
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getActivity().getApplicationContext(),"Caregiver Details Saved Successfully", Toast.LENGTH_LONG).show();
            if(msg.arg1 == 2)
                Toast.makeText(getActivity().getApplicationContext(),"Could not Save Caregiver Details", Toast.LENGTH_LONG).show();
            }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNextSelected(Intent mainActivityIntent);
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

    public void uploadClickedPic(){
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        //Bitmap photo = (Bitmap) data.getExtras().get("data");
        profilePhotoView.setImageBitmap(bitmap);
        if(bitmap!=null) {
            upload(bitmap,caregiver.getDetails(),caregiver.getLogin());
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"Could not take Pic",Toast.LENGTH_LONG).show();
        }
    }



    private void openAlbum() {

        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_ALBUM);
        }

    }

    private void uploadPhoto(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profilePhotoView.setImageBitmap(bm);
        upload(bm,caregiver.getDetails(),caregiver.getLogin());
    }

    @Override
    public void onStart() {
        super.onStart();
        ActivityResultBus.getInstance().register(mActivityResultSubscriber);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(dialog!=null){
            dialog.dismiss();
        }
        ActivityResultBus.getInstance().unregister(mActivityResultSubscriber);
        App.cancelAllRequests(TAG);
    }

    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            uploadClickedPic();
        } else {
            if (requestCode == REQUEST_IMAGE_ALBUM && resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                Log.d(TAG,uri.getPath());
                if (uri != null) {
                    uploadPhoto(data);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid URI", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getData(Caregiver caregiver) {
        Log.i(TAG, caregiver.toString());
        caregiver.getFacialId().setFacialId(facialId);

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
        if(photoFile!=null) {
            file = writeBytesToFile(byteArray, file.getAbsolutePath());
        }else{
            if(uri!=null){

                file = writeBytesToFile(byteArray, file.getAbsolutePath());
            }
        }
        return file;
    }

        private  File writeBytesToFile(byte[] bFile, String fileDest) {
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
