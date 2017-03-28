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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        loadData();
    }

    private void loadData() {
        new FetchTask().execute(checkAppSignature(this));
    }
    public class FetchTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String sign = params[0];
            String[] ret = new String[1];

            BufferedReader reader=null;
            try {

                char[] passphrase = "Ea1989".toCharArray();
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

                OutputStream outputstream = sslsocket.getOutputStream();
                OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
                BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);

                ret[0]=bufferedreader.readLine();

                bufferedwriter.write("denem");
                bufferedwriter.flush();
                String string ;
              // while ((string = bufferedreader.readLine()) != null) {
              //  }
            }
            catch(Exception ex)
            {
                System.out.println(">>>>>>>>>>>>>>>HTTP isteginde hata olustu");
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
                    mWeatherTextView.append(string + "\n\n\n");
                }
            }
        }
    }
    public static String checkAppSignature(Context context) {

        try {

            PackageInfo packageInfo = context.getPackageManager()

                    .getPackageInfo(context.getPackageName(),

                            PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                return  Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //compare signatures


            }

        } catch (Exception e) {

//assumes an issue in checking signature., but we let the caller decide on what to do.

        }

        return null;

    }
}
