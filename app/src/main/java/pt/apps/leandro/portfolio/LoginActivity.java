package pt.apps.leandro.portfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button bt_login;
    private EditText et_email;
    private EditText et_password;
    private TextView tv_signUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseUsers;
    private SignInButton mGoogleButton;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        if (firebaseAuth.getCurrentUser() != null){

            //se ja estiver logado
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
        }
        bt_login = (Button) findViewById(R.id.bt_login_login);
        et_email = (EditText) findViewById(R.id.et_login_email);
        et_password = (EditText) findViewById(R.id.et_login_password);
        tv_signUp = (TextView) findViewById(R.id.tv_signUp);

        mGoogleButton = (SignInButton) findViewById(R.id.bt_google_signIn);


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });



        //gooogle Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                Toast.makeText(getApplicationContext(), "Erro no login", Toast.LENGTH_SHORT).show();

            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);



            progressDialog.setMessage("A fazer login...");
            progressDialog.show();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                progressDialog.dismiss();


                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else {

                            progressDialog.dismiss();


                            checkUserExist();
                        }
                        // ...
                    }
                });
    }
    private void userLogin(){
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();



        if(TextUtils.isEmpty(email)){

            //email vazio

            Toast.makeText(getApplicationContext(), "Email vazio", Toast.LENGTH_SHORT).show();

            return;

        }
        if(TextUtils.isEmpty(password)){
            //password vazia
            Toast.makeText(getApplicationContext(), "Password vazia", Toast.LENGTH_SHORT).show();

            return;

        }

        progressDialog.setMessage("A autenticar...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //checking if success
                if(task.isSuccessful()){

                    progressDialog.dismiss();

                    checkUserExist();

                }else{

                    progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(),"Erro no login, tente novamente!",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void checkUserExist() {

        if(firebaseAuth.getCurrentUser() != null){



        final String userID = firebaseAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(userID)) {

                    Intent mainIntent  = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }else {
                    Intent setupIntent  = new Intent(LoginActivity.this, AccountSetupActivity.class);
                    setupIntent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }
    }
}
