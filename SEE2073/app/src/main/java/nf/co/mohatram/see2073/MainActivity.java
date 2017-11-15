package nf.co.mohatram.see2073;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String website = "http://see.ntc.net.np/gradesheet.php";
    private EditText seeNumber;
    private WebView webView;
    private Nthread nthread;
    private URL url;
    private EditText dob;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nthread = new Nthread();
        nthread.start();

        seeNumber = (EditText) findViewById(R.id.et_symbol_number);
        dob = (EditText) findViewById(R.id.et_dob);
        webView = (WebView) findViewById(R.id.webview);


    }

    private String sn;
    ;
    private String dobS;

    public void seeResult(View view) {
      sn = seeNumber.getText().toString();
        dobS = this.dob.getText().toString();
        nthread.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    url = new URL(website);
                    postData();
                } catch (MalformedURLException e) {
                }
            }
        });
        Log.e("Post ", "post");
    }

    private void postData() {


        final String post = "symbol=" + sn + "&dob=" + dobS;
        Log.e("Check", post);

        nthread.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(post.getBytes());
                    outputStream.flush();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String data = null;
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        data += line;
                    }
                    Log.e("Line", data);

                    showData(data);
                } catch (IOException e) {
                    Log.e("Error", "", e);
                }
            }
        });
    }

    private void showData(String data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


}
