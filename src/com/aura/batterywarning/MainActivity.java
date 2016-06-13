package com.aura.batterywarning;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        Intent i = new Intent(MainActivity.this,BatteryWarningService.class);
//		startService(i);
        
        finish();
        
        
    }


   
    
}
