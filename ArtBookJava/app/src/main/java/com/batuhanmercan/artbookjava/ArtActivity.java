package com.batuhanmercan.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.batuhanmercan.artbookjava.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;

public class ArtActivity extends AppCompatActivity {
    public ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
    }

    public void save(View view) {
        String name = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String year = binding.yearText.getText().toString();

         database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
         database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");
         String sqlString  = "INSERT INTO arts (artname, paintername, year, image) VALUES(?, ?, ?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,name);
        sqLiteStatement.bindString(1,artistName);
        sqLiteStatement.bindString(1,year);




    }




    public void selectImage (View view){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
              if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                  Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                      }
                  }).show();

              }   else {
                  permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
              }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

            }


    private void registerLauncher (){
          activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                      @Override
                      public void onActivityResult(ActivityResult result) {
                         if (result.getResultCode() == RESULT_OK){
                             Intent IntentFromResult    =  result.getData();
                             if (IntentFromResult != null){
                                   Uri imageData =  IntentFromResult.getData();
                                   //binding.imageView.setImageURI(imageData);

                                 try {
                                     if (Build.VERSION.SDK_INT >= 28){
                                         ImageDecoder.Source soruce = ImageDecoder.createSource(ArtActivity.this.getContentResolver(),imageData);
                                         selectedImage = ImageDecoder.decodeBitmap(soruce);
                                         binding.imageView.setImageBitmap(selectedImage);
                                     }else {
                                         selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                                         binding.imageView.setImageBitmap(selectedImage);

                                     }

                                 } catch (Exception e){
                                      e.printStackTrace();
                                 }
                             }
                         }
                      }
                  });
                  permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                      @Override
                      public void onActivityResult(Boolean result) {
                          if (result) {
                              Intent IntentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                              activityResultLauncher.launch(IntentToGallery);

                          } else {
                              Toast.makeText(ArtActivity.this, "Permission needed!", Toast.LENGTH_LONG).show();
                          }
                      }
                  });
    }
}