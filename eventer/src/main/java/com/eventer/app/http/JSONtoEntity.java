package com.eventer.app.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.entity.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuNana on 2016/4/22.
 */
public class JSONtoEntity {
    public static List<Event> EventListJson(JSONArray json){

        List<Event> list = new ArrayList<>();
        if(json != null && json.size() > 0) {

            int size = json.size();
            for(int i=0 ; i<size;i++){
                try{
                    JSONObject recvJs = (JSONObject)json.get(i);
                    String provider = recvJs.getString("cEvent_provider");
                    String content = recvJs.getString("cEvent_content");
                    String theme = recvJs.getString("cEvent_theme");
                    String place = recvJs.getString("cEvent_place");
                    String name = recvJs.getString("cEvent_name");
                    String time = recvJs.getString("cEvent_time");// 时间成对，可能有多个时间
                    String id = recvJs.getString("_id");
                    String pubtime = recvJs.getString("cEvent_publish");
                    long issuetime = Long.parseLong(pubtime);
                    time = time.replace("null,", "");
                    Event event = new Event();
                    event.setEventID(id);
                    event.setContent(content);
                    event.setPublisher(provider);
                    event.setIssueTime(issuetime);
                    event.setTime(time);
                    event.setTitle(name);
                    event.setTheme(theme);
                    event.setPublisher(provider);
                    event.setPlace(place);
                    list.add(event);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            Log.e("1","eventjson is null");
        }

        return list;
    }

    public static List<Event> JsonToEventList(JSONArray json){

        List<Event> list = new ArrayList<>();


        if(json != null && json.size() > 0) {

            int size = json.size();
            for(int i=0 ; i<size;i++){
                try{
                    String eventString = (String) json.get(i);

                    JSONObject recvJs = JSON.parseObject(eventString);
//                    JSONObject recvJs = (JSONObject)json.get(i);

                    String cover = recvJs.getString("cEvent_figure");
                    String name = recvJs.getString("cEvent_name");
                    String theme = recvJs.getString("cEvent_theme");
                    int click_num = recvJs.getInteger("click_num");
                    String id = recvJs.getString("_id");
                    String tag = recvJs.getString("cEvent_tag");

                   Log.e("event", cover + "---" + name + "---" + theme + "---" + id + "---" + tag + "---" + click_num);

                    Event event = new Event();
                    event.setEventID(id);
                    event.setTitle(name);
                    event.setTheme(theme);
                    event.setCover(cover);
                    event.setReadCount(click_num);
                    event.setTag(tag);

                    list.add(event);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("1", e.toString());
                }
            }
        }else{
            Log.e("1","eventjson is null");
        }

        return list;
    }

    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}
