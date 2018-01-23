package com.mitracking;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mitracking.fragments.LoginFragment;
import com.mitracking.fragments.MainFragment;
import com.mitracking.objs.LoginObj;
import com.mitracking.utils.ConnectToServer;
import com.mitracking.utils.Connectivity;
import com.mitracking.utils.Constants;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FrameLayout mainContent;
    private LoginFragment loginFragment;
    private MainFragment mainFragment;
    private LoginObj loginObj;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        mainContent = (FrameLayout)findViewById(R.id.mainContent);
        Singleton.setCurrentActivity(this);
        Singleton.setFragmentManager(getSupportFragmentManager());

        setToolbar();
        initFragments();

        if(!Singleton.getSettings().getBoolean(Constants.FLAG_LOGIN, false))
            initLoginFragment();
        else {
            if(Singleton.getSettings().getString(Constants.JSON_LOGIN, "").length() > 0)
                getResponse(Singleton.getSettings().getString(Constants.JSON_LOGIN, ""));
            else
                initLoginFragment();
        }
    }

    private void setToolbar() {
        ImageView action_img = (ImageView)findViewById(R.id.back_btn);
        Singleton.setActionImg(action_img);
        action_img.setOnClickListener(this);

        ImageView profile = (ImageView)findViewById(R.id.profile);
        Singleton.setProfileImg(profile);
        profile.setOnClickListener(this);

        ImageView info = (ImageView)findViewById(R.id.info);
        Singleton.setInfoImg(info);
        info.setOnClickListener(this);

        ImageView addBtn = (ImageView)findViewById(R.id.addBtn);
        Singleton.setAddImg(addBtn);
        addBtn.setOnClickListener(this);

        ImageView menu = (ImageView)findViewById(R.id.menu_btn);
        Singleton.setMenuImg(menu);
        menu.setOnClickListener(this);

        TextView action_txt = (TextView)findViewById(R.id.action_txt);
        Singleton.setActionTxt(action_txt);

        LinearLayout toolbar = (LinearLayout)findViewById(R.id.toolbar);
        Singleton.setToolbar(toolbar);
    }

    private void initFragments(){
        loginFragment = new LoginFragment();
        mainFragment = new MainFragment();
    }

    private void removeFragments(){
        if(Singleton.getCurrentFragment() != null){
            Log.d("fragment remove", Singleton.getCurrentFragment().getClass().toString());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(Singleton.getCurrentFragment()).commit();
        }
    }

    private void initLoginFragment(){
        if(Singleton.getCurrentFragment() != loginFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            if(loginFragment.getArguments() == null)
                loginFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), loginFragment)
                    .addToBackStack(TAG)
                    .commit();
        }
    }

    public void initMainFragment(LoginObj loginObj){
        if(Singleton.getCurrentFragment() != mainFragment){
            removeFragments();
            Bundle bundle = new Bundle();
            bundle.putInt("lay", mainContent.getId());
            bundle.putSerializable("loginObj", loginObj);
            if(mainFragment.getArguments() == null)
                mainFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainContent.getId(), mainFragment).addToBackStack(TAG).commit();
        }
    }

    /*private void initConnection(){
        Singleton.showLoadDialog(getSupportFragmentManager());
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
        Object[] objs = new Object[]{Constants.URL_ValidateUser, 0, this, TrackUserInfo};
        ConnectToServer connectToServer = new ConnectToServer(objs);
    }*/

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
                Singleton.setLoginObj(loginObj);
                Singleton.dissmissLoad();
                initMainFragment(loginObj);
            } else {
                Singleton.dissmissLoad();
                Singleton.showCustomDialog(getSupportFragmentManager(), "¡Atención!", jsonObject.getString("mensaje"), "Aceptar", 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Singleton.savePreferences(Constants.FLAG_LOGIN, false);
            Singleton.dissmissLoad();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
