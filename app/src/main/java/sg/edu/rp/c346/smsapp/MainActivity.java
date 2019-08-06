package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button sendBtn, sendViaMsgBtn;
    TextView toNumber, contentMsg;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        br = new MessageReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br,filter);

        sendBtn = findViewById(R.id.sendBtn);
        sendViaMsgBtn = findViewById(R.id.sendMsgBtn);
        toNumber = findViewById(R.id.etNum);
        contentMsg = findViewById(R.id.etContent);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arrOfNum = toNumber.getText().toString().split(",");

                for (int i = 0; i < arrOfNum.length;i++) {
                    Log.i("MainActivity","Num : " + arrOfNum[i]);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(arrOfNum[i], null, contentMsg.getText().toString(), null, null);
                }
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                contentMsg.setText("");
            }
        });

        sendViaMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + toNumber.getText().toString())); // This ensures only SMS apps respond
                intent.putExtra("sms_body", contentMsg.getText().toString());
                startActivity(intent);
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}
