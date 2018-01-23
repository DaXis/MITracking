package com.mitracking.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mitracking.MainActivity;
import com.mitracking.R;
import com.mitracking.Singleton;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private int lay;
    private EditText user, pass;
    private Button login_btn;
    private TextView forgot;
    private LoginObj loginObj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        lay = bundle.getInt("lay");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("setCurrentFragment", this.getClass().getSimpleName());
        Singleton.setCurrentFragment(this);
        Singleton.getToolbar().setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login, container, false);

        user = (EditText)rootView.findViewById(R.id.user);
        user.setText("user_1_test");

        pass = (EditText)rootView.findViewById(R.id.pass);
        pass.setText("p4ssw0rd");

        login_btn = (Button) rootView.findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

        forgot = (TextView) rootView.findViewById(R.id.forgot);
        forgot.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login_btn:
                if(validations()){
                    Singleton.hideKeyboard(this.getView());
                    initConnection();
                }
                break;
            case R.id.forgot:
                break;
        }
    }

    private boolean validations(){
        if(user.getText().length() > 0){
            if(pass.getText().length() > 0){
                return true;
            } else {
                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                        "No debe dejar el campo de password vacio.", "Aceptar", 0);
                return false;
            }
        } else {
            Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                    "No debe dejar el campo de usuario vacio.", "Aceptar", 0);
            return false;
        }
    }

    private void initConnection(){
        Singleton.showLoadDialog(getFragmentManager());
        JSONObject TrackUserInfo = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            json.put("SAASName", Constants.SAASName);
            json.put("TenantID", Constants.TenantID);
            json.put("UserLoginID", user.getText().toString());
            json.put("UserPwsdID", pass.getText().toString());
            json.put("MobileType", "0004");
            TrackUserInfo.put("trackUserInfo", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] objs = new Object[]{Constants.URL_ValidateUser, 1, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }

    public void getResponse(String arg){
        Log.d("response login", arg);
        try {
            JSONObject jsonObject = new JSONObject(arg);
            JSONObject ValidateUserResult = jsonObject.getJSONObject("ValidateUserResult");
            if(ValidateUserResult.getString("ServerStatus").equals("DONE") &&
                    ValidateUserResult.getString("IsValidUser").equals("TRUE")){
                loginObj = new LoginObj();
                loginObj.AlignmentID = ValidateUserResult.getString("AlignmentID");
                loginObj.AlignmentName = ValidateUserResult.getString("AlignmentName");
                loginObj.EmployeeID = ValidateUserResult.getString("EmployeeID");
                loginObj.EmployeeName = ValidateUserResult.getString("EmployeeName");
                loginObj.IsValidUser = ValidateUserResult.getString("IsValidUser");
                loginObj.IsValidUserReason = ValidateUserResult.getString("IsValidUserReason");
                loginObj.ServerErrorCode = ValidateUserResult.getString("ServerErrorCode");
                loginObj.ServerErrorDetail = ValidateUserResult.getString("ServerErrorDetail");
                loginObj.ServerStatus = ValidateUserResult.getString("ServerStatus");
                loginObj.SessionID = ValidateUserResult.getString("SessionID");

                Singleton.savePreferences(Constants.JSON_LOGIN, arg);
                Singleton.savePreferences(Constants.FLAG_LOGIN, true);
                Singleton.savePreferences(Constants.UserLoginID_TAG, user.getText().toString());
                Singleton.setLoginObj(loginObj);
                Singleton.dissmissLoad();
                ((MainActivity)Singleton.getCurrentActivity()).initMainFragment(loginObj);
            } else {
                Singleton.dissmissLoad();
                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!", "Error en el login", "Aceptar", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
            Singleton.dissmissLoad();
        }
    }

}
