package com.gmail.com.wjlee611.moa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;
    private WebView mWebView;
    private BackPressCloseHandler backPressCloseHandler;
    private TextView tvCurUrl, tvMainUrl, tvCurHttp, tvCurTitle;
    static String MainUrl = "https://mnmnmnmnm.xyz/";
    String Url1;  // "https://manamoa00.net/"
    static String Url2 = "bbs/board.php?bo_table=manga&wr_id=";
    String[] Url3 = new String[100];
    String MangaName = "N/A";
    private Button btnBookmark;
    boolean is_main = false;
    int is_main_set = 0;

    private ListView mListView;
    ArrayList<String> mArrayList_;
    ArrayAdapter mArrayAdapter_;
    TabHost tabHost1_;

    String[][] DataBase = new String[100][2];  // 이름/인덱스 데이더베이스
    int index = 0;

    int touchDelay = 0;

    private Button btnInfo, btnFord;
    private LinearLayout llCover1 ,llCover2;
    private TextView numOfIndex;
    private String recommName = "";

    int lastidx = -1;

    private int doubleClickFlag = 0;
    private final long CLICK_DELAY = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*탭뷰 세팅*/
        final TabHost tabHost1 = (TabHost) findViewById(R.id.tabhost1);
        tabHost1.setup();
        tabHost1_=tabHost1;

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.contents1);
        ts1.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.selector_book, null));
        tabHost1.addTab(ts1);

        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.contents2);
        ts2.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.selector_bookmark, null));
        tabHost1.addTab(ts2);


        /*스와이프 (새로고침 기능)세팅*/
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.contentSwipeLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        refreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mWebView.getScrollY() == 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });

        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("url");

        if (url != null) {
            mWebView.loadUrl(url);
            refreshLayout.setRefreshing(false);
        }


        tvMainUrl = (TextView) findViewById(R.id.mainUrl);
        llCover1 = (LinearLayout) findViewById(R.id.llCover1);
        llCover2 = (LinearLayout) findViewById(R.id.llCover2);
        numOfIndex = (TextView) findViewById(R.id.numOfIndex);


        /*웹뷰 세팅*/
        tvCurUrl = findViewById(R.id.curUrl);
        tvCurHttp = findViewById(R.id.curhttp);
        tvCurTitle = findViewById(R.id.curtitle);
        mWebView = findViewById(R.id.webView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (!TextUtils.isEmpty(title)) {

                    recommName = title;
                }

                tvCurHttp.setText(mWebView.getUrl().substring(0, mWebView.getUrl().indexOf("://")));

                if (is_main == true) {
                    if (mWebView.getUrl().contains(Url2)) {
                        tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, Url1.length()-1) + " >> " +
                                mWebView.getUrl().substring(mWebView.getUrl().indexOf(Url2)+Url2.length()));
                    } else {
                        if (mWebView.getUrl().length() > 35) {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, 25) + " ... " + mWebView.getUrl().substring(mWebView.getUrl().length()-10));
                        } else {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://") + 3));
                        }
                    }
                }

                if (recommName.length() > 30) {
                    tvCurTitle.setText(recommName.substring(0, 20) + " ... " + recommName.substring(recommName.length()-10));
                } else {
                    tvCurTitle.setText(recommName);
                }

                if (mWebView.getUrl().equals(MainUrl)) {
                    tvCurUrl.setText("Loading...");
                }
            }
        });


        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                tvCurUrl.setTextColor(Color.rgb(255,255,255));
                refreshLayout.setRefreshing(false);

                tvCurHttp.setText(mWebView.getUrl().substring(0, mWebView.getUrl().indexOf("://")));

                if (is_main == true) {
                    if (mWebView.getUrl().contains(Url2)) {
                        tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, Url1.length()-1) + " >> " +
                                mWebView.getUrl().substring(mWebView.getUrl().indexOf(Url2)+Url2.length()));
                    } else {
                        if (mWebView.getUrl().length() > 35) {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, 25) + " ... " + mWebView.getUrl().substring(mWebView.getUrl().length()-10));
                        } else {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://") + 3));
                        }
                    }
                }

                if (recommName.length() > 30) {
                    tvCurTitle.setText(recommName.substring(0, 20) + " ... " + recommName.substring(recommName.length()-10));
                } else {
                    tvCurTitle.setText(recommName);
                }

                if (mWebView.getUrl().equals(MainUrl)) {
                    tvCurUrl.setText("Loading...");
                }
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (mWebView.getUrl().contains("manamoa")) {
                    if (is_main_set == 0) {
                        is_main = true;
                        is_main_set++;

                        tvMainUrl.setText("[최신 주소]  " + mWebView.getUrl());
                        Url1 = mWebView.getUrl();
                        llCover1.setVisibility(View.GONE);
                        llCover2.setVisibility(View.VISIBLE);
                        numOfIndex.setText(index+"개");
                    }
                }

                if (is_main == true) {
                    tvCurUrl.setTextColor(Color.rgb(200,200,200));

                    if (mWebView.getUrl().contains(Url2)) {
                        tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, Url1.length()-1) + " >> " +
                                mWebView.getUrl().substring(mWebView.getUrl().indexOf(Url2)+Url2.length()));
                    } else {
                        if (mWebView.getUrl().length() > 35) {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://")+3, 25) + " ... " + mWebView.getUrl().substring(mWebView.getUrl().length()-10));
                        } else {
                            tvCurUrl.setText(mWebView.getUrl().substring(mWebView.getUrl().indexOf("://") + 3));
                        }
                    }
                }

                if (recommName.length() > 30) {
                    tvCurTitle.setText(recommName.substring(0, 20) + " ... " + recommName.substring(recommName.length()-10));
                } else {
                    tvCurTitle.setText(recommName);
                }

                if (mWebView.getUrl().equals(MainUrl)) {
                    tvCurUrl.setText("Loading...");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            // 웹뷰내 다운로드가 가능한 파일이 있다면!
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Log.i("DownloadInfo", "url:" + url + "/userAgent:" + userAgent + "/contentDisposition:" + contentDisposition + "/mimetype:" + mimetype + "/contentLength:" + contentLength);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file");
                    String fileName = contentDisposition.replace("inline; filename=", "");
                    fileName = fileName.replaceAll("\"", "");
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                    Toast.makeText(getApplicationContext(), "WebViewDownloadManager:" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        WebSettings mws = mWebView.getSettings();
        mws.setJavaScriptEnabled(true);
        mws.setSaveFormData(true);
        mws.setGeolocationEnabled(true);
        mws.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= 16) {
            mws.setAllowFileAccessFromFileURLs(true);
            mws.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 21){
            mws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mws.setLoadsImagesAutomatically(true);
        mws.setUseWideViewPort(true);
        mws.setBuiltInZoomControls(true);
        mws.setDisplayZoomControls(false);
        mws.setCacheMode(WebSettings.LOAD_DEFAULT);
        mws.setAppCacheEnabled(true);
        mws.setAllowFileAccess(true);
        mws.setDomStorageEnabled(true);
        mws.setLoadWithOverviewMode(true);
        mws.setUseWideViewPort(true);
        mws.setPluginState(WebSettings.PluginState.ON);
        mWebView.loadUrl(MainUrl);

        this.backPressCloseHandler = new BackPressCloseHandler(this);


        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doubleClickFlag++;
                Handler dchandler = new Handler();
                Runnable clickRunnable = new Runnable() {
                    @Override
                    public void run() {
                        doubleClickFlag = 0;
                    }
                };
                if (doubleClickFlag == 1) {
                    dchandler.postDelayed(clickRunnable, CLICK_DELAY);
                } else if (doubleClickFlag == 2) {
                    doubleClickFlag = 0;
                    Toast.makeText(getApplicationContext(), "double tab", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*리스트뷰*/
        mListView = (ListView) findViewById(R.id.listview1);

        final ArrayList<String> mArrayList = new ArrayList<>();
        final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mArrayList);

        mArrayList_ = mArrayList;
        mArrayAdapter_ = mArrayAdapter;

        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        mListView.setAdapter(mArrayAdapter);

        tvMainUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_main == false) {
                    Toast.makeText(getApplicationContext(), "로딩 중 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if (touchDelay == 0) {
                        mWebView.loadUrl(Url1);
                        tabHost1.setCurrentTabByTag("Tab Spec 1");
                        mWebView.clearCache(true);
                        mWebView.clearHistory();
                        Toast.makeText(getApplicationContext(), "최적화: 이전 사용기록을 지웠습니다.", Toast.LENGTH_SHORT).show();

                        lastidx = -1;
                    }
                }
            }
        });

        tvMainUrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (is_main == false) {
                    Toast.makeText(getApplicationContext(), "로딩 중 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    touchDelay = 1;

                    Url1 = mWebView.getUrl();
                    tvMainUrl.setText(""+Url1);
                    Toast.makeText(getApplicationContext(), "현재 페이지로 변경되었습니다.", Toast.LENGTH_SHORT).show();

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable()  {
                        public void run() {
                            touchDelay = 0;
                        }
                    }, 1000);

                }
                return false;
            }
        });


        /*데이터 불러오기*/
        try{
            BufferedReader br = new BufferedReader(new FileReader(getFilesDir() + "data.txt"));
            String readStr = "";
            String str = null;
            while ((str = br.readLine()) != null) {
                readStr = str;
            }
            if(readStr != "") {
                String[] array = readStr.split("`");
                for(int k=0;k<array.length;k+=2) {
                    DataBase[k/2][0] = array[k];
                    DataBase[k/2][1] = array[k+1];
                }
                index = array.length/2;
                for(int k=0;k<index;k++) {
                    mArrayList.add(DataBase[k][0]);
                    Url3[k] = DataBase[k][1];
                }
                mArrayAdapter.notifyDataSetChanged();
            }

            br.close();
            //Toast.makeText(getApplicationContext(), readStr, Toast.LENGTH_SHORT).show();  // 테스트용
            mArrayAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "File not Found. Inicializing...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*북마크 버튼*/
        btnBookmark = (Button) findViewById(R.id.btnBookmark);
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index >= 100) {
                    Toast.makeText(getApplicationContext(), "북마크가 너무 많아요!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), CustomDialog.class);
                    intent.putExtra("address", mWebView.getUrl());
                    intent.putExtra("recommName", recommName);
                    intent.putExtra("index", lastidx);
                    if (lastidx > -1) {
                        intent.putExtra("li_name", mArrayList.get(lastidx));
                    } else {
                        intent.putExtra("li_name", "없습니다. 새로운 북마크를 생성합니다.");
                    }

                    startActivityForResult(intent, 0);

                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            tabHost1.setCurrentTabByTag("Tab Spec 2");
                        }
                    }, 300);
                }
            }
        });

        /*리스트뷰 클릭 이벤트*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(touchDelay==0) {
                    lastidx = i;
                    mWebView.loadUrl(Url1 + Url2 + Url3[i]);

                    tabHost1.setCurrentTabByTag("Tab Spec 1");

                    //Toast.makeText(getApplicationContext(),DataBase[i][0] + DataBase[i][1], Toast.LENGTH_SHORT).show();  // 테스트용
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                touchDelay = 1;
                mArrayList_.remove(i);
                mArrayAdapter_.notifyDataSetChanged();

                int temp = i + 1;

                for(int j=temp;j<index;j++) {
                    Url3[j-1] = Url3[j];
                    DataBase[j-1][0] = DataBase[j][0];
                    DataBase[j-1][1] = DataBase[j][1];
                }

                lastidx = -1;

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        touchDelay = 0;
                    }
                }, 1000);

                index--;
                /*저장하기*/
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + "data.txt"));
                    String str = "";
                    for(int k=0;k<index;k++) {
                        str += (DataBase[k][0].toString() + "`" + DataBase[k][1].toString() + "`");
                    }
                    bw.write(str);
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                numOfIndex.setText(index+"개");

                return false;
            }
        });

        /*인포 버튼*/
        btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppInfoDialog dialog = new AppInfoDialog(MainActivity.this);
                dialog.show();
            }
        });

        btnInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String str = "";
                for(int k=0;k<index;k++) {
                    str += (DataBase[k][0].toString() + "`" + DataBase[k][1].toString() + "`");
                }

                Intent intent = new Intent(getApplicationContext(), DataCustomDialog.class);
                intent.putExtra("dataCode", str);
                startActivityForResult(intent, 1);

                return false;
            }
        });


        /*앞으로 가기 버튼*/
        btnFord = (Button) findViewById(R.id.btnFord);

        btnFord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.goForward();
            }
        });

        btnFord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                return false;
            }
        });
    }


    /*이름, 주소 인덱스 받아오기*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data.getIntExtra("index", -1) == -1) {
                MangaName = data.getStringExtra("result");
                Url3[index] = data.getStringExtra("url3");
                Toast.makeText(this, MangaName + " 이(가) 추가되었습니다.", Toast.LENGTH_SHORT).show();
                mArrayList_.add(MangaName);
                mArrayAdapter_.notifyDataSetChanged();
                DataBase[index][0] = MangaName;
                DataBase[index][1] = Url3[index];
                index++;
                numOfIndex.setText(index+"개");

                mListView.smoothScrollToPosition(index);
            } else {
                mArrayList_.set(data.getIntExtra("index", 0), data.getStringExtra("result"));
                Url3[data.getIntExtra("index", 0)] = data.getStringExtra("url3");
                Toast.makeText(this, "덮어씌웠습니다!", Toast.LENGTH_SHORT).show();
                mArrayAdapter_.notifyDataSetChanged();
                DataBase[data.getIntExtra("index", 0)][0] = data.getStringExtra("result");
                DataBase[data.getIntExtra("index", 0)][1] = data.getStringExtra("url3");
                numOfIndex.setText(index+"개");

                mListView.smoothScrollToPosition(data.getIntExtra("index", 0));
            }

            /*저장하기*/
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + "data.txt"));
                String str = "";
                for(int k=0;k<index-1;k++) {
                    str += (DataBase[k][0].toString() + "`" + DataBase[k][1].toString() + "`");
                }
                str += (DataBase[index-1][0].toString() + "`" + DataBase[index-1][1].toString() + "`");
                bw.write(str);
                //Toast.makeText(getApplicationContext(), str,Toast.LENGTH_SHORT).show();  // 테스트용
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            tabHost1_.setCurrentTabByTag("Tab Spec 1");
        }


        /*데이터 복구*/
        if (requestCode == 1 && resultCode == RESULT_OK) {

            /*저장하기*/
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + "data.txt"));
                bw.write(data.getStringExtra("data"));
                //Toast.makeText(getApplicationContext(), str,Toast.LENGTH_SHORT).show();  // 테스트용
                bw.close();

                Toast.makeText(getApplicationContext(), "적용되었습니다. 앱을 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /*웹 뒤로가기 키*/
    public void onBackPressed() {
        if (tabHost1_.getCurrentTabTag() == "Tab Spec 1") {
            if (this.mWebView.canGoBack()) {
                this.mWebView.goBack();
            } else {
                this.backPressCloseHandler.onBackPressed();
            }
        } else {
            tabHost1_.setCurrentTabByTag("Tab Spec 1");
        }
    }

}
