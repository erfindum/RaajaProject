package com.example.raaja.applockui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by RAAJA on 18-08-2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener {
EditText edit ;
    EditText edit2;
    PatternLockView lockView;
    TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_sample);
        lockView = (PatternLockView) findViewById(R.id.patternLock);
        textView = (TextView) findViewById(R.id.textInfo);
        setLockViewListener();
    }

    void setLockViewListener(){
        ObjectAnimator textAnimator = ObjectAnimator.ofInt(textView,"padding",0,0,0,0,10,0,0,0);
        textAnimator.setDuration(100).setInterpolator(new LinearInterpolator());
        textAnimator.setRepeatCount(3);
        ObjectAnimator textAnimator1 = ObjectAnimator.ofInt(textView,"padding",0,0,0,0,0,0,10,0);
        textAnimator.setDuration(100).setInterpolator(new LinearInterpolator());
        textAnimator1.setRepeatCount(3);
        final AnimatorSet animateSet = new AnimatorSet();
        animateSet.playSequentially(textAnimator,textAnimator1);
        animateSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                textView.setTextColor(Color.parseColor("#ef5350"));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textView.setTextColor(Color.parseColor("#ffffff"));
                lockView.resetPatternView();
            }
        });

        lockView.setOnPatternChangedListener(new PatternLockView.OnPatternChangedListener() {
            String pattern="";
            @Override
            public void onPatternNodeSelected(int selectedPatternNode) {
                pattern+=String.valueOf(selectedPatternNode);
            }

            @Override
            public void onPatternCompleted(boolean patternCompleted) {
                if(Integer.parseInt(pattern) != 1456){
                    lockView.postPatternError();
                    animateSet.start();
                    pattern="";
                }
            }
        });
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
