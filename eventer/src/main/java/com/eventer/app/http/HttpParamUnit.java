package com.eventer.app.http;

import com.eventer.app.Constant;
import com.eventer.app.entity.Schedual;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuNana on 2016/1/28.
 */
public class HttpParamUnit {
    public static Map<String,String> eventAddFeedback(String eid,
                                      String share, String click, String participate){
        Map<String,String> map=new HashMap<>();
        map.put("event_id", eid);
        map.put("share_num", share);
        map.put("click_num", click);
        map.put("participate_num", participate);
        map.put("token", Constant.TOKEN);
        map.put("uid",Constant.UID);
        return map;
    }

    public static Map<String,String> activityCreate(Schedual schedual){
        Map<String,String> map=new HashMap<>();
        map.put("a_name", schedual.getTitle());
        map.put("a_time", schedual.getEndtime());
        map.put("a_place", schedual.getPlace());
        map.put("a_desc", schedual.getDetail());
        map.put("a_frequency", schedual.getFrequency()+"");
        map.put("a_type", schedual.getType()+"");
        map.put("token", Constant.TOKEN);
        map.put("uid",Constant.UID);
        return map;
    }

    public static Map<String,String> activityParam(String id){
        Map<String,String> map=new HashMap<>();
        map.put("a_id", id);
        map.put("token", Constant.TOKEN);
        map.put("uid",Constant.UID);
        return map;
    }

    public static Map<String,String> eventListParam(String imei, int pos, int count, int size){
        Map<String,String> map=new HashMap<>();
        map.put("imei", imei);
        map.put("pos", pos + "");
        map.put("count", count + "");
        map.put("size", size + "");
        return map;
    }
}
