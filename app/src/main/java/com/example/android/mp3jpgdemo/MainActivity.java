package com.example.android.mp3jpgdemo;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.android.mp3jpgdemo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//TODO (1) Enable databinding
//TODO (2) Enable firebase database and storage
//TODO (3) Set rules in firebase to true, or add authorization

public class MainActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private StorageReference mTempStorageRef;
    private final String TAG = MainActivity.class.getSimpleName();
    public static FirebaseDatabase database;
    public static DatabaseReference mImagesRef;

    ChildEventListener mImagesListener;
    private ArrayList<String> mImagesList;
    private HashMap<String, String> mImageByCoordinates;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mImagesList = new ArrayList<>();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("route1");
        database = FirebaseDatabase.getInstance();

        mImagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (("image/png").equals((String) dataSnapshot.child("contentType").getValue()) ||
                        ("image/png").equals((String) dataSnapshot.child("contentType").getValue())) {
                    mImagesList.add(dataSnapshot.getKey());
                    addImage(dataSnapshot.getKey());
                } else {
                    Log.d(TAG, "contentType = " +
                            dataSnapshot.child("contentType").getValue());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mImagesList.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //TODO change to getReference("images") and corresponding folder names.
        mImagesRef = database.getReference("route1");
        mImagesRef.addChildEventListener(mImagesListener);

    }

    private void addImage(String key) {
        try {
            Log.d(TAG, "key = " + key);
            Log.d(TAG, "readable key = " + readableKey(key));
            final File localFile = File.createTempFile("images", "png");
            mTempStorageRef = mStorageRef.child(readableKey(key));
            mTempStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    binding.bcmControls.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, ""+e);
                    Log.d(TAG, "onFailure");
                }
            });
        }
        catch (IOException e) {}
    }

    private String readableKey(String key) {
        return key.replace("*", ".");
    }
}
