package pt.apps.leandro.portfolio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private Button bt_login;
    private EditText et_name;
    private EditText et_email;
    private EditText et_password;
    private TextView tv_signIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");



        firebaseAuth = FirebaseAuth.getInstance();


        bt_login = (Button) findViewById(R.id.bt_register_login);
        et_email = (EditText) findViewById(R.id.et_register_email);
        et_name = (EditText) findViewById(R.id.et_register_name);
        et_password = (EditText) findViewById(R.id.et_register_password);
        tv_signIn = (TextView) findViewById(R.id.tv_signIn);


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tv_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);

            }
        });

    }

    private void registerUser(){

        final String name = et_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();


        if(TextUtils.isEmpty(name)){

            //email vazio

            Toast.makeText(getApplicationContext(), "Nome vazio", Toast.LENGTH_SHORT).show();

            return;

        }
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

        progressDialog.setMessage("A registar utilizador....");
        progressDialog.show();



        if(!isEmailValid(email)){
            Toast.makeText(getApplicationContext(),"Email inválido",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else if(password.length() <6) {
            Toast.makeText(getApplicationContext(), "Password com menos de 6 caracteres", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else{

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //checking if success
                            if(task.isSuccessful()){
                                //display some message here

                                String userID = firebaseAuth.getCurrentUser().getUid();

                                DatabaseReference current_userDB = mDatabase.child(userID);

                                current_userDB.child("name").setValue(name);
                                current_userDB.child("image").setValue("default");


                                Toast.makeText(getApplicationContext(),"Registado com sucesso!",Toast.LENGTH_SHORT).show();
                                Intent products = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(products);
                            }else{
                                //display some message here
                                Toast.makeText(getApplicationContext(),"Erro no Registo, tente novamente!",Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }


    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
