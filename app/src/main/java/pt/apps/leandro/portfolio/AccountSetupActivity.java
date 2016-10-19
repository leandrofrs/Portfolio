package pt.apps.leandro.portfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AccountSetupActivity extends AppCompatActivity {

    private ImageButton mSetupbtn;
    private TextView et_name;
    private Button bt_submit;
    private static final int GALLERY_REQUEST = 1;

    private Uri mImageUri = null;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;

    private StorageReference mStorageImage;

    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mSetupbtn = (ImageButton) findViewById(R.id.bt_setup_image);
        et_name = (TextView) findViewById(R.id.et_setup_name);
        bt_submit = (Button) findViewById(R.id.bt_submit_setup);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mProgress = new ProgressDialog(this);

        mSetupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {

        final String name = et_name.getText().toString().trim();

        final String UserID = mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && mImageUri != null){

            mProgress.setMessage("A guardar dados...");
            mProgress.show();

            StorageReference filePath = mStorageImage.child(mImageUri.getLastPathSegment());


            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(UserID).child("name").setValue(name);
                    mDatabaseUsers.child(UserID).child("image").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent mainIntent  = new Intent(AccountSetupActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST &&  resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                 mImageUri = result.getUri();


                mSetupbtn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
