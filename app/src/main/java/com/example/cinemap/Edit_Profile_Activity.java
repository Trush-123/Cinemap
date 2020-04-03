package com.example.cinemap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Edit_Profile_Activity extends AppCompatActivity implements View.OnClickListener
{

    EditText Username;
    EditText Description;
    Button Back;
    Button Update;
    Button SetImage;
    ImageView ProfImage;
    Uri ImageUri;
    StorageReference DatabaseStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile);
        //variable declaration
        Username=findViewById(R.id.profile_edit_username);
        Description=findViewById(R.id.profile_edit_description);
        Back=findViewById(R.id.profile_edit_back);
        Update=findViewById(R.id.profile_edit_update);
        SetImage=findViewById(R.id.profile_edit_setIamge);
        ProfImage=findViewById(R.id.profile_edit_profImage);
        Back.setOnClickListener(this);
        Update.setOnClickListener(this);
        SetImage.setOnClickListener(this);
        //gets the firebase storage reference(used in set image method)
        DatabaseStorageRef= FirebaseStorage.getInstance().getReference();
        //gets the users profile details and displays them
        FirebaseFirestore Database = FirebaseFirestore.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference UserDocRef=Database.collection("Users").document(user.getUid());
        UserDocRef.get().addOnSuccessListener(documentSnapshot ->
        {
            Username.setText(documentSnapshot.getString("UserName"));
            Description.setText(documentSnapshot.getString("User_Description"));
            Glide.with(this).load(documentSnapshot.getString("Profile_Image")).into(ProfImage);
        });
        //requests necessary permissions(for using the camera in set image method )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(Edit_Profile_Activity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    //universal click handler
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profile_edit_back: //if Back Button is pressed goes back to the users profile
                finish();
                break;
            case R.id.profile_edit_update: //if Update Button is pressed updates the users profile with the new details
                update();
                break;
            case R.id.profile_edit_setIamge: // if SetImage Button is pressed starts the set image method
                setimage();
                break;
        }
    }

    //opens an alert dialog that lets the user choose how they want to set their profile image
    private void setimage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Profile Image");
        builder.setMessage("Set Your Profile Image With Your Camera Or From Your Gallery");
        builder.setPositiveButton("Camera", (dialogInterface, i) ->
        {
            TakePicture();
        });
        builder.setNeutralButton("Gallery", (dialogInterface, i) ->
        {
            ChooseFiles();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) ->
        {
            dialogInterface.dismiss();
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //after selecting the image or taking a photo displays it in the image view
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1: //case image from gallery
                if (resultCode == RESULT_OK&data!=null&&data.getData()!=null)
                {
                    ImageUri=data.getData();
                    Glide.with(this).load(ImageUri).into(ProfImage);
                }
                break;
            case 2: //case image from camera
                if (resultCode == RESULT_OK)
                {
                    File photo=new File(Environment.getExternalStorageDirectory(),"photo.jpg");
                    ImageUri=FileProvider.getUriForFile(this,this.getApplicationContext().getPackageName()+".provider",photo);
                    Glide.with(this).load(ImageUri).into(ProfImage);
                }
                break;
        }
    }

    //uploads the image to firebase storage
    private void UploadImage()
    {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(ImageUri!=null)
        {
            StorageReference ChildRef = DatabaseStorageRef.child(System.currentTimeMillis() + "." + FileExtension(ImageUri));
            ChildRef.putFile(ImageUri).addOnSuccessListener(taskSnapshot ->
            {
                ChildRef.getDownloadUrl().addOnSuccessListener(uri ->
                {
                    FirebaseFirestore Database = FirebaseFirestore.getInstance();
                    Map<String,Object> UserData=new HashMap<>();
                    UserData.put("Profile_Image",uri.toString());
                    DocumentReference UserRef=Database.collection("Users").document(user.getUid());
                    UserRef.update(UserData);
                });
            });
        }
    }

    //opens camera and lets user take a picture
    private void TakePicture()
    {
        File photo=new File(Environment.getExternalStorageDirectory(),"photo.jpg");
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ImageUri=FileProvider.getUriForFile(this,this.getApplicationContext().getPackageName()+".provider",photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,ImageUri);
        startActivityForResult(intent, 2);
    }


    //opens a the gallery and lets you choose an image
    public void ChooseFiles()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    //updates the users profile
    private void update()
    {
        Map<String,Object> UserData=new HashMap<>();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        UserData.put("UserName",Username.getText().toString());
        UserData.put("User_Description",Description.getText().toString());
        FirebaseFirestore Database = FirebaseFirestore.getInstance();
        DocumentReference UserRef=Database.collection("Users").document(user.getUid());
        UserRef.update(UserData);
        UploadImage();
        Toast.makeText(this,"Profile Updated",Toast.LENGTH_SHORT).show();
    }

    // returns an image file extension(ie: jpg,png ,etc)
    public String FileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
