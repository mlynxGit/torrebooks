package com.maghribpress.torrebook.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.classes.ApiTokenObject;
import com.maghribpress.torrebook.classes.Functions;
import com.maghribpress.torrebook.classes.SharedPrefManager;
import com.maghribpress.torrebook.network.GetBooksDataService;
import com.maghribpress.torrebook.network.RetrofitInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maghribpress.torrebook.classes.Functions.isEmailValid;


/**
 * Created by gopalsamy.k on 20/3/17.
 */

public class LoginActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private TextView _signupLink;
    //private TextView _signupasguestLink;
    //private View mProgressView;
    GetBooksDataService mApiService;
    Context mcontext;
    SharedPrefManager sharedPref;
    AppCompatButton signinButton;
    EditText txtUsername;
    EditText txtPassword;
    LoginButton loginButton;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 555;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        sharedPref = new SharedPrefManager(this);
        mApiService = RetrofitInstance.getRetrofitInstance().create(GetBooksDataService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }
        signinButton = (AppCompatButton)findViewById(R.id.signinButton);
        mcontext = this;
        // Set up the login form.
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        txtUsername = (EditText) findViewById(R.id.txt_username);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        _signupLink = (TextView) findViewById(R.id.btn_signup);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        TextView logoText = (TextView) findViewById(R.id.logo_text);
        Typeface mtypeFace = Typeface.createFromAsset(mcontext.getAssets(),
                "fonts/caviardreams.ttf");
        logoText.setTypeface(mtypeFace);
        _signupLink.setPaintFlags(_signupLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        });
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        //FacebookSdk.sdkInitialize(getApplicationContext());
        Log.d("FACEBOOKACCESS", "key:" + FacebookSdk.getApplicationSignature(this));
        //printHashKey(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String token = loginResult.getAccessToken().getToken();
                        callbackLogin("facebook",token);
                        Log.d("FACEBOOKACCESS",token);
                    }

                    @Override
                    public void onCancel() {
                        showloginFailed("Login with Facebook was Canceled");
                        Log.d("FACEBOOKACCESS","Canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showloginFailed("Error Login with Facebook !");
                        Log.d("FACEBOOKACCESS","Error");
                    }
                });

        //Google Sign-in

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("363157420747-gj570csl74b6eorqpj9vf61c9rn2ei8h.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void callbackLogin(String provider, String token) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Login...");
        progressDialog.show();
        Call<ResponseBody> call = mApiService.callback(provider,token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.equals(null)) {
                    if(response.isSuccessful()) {
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.has("success")){
                                if(jsonRESULTS.getBoolean("success")) {
                                    JSONObject jsonToken = jsonRESULTS.getJSONObject("data");
                                    String token = jsonToken.getString("token");
                                    String name = jsonToken.getString("name");
                                    String email = jsonToken.getString("email");
                                    String avatar = jsonToken.getString("avatar");
                                    sharedPref.saveToken(token);
                                    sharedPref.saveName(name);
                                    sharedPref.saveEmail(email);
                                    sharedPref.saveLoginState(true);
                                    sharedPref.setDisclaimerState(false);
                                    sharedPref.setAvatar(avatar);
                                    ApiTokenObject.getInstance().setToken(token);
                                    ApiTokenObject.getInstance().setmEmail(email);
                                    ApiTokenObject.getInstance().setmUsername(name);
                                    ApiTokenObject.getInstance().setAvatar(avatar);
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.putExtra("usrname", name);
                                    i.putExtra("usrmail", email);
                                    progressDialog.dismiss();
                                    startActivity(i);
                                    finish();
                                }else {
                                    if (jsonRESULTS.has("error")){
                                        String errorMessage = jsonRESULTS.getString("error");
                                        showloginFailed(errorMessage);
                                    }
                                }
                            } else {
                                // Jika login gagal
                                showloginFailed(getString(R.string.unable_facebook_login));
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }catch (IOException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }else{
                        showloginFailed(getString(R.string.unable_facebook_login));
                    }
                }else {
                    progressDialog.dismiss();
                    showloginFailed(getString(R.string.unable_facebook_login));
                }
                // generateNewsList(response.body().getNewslist());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                showloginFailed(getString(R.string.login_attempt_failed));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            //performBackEnd signup

        }
        //updateUI(account);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @SuppressWarnings("unchecked") void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        startActivity(i, transitionActivityOptions.toBundle());
    }
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(500);
            getWindow().setExitTransition(slide);
        }
    }
    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private void attemptLogin() {
        // Reset errors.
        txtUsername.setError(null);
        txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtUsername.setError(getString(R.string.error_field_required));
            focusView = txtUsername;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtUsername.setError(getString(R.string.error_invalid_email));
            focusView = txtUsername;
            cancel = true;
        }else if (!Functions.isEmailValid(email)) {
            focusView = txtUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Login...");
            progressDialog.show();
           /* mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
            Call<ResponseBody> call = mApiService.login(email,password);

            Log.wtf("URL Called", call.request().url() + "");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(!response.equals(null)) {
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
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        i.putExtra("usrname", name);
                                        i.putExtra("usrmail", email);
                                        startActivity(i);
                                        finish();
                                    }else {
                                        if (jsonRESULTS.has("error")){
                                            String errorMessage = jsonRESULTS.getString("error");
                                            showloginFailed(errorMessage);
                                        }
                                    }
                                } else {
                                    // Jika login gagal
                                    showloginFailed(getString(R.string.failed_login_account_credentials));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }catch (IOException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }else{
                            showloginFailed(getString(R.string.failed_login_account_credentials));
                        }
                    }else {
                        showloginFailed(getString(R.string.failed_login_account_credentials));
                    }
                    // generateNewsList(response.body().getNewslist());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showloginFailed(getString(R.string.login_attempt_failed));
                }
            });
        }
    }

    private void showloginFailed(String errorMsg) {
        progressDialog.dismiss();
        Toast.makeText(mcontext, errorMsg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account!=null) {
                Log.w("GOOGLESIGNIN", account.getIdToken());
                callbackLogin("google",account.getIdToken());
            }
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLESIGNIN", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

}
