package com.jpilay.bueesclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jpilay.bueesclient.R;
import com.jpilay.bueesclient.models.User;
import com.jpilay.bueesclient.network.Network;
import com.jpilay.bueesclient.util.Controller;
import com.jpilay.bueesclient.util.JSONParser;
import com.jpilay.bueesclient.util.Ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignUpTask mAuthTask = null;
    private UserGroupTask mAuthTask2 = null;

    // UI references.
    private EditText mUsernameView, mPasswordView, mEmailView;
    private View mProgressView;
    private View mSignUpFormView;
    private Spinner mGroupView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set up the register form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignup();
                    return true;
                }
                return false;
            }
        });

        mGroupView = (Spinner) findViewById(R.id.group);

        Button mUserSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mUserSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Ui.hideSoftKeyboard(getApplicationContext(), view);
                attemptSignup();
            }
        });

        mSignUpFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);

        if (!Network.checkInternetConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext()
                    , R.string.network_internet_disconnect
                    , Toast.LENGTH_LONG).show();

        } else {
            mAuthTask2 = new UserGroupTask();
            mAuthTask2.execute((Void) null);
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mEmailView.setError(null);
        //mGroupView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();
        String group = mGroupView.getSelectedItem().toString().trim();

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

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid select gorup
        if (TextUtils.isEmpty(group)) {
            focusView = mGroupView;
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
                mAuthTask = new UserSignUpTask(username, password,email,group);
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

    private boolean isEmailValid(String email) {
        try {
            // Compiles the given regular expression into a pattern.
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            // Match the given input against this pattern
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous signup task used to create
     * the user.
     */
    public class UserSignUpTask extends AsyncTask<Void, Void, Object> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;
        private final String mGroup;

        UserSignUpTask(String username, String password,
                       String email, String group) {
            mUsername = username;
            mPassword = password;
            mEmail = email;
            mGroup = group;
        }

        @Override
        protected Object doInBackground(Void... params) {
            User user = null;
            try {

                Controller controller = new Controller(getApplicationContext());
                JSONObject jsonObj = controller.signup(mUsername, mPassword, mEmail, mGroup);

                if (jsonObj != null) {
                    user = new User(
                            jsonObj.getString("username"),
                            jsonObj.getString("email"),
                            jsonObj.getString("group")
                    );
                }

            } catch (JSONException e) {
                Log.e("Buees", e.getMessage());
            } catch (Exception e){
                Log.e("Buees", e.getMessage());
            }
            return user;
        }

        @Override
        protected void onPostExecute(final Object object) {
            mAuthTask = null;
            showProgress(false);

            if (object != null) {
                User user = (User) object;
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this,"El nombre de usuario ya se encuentra registrado",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserGroupTask extends AsyncTask<Void, Void, Object> {
        private ProgressDialog mDialog;

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<String> groups = null;

            try {
                Controller controller = new Controller(getApplicationContext());
                JSONArray jsonArray = controller.groups();

                if(jsonArray != null){

                    if (jsonArray.length()!=0){
                        JSONObject jsonObject;
                        groups = new ArrayList<>();

                        for (int i=0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            groups.add(jsonObject.getString("name"));
                        }

                    }

                }

            } catch (JSONException e) {
                Log.e("Buees", e.getMessage());
            } catch (Exception e){
                Log.e("Buees", e.getMessage());
            }
            return groups;

        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(SignUpActivity.this,
                    "",
                    "Cargando...",
                    true, false);

        }

        @Override
        protected void onPostExecute(final Object object) {
            mAuthTask2 = null;
            mDialog.dismiss();

            if (object != null) {
                ArrayList<String> groups = (ArrayList<String>) object;
                ArrayAdapter<String> groupsAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, groups);
                //setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mGroupView.setAdapter(groupsAdapter);

            } else {
                Toast.makeText(SignUpActivity.this,"No se encontraron roles registrados",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask2 = null;
        }
    }
}

