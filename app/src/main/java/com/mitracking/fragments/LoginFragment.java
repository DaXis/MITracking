package com.mitracking.fragments;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    private float mHeightPixels, mWidthPixels;

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
        user.setText("alejandro.martinez@demo.com.mx");

        pass = (EditText)rootView.findViewById(R.id.pass);
        //pass.setText("p4ssw0rd");

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
            //if(pass.getText().length() > 0){
                return true;
            /*} else {
                Singleton.showCustomDialog(getFragmentManager(), "¡Atención!",
                        "No debe dejar el campo de password vacio.", "Aceptar", 0);
                return false;
            }*/
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
            json.put("UserPwsdID", "password");
            json.put("MobileType", getDeviceId());
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

    private String getDeviceId(){
        double size = getScreenSize();
        /*if(size >= 7)
            return "0003";
        else
            return "0004";*/
        if(size >= 7) {
            Singleton.savePreferences("device_type", "android.tablet");
            return "android.tablet";
        }else {
            Singleton.savePreferences("device_type", "android.phone");
            return "android.phone";
        }
    }

    private double getScreenSize(){
        setRealDeviceSizeInPixels();
        DisplayMetrics dm = new DisplayMetrics();
        Singleton.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels/dm.xdpi,2);
        double y = Math.pow(mHeightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);
        return screenInches;
    }

    private void setRealDeviceSizeInPixels() {
        WindowManager windowManager = Singleton.getCurrentActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        mWidthPixels = displayMetrics.widthPixels;
        mHeightPixels = displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
    }

}
