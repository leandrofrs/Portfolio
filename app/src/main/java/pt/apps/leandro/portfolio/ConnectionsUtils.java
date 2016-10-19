package pt.apps.leandro.portfolio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.apps.leandro.portfolio.Parsers.ProductParser;


public class ConnectionsUtils {

    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void loadProducts(){
        HttpURLConnection connection = null;

        try {
            URL url = new URL(Globals.restPproducts);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();

            BufferedReader reply = new BufferedReader(new InputStreamReader(in));

            Gson parser = new Gson();
            ProductParser products = parser.fromJson(reply, ProductParser.class);

            Globals.products = products.getProducts();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}
