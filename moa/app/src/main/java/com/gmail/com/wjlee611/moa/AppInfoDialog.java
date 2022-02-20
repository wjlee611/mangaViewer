package com.gmail.com.wjlee611.moa;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


public class AppInfoDialog extends Dialog {

    public AppInfoDialog(Context context) {
        super(context);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.app_info_popup);
    }

}
