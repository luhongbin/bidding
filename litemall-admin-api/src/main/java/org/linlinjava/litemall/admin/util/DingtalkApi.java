package org.linlinjava.litemall.admin.util;

import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

public class DingtalkApi {

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=";
    public static String MESSAGEHOOK_TOKEN = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=";
    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken?appkey=&appsecret=-";
    public static String url_post_file = "https://oapi.dingtalk.com/media/upload?access_token=";
    public static String down_load_url = "https://oapi.dingtalk.com/robot/send?access_token=";

    public static void robotSend(String textMsg) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(WEBHOOK_TOKEN);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
//            String textMsg ="{ \"msgtype\": \"text\", \"text\": {\"content\": \"我就是我, 是不一样的烟火\"}}";
        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        }
    }


    public static String get_media_id(String file_name,String token) throws Exception {
        String sTestsetFile=file_name;
        String sURL = url_post_file + token + "&type=file";
        //CloseableHttpClient意思是：可关闭的
        CloseableHttpClient httpClient = HttpClients.createDefault();//1、创建实例
        HttpPost uploadFile = new HttpPost(sURL);//2、创建请求

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("text", "111", ContentType.TEXT_PLAIN);//传参
        builder.setCharset(Charset.forName("utf8"));//设置请求的编码格式
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//设置浏览器兼容模式

        // 把文件加到HTTP的post请求中
        File f = new File(sTestsetFile);
        builder.addBinaryBody(
                "media",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);//对于HttpPost对象而言，可调用setEntity(HttpEntity entity)方法来设置请求参数。
        CloseableHttpResponse response = httpClient.execute(uploadFile);//3、执行
        HttpEntity responseEntity = response.getEntity();//4、获取实体

        Header header=responseEntity.getContentType();
        //打印内容
        String sResponse= EntityUtils.toString(responseEntity, "UTF-8");//5、获取网页内容，并且指定编码 multipart/form-data
        System.out.println("Post 返回结果"+sResponse);

        Gson gs = new Gson();
        Map<String,String> map = gs.fromJson(sResponse, Map.class);
        return map.get("media_id");
    }

    public static String asyncsend(String textMsg) throws Exception {
        String token = DingtalkApi.gettoken();

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost =new HttpPost(MESSAGEHOOK_TOKEN + token);
        httppost.addHeader("Content-Type","application/json; charset=utf-8");
        StringEntity se =new StringEntity(textMsg,"utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String result= EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println("asyncsend:"+result);
            return "asyncsend:"+result;
        }
        return "asyncsend 没消息 可能报错了";
    }
    public static String send_file(String downFileUrl, String downFilename) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget =new HttpGet(downFileUrl);
        httpget.addHeader("Content-Type","application/json; charset=utf-8");

        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String result= EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println(result);
            Gson gs = new Gson();
            Map<String,String> map = gs.fromJson(result, Map.class);
            System.out.println(map.get("access_token"));
            return map.get("access_token");
        }
        return null;
    }

    public static String gettoken() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget =new HttpGet(GET_ACCESS_TOKEN_URL);
        httpget.addHeader("Content-Type","application/json; charset=utf-8");
        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String result= EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println(result);
            Gson gs = new Gson();
            Map<String,String> map = gs.fromJson(result, Map.class);
            System.out.println(map.get("access_token"));
            return map.get("access_token");
        }
        return null;
    }
    //    public static String send_file(String media_id, String msg,String token) throws Exception {
//        HttpClient httpclient = HttpClients.createDefault();
//
//        String down_load_url="https://oapi.dingtalk.com/media/downloadFile?access_token="+token+"&media_id="+media_id;
//        HttpGet httpget =new HttpGet(down_load_url);
//        httpget.addHeader("Content-Type","application/json; charset=utf-8");
//        String msgInfo = msg.replace("http://61.175.134.14:8190/wx/storage/fetch/",down_load_url);
//        StringEntity se =new StringEntity(msgInfo,"utf-8");
//        HttpResponse response = httpclient.execute(httpget);
//        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
//            String result= EntityUtils.toString(response.getEntity(),"utf-8");
//            System.out.println(result);
//            Gson gs = new Gson();
//            Map<String,String> map = gs.fromJson(result, Map.class);
//            System.out.println("fromJson"+map.get("errmsg"));
//            return map.get("errmsg");
//        }
//        return null;
//    }
////            '''通过群机器人发送链接，达到点击链接下载文件的目的'''
//    public static void  send_file_robot(String url_robot, String msg) throws Exception  {
////        '' '依次为文件路径，Webhook地址,需要发送的消息,钉钉安全设置的自定义关键字' ''
//        String token = DingtalkApi.gettoken();
//
//        send_file(get_media_id(url_robot,token), url_robot, msg, token);
//    }
}
