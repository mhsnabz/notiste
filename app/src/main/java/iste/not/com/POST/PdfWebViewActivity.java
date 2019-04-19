package iste.not.com.POST;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import iste.not.com.Main.MainActivity;
import iste.not.com.R;

public class PdfWebViewActivity extends AppCompatActivity {
    public static WebView mWebview;
    private android.content.Context Context;
    private static String getIntentValue = null;
    public static SharedPreferences sharedPreferences;
    private ProgressDialog mProgressDialog;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        mWebview.setWebViewClient(new Callback());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web_view);
        Context = this;


        getIntentValue = getIntent().getStringExtra("pdf");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



            mWebview = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = mWebview.getSettings();
            mWebview.getSettings().setJavaScriptEnabled(true);
            mWebview.setWebChromeClient(new WebChromeClient());
           // mWebview.setWebViewClient(new CustomWebViewClient());

            //improve WebView Performance
            mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            mWebview.getSettings().setAppCacheEnabled(false);
            mWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
             mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);

            if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(PdfWebViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 1);
            }

            mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setSavePassword(true);
            webSettings.setSaveFormData(true);
            webSettings.setEnableSmoothTransition(true);
            mWebview.loadUrl(getIntentValue);
            // progress dialog
            mProgressDialog = new ProgressDialog(Context);

            if (sharedPreferences.getBoolean("isKeyGenerated", true)) {

                if (getIntentValue != null) {
                    mWebview.loadUrl("http://www.example.com/page");
                    getIntentValue = null;

                } else {
                    mWebview.loadUrl("http://www.example.com/page2");
                }
            }



    }


    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(".pdf")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

            //on page started, show loading page
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();

        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            String currentPage= mWebview.getUrl();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentpage",currentPage);
            editor.commit();

            //after loading page, remove loading page

            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            mProgressDialog.dismiss();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            view.loadUrl("http://example.com/page");
        }

    }






    public static void loadUrl(String key) {

        if (getIntentValue != null) {
            mWebview.loadUrl("http://example.com/page");
            getIntentValue = null;
        } else {
            mWebview.loadUrl("http://example.com/page");
        }

    }


    public static void reLoad() {
        mWebview.reload();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    }

    class Callback extends WebViewClient{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            //Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }

    }


