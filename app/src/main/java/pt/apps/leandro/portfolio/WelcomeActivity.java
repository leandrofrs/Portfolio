package pt.apps.leandro.portfolio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

       /* ProductLoader pLoader = new ProductLoader(WelcomeActivity.this);
        pLoader.execute();*/

        Intent cenas = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(cenas);

    }


}
