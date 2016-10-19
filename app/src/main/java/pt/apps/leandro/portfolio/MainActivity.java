package pt.apps.leandro.portfolio;


import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import pt.apps.leandro.portfolio.Adapters.ProductsAdapter;
import pt.apps.leandro.portfolio.Model.Post;

public class MainActivity extends AppCompatActivity {

    private RecyclerView postList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabasePost;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Button bt_logout;
    private TextView tv_name;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabaseUser;

    private DatabaseReference mDatabaseCurrentUser;
    private Query mQueryCurrentUser;



    //TESTE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        final String userId = mAuth.getCurrentUser().getUid();

        mDatabasePost = FirebaseDatabase.getInstance().getReference().child("Post").child(userId);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        mCurrentUser = mAuth.getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());


        String currentUserID = mAuth.getCurrentUser().getUid();

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Post");

        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserID);



        postList = (RecyclerView) findViewById(R.id.post_list_main);
        postList.setHasFixedSize(true);
        postList.setLayoutManager(new LinearLayoutManager(this));

        bt_logout = (Button) findViewById(R.id.bt_main_logout);



        tv_name = (TextView) findViewById(R.id.tv_name_main);

        tv_name.setText(mAuth.getCurrentUser().getDisplayName());

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
               /* Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);*/
            }
        });

        checkUserExist();


    }



    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                Post.class,
                R.layout.post_row,
                PostViewHolder.class,
                mQueryCurrentUser

        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {



                String username = mDatabasePost.toString();
                String usernameDB = mDatabaseUser.toString();

                String usernameCompare = username.replace("https://portfolio-bb216.firebaseio.com/Post/", "");
                String usernameDBCompare = usernameDB.replace("https://portfolio-bb216.firebaseio.com/users/", "");

                Query queryRef = mDatabasePost.orderByChild("username").equalTo(username);

                //falta ver quais os do utilizador

                if (usernameCompare.equals(usernameDBCompare)) {
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDesc(model.getDesc());
                    viewHolder.setImage(getApplicationContext(), model.getImage());

                }else {
                    Toast.makeText(getApplicationContext(), "nenhum post adicionado", Toast.LENGTH_SHORT).show();
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "foi clicado", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };


            postList.setAdapter(firebaseRecyclerAdapter);


    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String userID = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(userID)) {

                        Intent setupIntent = new Intent(MainActivity.this, AccountSetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView post_title;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


            post_title = (TextView) mView.findViewById(R.id.post_title_main);

            post_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mView.getContext(), "titulo foi clicado", Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void setTitle(String title){

            //TextView post_title = (TextView) mView.findViewById(R.id.post_title_main);
            post_title.setText(title);

        }
        public void setDesc(String desc){

            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc_main);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.iv_post_main);
            Picasso.with(ctx).load(image).into(post_image);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_add){
            Intent post = new Intent(MainActivity.this, PostActivity.class);
            startActivity(post);
        }
        return super.onOptionsItemSelected(item);
    }
}
