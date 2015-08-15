package com.mo.httpclient_get_post;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mo.httpclient_get_post.tools.ServerTools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SUCCESS = 0;
    private static final int FAILE = 1;
    private static final int NET_ERROR = 3;
    private static final String TAG = "MainActivity";
    EditText et_username;
    EditText et_password;
    TextView show_result;
    String username;
    String password;

    final String path = "http://188.188.7.85/Android_Server/Login";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what) {
                case SUCCESS:
                    String data = (String) msg.obj;
                    show_result.setText(data);
                    break;
                case FAILE:
                    Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    Toast.makeText(MainActivity.this, "网络出现异常", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        show_result = (TextView) findViewById(R.id.show_result);

        username = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();
    }

    public void login(View view) {
        username = et_username.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //使用Apche公司提供的API get方式的请求服务器
//        new Thread_get().start();


        //使用Apche公司提供的API post方式请求服务器
        new Thread_post().start();

    }

    //Apche公司提供的，这是 面向对象API的post方式请求服务器端
    class Thread_post extends Thread {
        @Override
        public void run() {
            try {

                //1.创建出来一个httpclient对象，http客户端
                HttpClient client = new DefaultHttpClient();

                //2.定义一个httpPost对象
                HttpPost request = new HttpPost(path);

                //3.定义传递给服务器的键值对数据
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("username", username));
                parameters.add(new BasicNameValuePair("password", password));

                //4.定义一个url编码过的form表单实体
                //要对中文数据进行编码
                HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");

                //5.给服务器传输的数据实体
                request.setEntity(entity);

                //6.执行一个请求
                HttpResponse res = client.execute(request);

                //7.获取状态码
                int code = res.getStatusLine().getStatusCode();

                if (code == 200) {
                   //获取响应对象中包含的数据实体
                    HttpEntity entity1 = res.getEntity();

                    //获取实体里面输入流
                    InputStream in = entity1.getContent();

                    //使用工具类将流资源转化成数据
                    String data = ServerTools.getInfo(in);

                    Log.i(TAG, "data:---" + data);
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = SUCCESS;
                    ms.obj = data;
                    handler.sendMessage(ms);
                } else {
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = FAILE;
                    handler.sendMessage(ms);
                }

            } catch (Exception e) {

                //使用消息处理机制，将数据传递给主线程
                Message ms = new Message();
                ms.what = NET_ERROR;
                handler.sendMessage(ms);
                e.printStackTrace();
            }
        }
    }

    //Apche公司提供的API,这是 面向对象的get方式请求服务器端
    class Thread_get extends Thread {
        @Override
        public void run() {
            try {
                String getPath = path +
                        "?username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                //1.创建出来一个HttpClient对象，http客户端
                HttpClient client = new DefaultHttpClient();

                //2.定义一个httpget请求
                HttpGet request = new HttpGet(getPath);

                //3.执行一个请求
                HttpResponse res = client.execute(request);

                //4.获取状态码
                int code = res.getStatusLine().getStatusCode();

                if (code == 200) {
                    //5.获得响应对象中包含的数据实体对象
                    HttpEntity entity = res.getEntity();

                    //6.获取实体里面的输入流
                    InputStream is = entity.getContent();


                    //表示连接服务器成功返回信息
                    String data = ServerTools.getInfo(is);

                    Log.i(TAG, "data:---" + data);
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = SUCCESS;
                    ms.obj = data;
                    handler.sendMessage(ms);
                } else {
                    //使用消息处理机制，将数据传递给主线程
                    Message ms = new Message();
                    ms.what = FAILE;
                    handler.sendMessage(ms);
                }

            } catch (Exception e) {

                //使用消息处理机制，将数据传递给主线程
                Message ms = new Message();
                ms.what = NET_ERROR;
                handler.sendMessage(ms);
                e.printStackTrace();
            }
        }
    }

}

