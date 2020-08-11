package com.xdream.wisdom.util.response;

import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class MapResultData {

    public static Map<String, String> resultMap(String returnCode, String message, String merchOrderId, String bankResult, String faceComparisonScore){
        Map<String, String>resultMap=new HashMap<String, String>();
        resultMap.put("returnCode", returnCode);
        resultMap.put("message", message);
        resultMap.put("merchOrderId", merchOrderId);
        resultMap.put("bankResult",bankResult);
        if(!StringUtil.isNullOrEmpty(faceComparisonScore)||!"null".equals(faceComparisonScore)){
            resultMap.put("faceComparisonScore",faceComparisonScore);
        }
        return resultMap;
    }


    public static Map<String, String> resultMap(String returnCode, String message, String merchOrderId, String bankResult){
        Map<String, String>resultMap=new HashMap<String, String>();
        resultMap.put("returnCode", returnCode);
        resultMap.put("message", message);
        resultMap.put("merchOrderId", merchOrderId);
        resultMap.put("bankResult",bankResult);
        return resultMap;
    }
}
