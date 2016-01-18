package com.example.user.climbinghelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by user on 2016-01-06.
 */
public class Recode extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recode);

        try{
            ViewConfiguration config=ViewConfiguration.get(this);
            Field menuKeyField=ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField!=null){
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }

        } catch(Exception ex){

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        //noinspection SimplifiableIfStatement
        switch(item.getItemId()){
            case R.id.info:
                Intent intent1=new Intent(this, Search.class);
                startActivity(intent1);
                return true;

            case R.id.login:
                Intent intent2=new Intent(this,Login.class);
                startActivity(intent2);
                return true;

            case R.id.recode:
                Intent intent3=new Intent(this, Recode.class);
                startActivity(intent3);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
