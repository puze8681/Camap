package com.example.toshiba.myapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by TOSHIBA on 2016-08-30.
 */


public class Fragment1 extends Fragment {

    ImageView selectedImage;

    TextView lonText, latText;

    public Fragment1() {

    }

    // PlaceholderFragment.newInstance() 와 똑같이 추가
    static Fragment1 newInstance(int SectionNumber) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putInt("section_number", SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.gallery,
                container, false);

        Gallery gallery = (Gallery) rootView.findViewById(R.id.gallery);
        selectedImage = (ImageView) rootView.findViewById(R.id.imageView);

        lonText = (TextView) rootView.findViewById(R.id.longitude);
        latText = (TextView) rootView.findViewById(R.id.latitude);

        gallery.setSpacing(1);

        final GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(getContext());
        gallery.setAdapter(galleryImageAdapter);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // show the selected Image

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 4;
                Bitmap bm = BitmapFactory.decodeFile(galleryImageAdapter.pathArray.get(position), options);
                selectedImage.setImageBitmap(bm);

                latText.setText("위도 : " + galleryImageAdapter.latArray.get(position));
                lonText.setText("경도 : " + galleryImageAdapter.lonArray.get(position));


            }
        });

        return rootView;
    }

}
