package com.gmail.com.wjlee611.moa;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
    private Activity activity;
    private long backKeyPressedTime;
    private Toast toast;

    public BackPressCloseHandler(Activity context) {
        this.backKeyPressedTime = 0;
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > this.backKeyPressedTime + 2000) {
            this.backKeyPressedTime = System.currentTimeMillis();
            showGuide();
        } else if (System.currentTimeMillis() <= this.backKeyPressedTime + 2000) {
            this.activity.finish();
            this.toast.cancel();
        }
    }

    public void showGuide() {
        this.toast = Toast.makeText(this.activity, "한번 더 입력시 종료됩니다.", Toast.LENGTH_SHORT);
        this.toast.show();
    }
}
