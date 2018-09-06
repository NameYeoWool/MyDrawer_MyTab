package kr.ac.kumho.mydrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity{

    //UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private EditText mNickNameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView2 = (EditText) findViewById(R.id.password2);
        mNickNameView = (EditText) findViewById(R.id.nickname);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    //-------------------------------------------------------------------------

    private void attemptLogin(){
        //Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordView2.setError(null);
        mNickNameView.setError(null);

        //Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPasswordView2.getText().toString();
        String nickname = mNickNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        //Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel =true;
        }

        if (!TextUtils.isEmpty(password2) && !isPasswordValid(password2)) {
            mPasswordView2.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView2;
            cancel =true;
        }

        if (password.equals(password2) == false) {
            mPasswordView.setError(getString(R.string.error_different_password));
            mPasswordView2.setError(getString(R.string.error_different_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(nickname) && !isNicknameValid(nickname)) {
            mNickNameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNickNameView;
            cancel =true;
        }

        if(cancel){
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            SessionManager.getsInstance(getApplicationContext()).
                    Signup(email, password, password2, nickname);
            setResult(RESULT_OK);
            finish();
        }



    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }


    private boolean isPasswordValid(String password){
        return password.length() > 0 ;
    }

    private boolean isNicknameValid(String password){
        return password.length() > 0;
    }

}