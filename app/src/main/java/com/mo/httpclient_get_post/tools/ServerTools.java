package com.mo.httpclient_get_post.tools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/8/15.
 */
public class ServerTools {

    //从服务端获取流数据进行转化成文本文件
    public static String getInfo(InputStream in) {

        //将数据流写在内存中
        ByteArrayOutputStream raf = new ByteArrayOutputStream();
        String data = null;

        try{
            byte[] bt = new byte[1024];
            int len =0 ;
            while((len = in.read(bt)) != -1){
                raf.write(bt,0,len);
            }

           data = raf.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }
}
