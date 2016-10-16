package com.example.toshiba.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TOSHIBA on 2016-08-30.
 */
public class Fragment2 extends Fragment {

    public ImageView btnCameraIntent;
    public ImageView ivResult;
    public TextView txtLat;
    public TextView txtLon;

    private SharedPreferences pref;

    private GpsInfo gps;

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    public static Fragment2 newInstance(int SectionNumber) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.camera, container, false);

        txtLat = (TextView) rootView.findViewById(R.id.Latitude);
        txtLon = (TextView) rootView.findViewById(R.id.Longitude);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        loadPathArray();
        loadLocationArray();

        btnCameraIntent = (ImageView) rootView.findViewById(R.id.btnCameraIntent);
        ivResult = (ImageView) rootView.findViewById(R.id.ivResult);

        btnCameraIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                // 카메라 찍기 액션 후, 지정된 파일을 비트맵으로 꺼내 이미지뷰에 삽입
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

                options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 8;
                Bitmap smallBm = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                saveBitmap(smallBm, mCurrentPhotoPath + "_small");


                ivResult.setImageBitmap(bm);

                gps = new GpsInfo(getContext());
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    txtLat.setText("위도 " + latitude);
                    txtLon.setText("경도 " + longitude);

                    latArray.add(latitude);
                    lonArray.add(longitude);

                    saveLocationArray();

                }

                pathArray.add(mCurrentPhotoPath);
                savePathArray();

            }
        }
    }


    private void saveBitmap(Bitmap bitmap, String strFilePath){

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    ArrayList<String> pathArray;
    ArrayList<Double> latArray;
    ArrayList<Double> lonArray;


    private void loadPathArray() {
        Type t = new TypeToken<ArrayList<String>>() {}.getType();
        pathArray = new Gson().fromJson(pref.getString("pathArray", "[]"), t);

    }

    private void loadLocationArray() {
        Type t = new TypeToken<ArrayList<Double>>() {}.getType();
        latArray = new Gson().fromJson(pref.getString("latArray", "[]"), t);
        lonArray = new Gson().fromJson(pref.getString("lonArray", "[]"), t);
    }

    private void savePathArray() {
        pref.edit().putString("pathArray", new Gson().toJson(pathArray)).apply();
    }

    private void saveLocationArray(){
        pref.edit()
                .putString("latArray", new Gson().toJson(latArray))
                .putString("lonArray", new Gson().toJson(lonArray))
                .apply();
    }


    String mCurrentPhotoPath;
    Uri contentUri;


    public static final int REQUEST_CAMERA = 100;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath(); //나중에 Rotate하기 위한 파일 경로.
        new File(mCurrentPhotoPath + "_small").createNewFile();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a player activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                contentUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

}