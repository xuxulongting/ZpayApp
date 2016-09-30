package com.spreadtrum.iit.zpayapp.register_login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spreadtrum.iit.zpayapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by SPREADTRUM\ting.long on 16-9-29.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnRegister;
    private EditText editTextUsername;
    private EditText editTextPwd;
    private EditText editTextRepwd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);
        btnRegister = (Button) findViewById(R.id.id_btn_register);
        editTextUsername = (EditText) findViewById(R.id.id_et_username);
        editTextPwd = (EditText) findViewById(R.id.id_et_pwd);
        editTextRepwd = (EditText) findViewById(R.id.id_et_repwd);
        btnRegister.setOnClickListener(this);
        btnBack = (ImageButton) findViewById(R.id.id_iv_arrowback);
        btnBack.setOnClickListener(this);
        editTextUsername.addTextChangedListener(new TextWatcher() {
            int cou = 0;
            int selectionEnd = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                cou = before + count;
                String editable = editTextUsername.getText().toString();
                String str = LoginCommon.stringFilter(editable); //过滤特殊字符
                if (!editable.equals(str)) {
                    editTextUsername.setText(str);
                }
                editTextUsername.setSelection(editTextUsername.length());
                cou = editTextUsername.length();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (cou > LoginCommon.maxUsernameLength) {
                    selectionEnd = editTextUsername.getSelectionEnd();
                    editable.delete(LoginCommon.maxUsernameLength, selectionEnd);
                }
            }
        });
        editTextPwd.addTextChangedListener(new TextWatcher() {
            int cou = 0;
            int selectionEnd = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cou = i1+i2;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cou>LoginCommon.maxPwdLength){
                    selectionEnd = editTextPwd.getSelectionEnd();
                    editable.delete(LoginCommon.maxPwdLength,selectionEnd);
                }
            }
        });

        editTextRepwd.addTextChangedListener(new TextWatcher() {
            int cou;
            int selectionEnd;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cou = i1+i2;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cou>LoginCommon.maxRepwdLength){
                    selectionEnd = editTextRepwd.getSelectionEnd();
                    editable.delete(LoginCommon.maxRepwdLength,selectionEnd);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_register:
                //去掉软键盘
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String userNameStr = editTextUsername.getText().toString();
                String pwdStr = editTextPwd.getText().toString();
                String repwdStr = editTextRepwd.getText().toString();
                if(userNameStr.isEmpty() || pwdStr.isEmpty() ||
                        repwdStr.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"注册信息不完整",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!(pwdStr.equals(repwdStr))){
                    Toast.makeText(RegisterActivity.this,"两次输入密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                userRegister(userNameStr,pwdStr);
                break;
            case R.id.id_iv_arrowback:
                //实现返回
                finish();
                break;
        }
    }



    public void userRegister(String userName, String pwd){
        final AlertDialog dialog = new AlertDialog.Builder(this).setMessage("正在提交信息，请稍候...").show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonObject.put("version","1.0");
            jsonObject.put("loginName",userName);
            jsonObject.put("loginPwd",pwd);
            jsonObject.put("checkCode","123456");
            String params = jsonObject.toString();
            jsonRequest.put("params",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            String result = response.getString("result");
                            if(result.equals("0")){
                                String errorCode = response.getString("errorCode");
                                String errorMsg = response.getString("errorMsg");

                                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();
                            }
                            else {
                                String userId = response.getString("userId");
                                String token = response.getString("token");
                                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(request);

    }

    public static String REGISTER_URL="http://10.0.64.137:7788/app/register";
}
