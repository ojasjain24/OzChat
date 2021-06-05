package com.example.chatappv0;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;

public class ThemeSetter extends AppCompatActivity {
    public void aSetTheme(Context context, Resources.Theme theme){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String text = sharedPreferences.getString("Theme", "Default");
        if(text.equals("Default"))   { theme.applyStyle(R.style.AppTheme,true);}
        if(text.equals("Yellow"))    { theme.applyStyle(R.style.Yellow,true); }
        if(text.equals("Green"))     { theme.applyStyle(R.style.Green, true);}
        if(text.equals("Blue"))      { theme.applyStyle(R.style.Blue,true);}
        if(text.equals("SkyBlue"))   { theme.applyStyle(R.style.SkyBlue,true);}
        if(text.equals("Red"))       { theme.applyStyle(R.style.Red,true);}
        if(text.equals("OceanBlue")) { theme.applyStyle(R.style.OceanBlue,true);}
    }
}
