package com.eventer.app.http;

import android.util.Log;

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
}
