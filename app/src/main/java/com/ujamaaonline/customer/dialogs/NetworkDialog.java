package com.ujamaaonline.customer.dialogs;

import android.content.Context;
import android.view.View;

import com.ujamaaonline.customer.R;
import com.ujamaaonline.customer.components.FontAwesomeIcon;
import com.ujamaaonline.customer.utils.UIUtil;


public class NetworkDialog extends GlobalAlertDialog {

    public NetworkDialog(Context context) {
        super(context, true, false);
    }

    @Override
    protected int layoutID() {
        return R.layout.dialog_network;
    }

    @Override
    protected void loadHeader() {
        super.loadHeader();
        FontAwesomeIcon wifiIcon = findViewById(R.id.titleIcon);
        wifiIcon.setText(R.string.icon_wifi);
        wifiIcon.setAnimation(UIUtil.animBlink());
        wifiIcon.setTextColor(UIUtil.getColor(R.color.white));
    }

    @Override
    protected void loadBody() {

    }

    @Override
    protected void loadFooter() {
        //this.setPositiveBtnTxt(ApplicationHelper.application().getResources().getString(R.string.go_settings));

        super.loadFooter();
        this.cancelBtnLayout.setVisibility(View.GONE);
    }

//    @Override
//    public void onConfirmation() {
//        //ApplicationHelper.application().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ApplicationHelper.application().startActivity(intent);
//    }

}
