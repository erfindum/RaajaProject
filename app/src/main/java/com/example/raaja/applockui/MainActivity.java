package com.example.raaja.applockui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by RAAJA on 18-08-2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {
EditText edit ;
    EditText edit2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_sample);

    }

    @Override
    protected void onStart() {
        super.onStart();
       // getSupportFragmentManager().beginTransaction().add(R.id.user_profile_activity_fragment_container,new SettingsFragment(),"UserProfile").commit();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText edit = (EditText) v;
        if(hasFocus){
            edit.setCompoundDrawablesWithIntrinsicBounds(getSelectedDrawableId(edit),0,0,0);

        }else {
            edit.setCompoundDrawablesWithIntrinsicBounds(getDrawableId(edit),0,0,0);
        }
    }

    int getDrawableId(EditText text){

        int editId = text.getId();
        switch (editId){
            case R.id.login_fragment_email_edit:
                return R.drawable.ic_login_edit_person;
            case R.id.login_fragment_password_edit:
                return R.drawable.ic_login_edit_password;
        }
        return 0;
    }

    int getSelectedDrawableId(EditText text){

        int editId = text.getId();
        switch (editId){
            case R.id.login_fragment_email_edit:
                return R.drawable.ic_login_edit_person_selected;
            case R.id.login_fragment_password_edit:
                return R.drawable.ic_login_edit_password_selected;
        }
        return 0;
    }
}
