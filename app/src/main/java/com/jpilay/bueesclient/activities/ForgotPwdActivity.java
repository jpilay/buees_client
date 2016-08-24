package com.jpilay.bueesclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.models.User;
import com.jpilay.bueesclient.network.Network;
import com.jpilay.bueesclient.util.Controller;
import com.jpilay.bueesclient.util.Ui;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class ForgotPwdActivity extends AppCompatActivity {

     /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ForgotPwdTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private View mProgressView;
    private View mRecoveryFormView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button mUserRecoveryButton = (Button) findViewById(R.id.user_recovery_button);
        mUserRecoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Ui.hideSoftKeyboard(getApplicationContext(), view);
                attemptForgotPwd();

            }
        });

        mRecoveryFormView = findViewById(R.id.recovery_form);
        mProgressView = findViewById(R.id.recovery_progress);
    }

    private void attemptForgotPwd() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (!Network.checkInternetConnection(getApplicationContext())) {
                Toast.makeText(getApplicationContext()
                        , R.string.network_internet_disconnect
                        , Toast.LENGTH_LONG).show();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);
                mAuthTask = new ForgotPwdTask(username);
                mAuthTask.execute((Void) null);
            }
        }
    }


    private boolean isUsernameValid(String username) {
        return username.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRecoveryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecoveryFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecoveryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRecoveryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ForgotPwdTask extends AsyncTask<Void, Void, Object> {

        private final String mUsername;

        ForgotPwdTask(String username) {
            mUsername = username;
        }

        @Override
        protected Object doInBackground(Void... params) {

            User user = null;
            try {

                Controller controller = new Controller(getApplicationContext());
                JSONObject jsonObj = controller.recoveryPassword(mUsername);

                if (jsonObj != null) {
                    user = new User(
                            jsonObj.getString("username"),
                            jsonObj.getString("email"),
                            jsonObj.getString("group")
                    );
                }

            } catch (JSONException e) {
                Log.e("Buees", e.getMessage());
            }
            return user;
        }

        @Override
        protected void onPostExecute(final Object object) {
            mAuthTask = null;
            showProgress(false);

            if (object != null) {
                Toast.makeText(ForgotPwdActivity.this,"La nueva contraseña ha sido enviada al correo",Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(ForgotPwdActivity.this,"Usuario incorrecto",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

