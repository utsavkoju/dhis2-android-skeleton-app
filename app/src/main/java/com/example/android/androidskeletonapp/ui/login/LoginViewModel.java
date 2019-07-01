package com.example.android.androidskeletonapp.ui.login;

import android.util.Patterns;

import com.example.android.androidskeletonapp.R;

import org.hisp.dhis.android.core.user.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Single;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    LoginViewModel() {
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public Single<User> login(String username, String password, String serverUrl) {
        return setServerUrlAndLogin(username, password, serverUrl)
                .doOnSuccess(user -> {
                    if (user != null) {
                        loginResult.postValue(new LoginResult(user));
                    } else {
                        loginResult.postValue(new LoginResult(R.string.login_failed));
                    }
                }).doOnError(throwable -> {
                    loginResult.postValue(new LoginResult(R.string.login_failed));
                    throwable.printStackTrace();
                });
    }

    public Single<User> setServerUrlAndLogin(String username, String password, String serverUrl) {
        // TODO Set server url and login

        return null;
    }

    void loginDataChanged(String serverUrl, String username, String password) {
        if (!isServerUrlValid(serverUrl)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_server_url, null, null));
        } if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isServerUrlValid(String serverUrl) {
        if (serverUrl == null) {
            return false;
        }
        return Patterns.WEB_URL.matcher(serverUrl).matches();
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
