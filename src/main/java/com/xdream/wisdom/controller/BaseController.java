package com.xdream.wisdom.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseController {
    public Map<String, Object> getParamsToMap(HttpServletRequest request) {
        Map<String,Object>  res = new HashMap<String,Object>();
        Map<String,String[]>  parameter = request.getParameterMap();
        Iterator<String> it = parameter.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            String[]  val = parameter.get(key);
            if(val!=null&&val.length>0){
                if(val[0]!=null&&!"".equals(val[0])){
                    res.put(key, val[0].trim());
                }
            }
        }
        return res;
    }

}
