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

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=45560b7a340db69b4aedee8d4508fce642fb3a04e6735fdc285c345d19fcf285";
    public static String MESSAGEHOOK_TOKEN = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=";
    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken?appkey=dingtxmp9bvomxhtvj8q&appsecret=NhhTE2Timl4cB0_gGiTQU0fEkFWXZjG6eJMG-2zVZmdQrp4YRXN6f4bXscN0t07g";
    public static String url_post_file = "https://oapi.dingtalk.com/media/upload?access_token=";
    public static String down_load_url = "https://oapi.dingtalk.com/robot/send?access_token=45560b7a340db69b4aedee8d4508fce642fb3a04e6735fdc285c345d19fcf285";
    public static String getUserInfo_url = "https://oapi.dingtalk.com/user/get?access_token=%s&userid=%s";
    public static String getDeptInfo_url = "https://oapi.dingtalk.com/department/get?access_token=%s&id=%d";

    public static String getDeptInfo(Integer Id) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String url = String.format(getDeptInfo_url,DingtalkApi.gettoken(), Id);
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Content-Type", "application/json; charset=utf-8");

        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println(result);
            return result;
        }
        return url;
    }
    public static String getUserInfo(String userId) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        String url = String.format(getUserInfo_url,DingtalkApi.gettoken(), userId);
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Content-Type", "application/json; charset=utf-8");

        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println(result);
            return result;
        }
        return url;
    }
    public static void robotSend(String textMsg) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(WEBHOOK_TOKEN);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
//            String textMsg ="{ \"msgtype\": \"text\", \"text\": {\"content\": \"????????????, ?????????????????????\"}}";
        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
//            System.out.println(result);
        }
    }


    public static String get_media_id(String file_name,String token) throws Exception {
        String sTestsetFile=file_name;
        String sURL = url_post_file + token + "&type=file";
        //CloseableHttpClient????????????????????????
        CloseableHttpClient httpClient = HttpClients.createDefault();//1???????????????
        HttpPost uploadFile = new HttpPost(sURL);//2???????????????

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("text", "111", ContentType.TEXT_PLAIN);//??????
        builder.setCharset(Charset.forName("utf8"));//???????????????????????????
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//???????????????????????????

        // ???????????????HTTP???post?????????
        File f = new File(sTestsetFile);
        builder.addBinaryBody(
                "media",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);//??????HttpPost????????????????????????setEntity(HttpEntity entity)??????????????????????????????
        CloseableHttpResponse response = httpClient.execute(uploadFile);//3?????????
        HttpEntity responseEntity = response.getEntity();//4???????????????

        Header header=responseEntity.getContentType();
        //????????????
        String sResponse= EntityUtils.toString(responseEntity, "UTF-8");//5?????????????????????????????????????????? multipart/form-data
//        System.out.println("Post ????????????"+sResponse);

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
        return "asyncsend ????????? ???????????????";
    }
    public static String send_file(String downFileUrl, String downFilename) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget =new HttpGet(downFileUrl);
        httpget.addHeader("Content-Type","application/json; charset=utf-8");

        HttpResponse response = httpclient.execute(httpget);
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
            String result= EntityUtils.toString(response.getEntity(),"utf-8");
//            System.out.println(result);
            Gson gs = new Gson();
            Map<String,String> map = gs.fromJson(result, Map.class);
//            System.out.println(map.get("access_token"));
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
//            System.out.println(result);
            Gson gs = new Gson();
            Map<String,String> map = gs.fromJson(result, Map.class);
//            System.out.println(map.get("access_token"));
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
////            '''????????????????????????????????????????????????????????????????????????'''
//    public static void  send_file_robot(String url_robot, String msg) throws Exception  {
////        '' '????????????????????????Webhook??????,?????????????????????,???????????????????????????????????????' ''
//        String token = DingtalkApi.gettoken();
//
//        send_file(get_media_id(url_robot,token), url_robot, msg, token);
//    }
}
