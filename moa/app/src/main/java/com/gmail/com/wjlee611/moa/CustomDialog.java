package com.gmail.com.wjlee611.moa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog extends Activity {

    private String dlgUrl3;
    private String recommName;

    int lastidx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_popup);

        final EditText dlgEdt = (EditText) findViewById(R.id.diaEtMangaName);
        final TextView dlgTv = (TextView) findViewById(R.id.textView0);
        TextView dlgTvUrl3 = (TextView) findViewById(R.id.dlgUrl3);
        TextView dlgTvUrl3Edit = (TextView) findViewById(R.id.dlgUrl3Edit);
        Button dlgBtnOK = (Button) findViewById(R.id.btnDlgOK);
        Button dlgBtnCancel = (Button) findViewById(R.id.btnDlgCancel);
        Button dlgBtnOR = (Button) findViewById(R.id.btnDlgOR);
        TextView liName = (TextView) findViewById(R.id.tvlastload);

        Intent intent = getIntent();
        dlgUrl3 = intent.getStringExtra("address");
        recommName = intent.getStringExtra("recommName");
        dlgTvUrl3.setText("주소_ " + intent.getStringExtra("address"));
        dlgEdt.setText(recommName);
        dlgTv.setText("만화 이름_ [추천]");
        liName.setText(intent.getStringExtra("li_name"));
        lastidx = intent.getIntExtra("index", -1);


        /*인덱스 부분만 추출*/
        int idx = dlgUrl3.indexOf("_id=");
        dlgUrl3 = dlgUrl3.substring(idx+4);
        if(dlgUrl3.contains("&_")) {
            idx = dlgUrl3.indexOf("&_");
            dlgUrl3 = dlgUrl3.substring(0, idx);
        }
        if(dlgUrl3.contains("/?_")) {
            idx = dlgUrl3.indexOf("/?_");
            dlgUrl3 = dlgUrl3.substring(0, idx);
        }
        dlgTvUrl3Edit.setText("인덱스 추출_ " + dlgUrl3);

        dlgEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dlgTv.setText("만화 이름_");
                return false;
            }
        });

        dlgBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dlgEdt.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    dlgEdt.setText(dlgEdt.getText().toString().replace('`','\''));
                    dlgEdt.setText(dlgEdt.getText().toString().replace('\n',' '));
                    data.putExtra("result", dlgEdt.getText().toString());
                    data.putExtra("url3", dlgUrl3);
                    data.putExtra("index", -1);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        dlgBtnOR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dlgEdt.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    dlgEdt.setText(dlgEdt.getText().toString().replace('`','\''));
                    dlgEdt.setText(dlgEdt.getText().toString().replace('\n',' '));
                    data.putExtra("result", dlgEdt.getText().toString());
                    data.putExtra("url3", dlgUrl3);
                    data.putExtra("index", lastidx);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        dlgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
