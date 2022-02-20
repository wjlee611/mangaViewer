package com.gmail.com.wjlee611.moa;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class DataCustomDialog extends Activity {

    private String DataCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_popup);

        final EditText etDataCode = (EditText) findViewById(R.id.etDataCode);
        Button btnCopyData = (Button) findViewById(R.id.btnCopyDataCode);
        Button btnOK = (Button) findViewById(R.id.btnDlgOK2);
        Button btnCancel = (Button) findViewById(R.id.btnDlgCancel2);

        Intent intent = getIntent();
        DataCode = intent.getStringExtra("dataCode");

        btnCopyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("BookmarkData", DataCode);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "북마크 데이터코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "정말 적용하시겠습니까?", Snackbar.LENGTH_LONG).setAction("적용 후 나가기", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent data = new Intent();
                        data.putExtra("data", etDataCode.getText().toString());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
