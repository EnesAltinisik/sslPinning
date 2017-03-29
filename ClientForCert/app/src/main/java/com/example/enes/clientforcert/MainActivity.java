package com.example.enes.clientforcert;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {
    private TextView mWeatherTextView;
    private Button btnCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        btnCount=(Button)findViewById(R.id.button);

        btnCount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        new FetchTask().execute();
    }
    public class FetchTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String[] ret = new String[1];

            BufferedReader reader=null;
            try {

                char[] passphrase = "".toCharArray();
                KeyStore ksTrust = KeyStore.getInstance("BKS");
                Context context = getApplicationContext();

                ksTrust.load(context.getResources().openRawResource(R.raw.trusted), passphrase);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                tmf.init(ksTrust);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());


                SSLSocketFactory sslsocketfactory =  sslContext.getSocketFactory();
                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket("192.168.56.1", 10023);

                InputStreamReader inputstreamreader = new InputStreamReader(sslsocket.getInputStream());
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);


                ret[0]=bufferedreader.readLine();

            }
            catch(Exception ex)
            {
                ret[0]="baglanti saglanamdi";
                Log.d(">>>>>>>>>>>>>>>","HTTP isteginde hata olustu");
                Log.d(">>>>>>>>>>>>>>>", "exception", ex);
            }
            finally
            {
                try
                {
                    reader.close();
                }
                catch(Exception ex) {}
            }
            return  ret;
        }
        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                for (String string : data) {
                    mWeatherTextView.setText(string + "\n\n\n");
                }
            }
        }
    }

}
