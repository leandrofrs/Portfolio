package pt.apps.leandro.portfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mBtSubmit;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mSelectImage = (ImageButton) findViewById(R.id.image_select_post);
        mPostTitle = (EditText) findViewById(R.id.et_title_post);
        mPostDesc = (EditText) findViewById(R.id.et_desc_post);
        mBtSubmit = (Button) findViewById(R.id.bt_submit_post);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        firebaseAuth = FirebaseAuth.getInstance();




        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });


        mBtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

         }



    private void startPosting() {


        mProgress.setMessage("Uploading...");

        final String titleValue = mPostTitle.getText().toString().trim();
        final String descValue = mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(descValue) && imageUri != null){

            mProgress.show();


            StorageReference filePath = mStorage.child("Post_Images").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadURL = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("Title").setValue(titleValue);
                            newPost.child("Desc").setValue(descValue);
                            newPost.child("image").setValue(downloadURL.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    }

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));

                }
            });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

             imageUri = data.getData();

            mSelectImage.setImageURI(imageUri);


        }
    }
}
