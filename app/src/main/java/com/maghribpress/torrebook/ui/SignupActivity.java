package com.maghribpress.torrebook.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.SharedPrefManager;
import com.maghribpress.torrebook.network.GetBooksDataService;
import com.maghribpress.torrebook.network.RetrofitInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gopalsamy.k on 20/3/17.
 */

public class SignupActivity extends AppCompatActivity {
    private EditText _nameText;
    private EditText _emailText;
    private EditText _passwordText;
    private AppCompatButton _signupButton;
    // private TextView _loginLink;
    GetBooksDataService mApiService;
    Context mcontext;
    SharedPrefManager sharedPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }
        sharedPref = new SharedPrefManager(this);
        mApiService = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }
        _signupButton = (AppCompatButton)findViewById(R.id.signinButton);
        mcontext = this;
        // Set up the login form.
        _nameText = (EditText) findViewById(R.id.txt_username);
        _emailText = (EditText) findViewById(R.id.txt_email);
        _passwordText = (EditText) findViewById(R.id.txt_password);
        _signupButton = (AppCompatButton) findViewById(R.id.btn_signup);
        TextView logoText = (TextView) findViewById(R.id.txt_logo_title);
        Typeface mtypeFace = Typeface.createFromAsset(mcontext.getAssets(),
                "fonts/caviardreams.ttf");
        logoText.setTypeface(mtypeFace);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }
    public void signup() {

        if (!validate()) {
            onSignupFailed(getString(R.string.acount_creation_failed));
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        Call<ResponseBody> call = mApiService.register(name,email,password);

        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.has("success")){
                                if(jsonRESULTS.getBoolean("success")) {
                                    JSONObject jsonToken = jsonRESULTS.getJSONObject("data");
                                    String token = jsonToken.getString("token");
                                    String name = jsonToken.getString("name");
                                    String email = jsonToken.getString("email");
                                    sharedPref.saveToken(token);
                                    sharedPref.saveName(name);
                                    sharedPref.saveEmail(email);
                                    sharedPref.saveLoginState(true);
                                    sharedPref.setDisclaimerState(false);
                                    ApiTokenObject.getInstance().setToken(token);
                                    ApiTokenObject.getInstance().setmEmail(email);
                                    ApiTokenObject.getInstance().setmUsername(name);
                                    progressDialog.dismiss();
                                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                    i.putExtra("usrname", name);
                                    i.putExtra("usrmail", email);
                                    startActivity(i);
                                    finish();
                                }else {
                                    if (jsonRESULTS.has("error")){
                                        String errorMessage = jsonRESULTS.getString("error");
                                        onSignupFailed(errorMessage);
                                    }
                                }
                            } else {
                                // Jika login gagal
                                onSignupFailed(getString(R.string.acount_creation_failed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            _signupButton.setEnabled(true);
                            progressDialog.dismiss();
                        }catch (IOException e) {
                            e.printStackTrace();
                            _signupButton.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }else {
                        _signupButton.setEnabled(true);
                        progressDialog.dismiss();
                        onSignupFailed(getString(R.string.failed_to_connect_to_our_server));
                    }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _signupButton.setEnabled(true);
                progressDialog.dismiss();
                onSignupFailed(getString(R.string.login_attempt_failed));
            }
        });
    }
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(500);
            getWindow().setEnterTransition(fade);
        }
    }
    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(getString(R.string.caracters_min));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.invalid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError(getString(R.string.short_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onSignupFailed(String ErrorMessage) {
        Toast.makeText(getBaseContext(), ErrorMessage, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }
    @SuppressWarnings("unchecked") void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        startActivity(i, transitionActivityOptions.toBundle());
    }
}
