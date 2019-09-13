/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText usernameEditText;
    EditText passwordEditText;
    TextView textView;
    Button button;

    boolean signUpModeActive = true;

    public void showUserList(){
        Intent intent = new Intent(getApplicationContext(),UsersListActivity.class);
        startActivity(intent);
    }

    public void signUp(View view){

        final String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!username.matches("") && !password.matches("")){
            if (signUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Success", "Signed Up!");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null){
                            Log.i("Success","Logged In");
                            showUserList();
                        }else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Toast.makeText(this, "A username and a password is required", Toast.LENGTH_SHORT).show();
        }
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Instagram");

    usernameEditText = (EditText) findViewById(R.id.usernameEditText);
    passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    textView = (TextView) findViewById(R.id.textView);
    button = (Button) findViewById(R.id.button);
    ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
    RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.layout);

    textView.setOnClickListener(this);
    logoImageView.setOnClickListener(this);
    backgroundLayout.setOnClickListener(this);

    if (ParseUser.getCurrentUser() != null){
        showUserList();
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textView){
            if (signUpModeActive) {
                textView.setText("or, Sign Up");
                button.setText("Login");
                signUpModeActive = false;
            }else{
                textView.setText("or, Login");
                button.setText("Sign Up");
                signUpModeActive = true;
            }
        }else if (view.getId() == R.id.layout || view.getId() == R.id.logoImageView){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            signUp(view);
            return  true;
        }
        return false;
    }
}