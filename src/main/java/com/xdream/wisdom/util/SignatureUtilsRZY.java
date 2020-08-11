package com.xdream.wisdom.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class SignatureUtilsRZY {

    private static final String ENCODING = "UTF-8";

    static final String PARAMETER_SIGNATURE = "signature";

    static final String PARAMETER_SIGNATURE_TYPE = "signatureType";

    private static final String[] NON_SIGNATURE_FACTORS = {
            PARAMETER_SIGNATURE
    };

    // TODO: 2018/1/31 Wrap map within {@link java.lang.ref.SoftReference} to avoid memory-holding?
    private static final ThreadLocal<TreeMap<String, String>> reusableOrderingMap = new ThreadLocal<TreeMap<String, String>>();


    public static boolean validateSignature(HttpServletRequest request, String appKey) throws IllegalSignatureTypeException {
        TreeMap<String, String> orderingMap = null;
        try {
            orderingMap = getCurrentOrderingMap();

            Enumeration<String> enumeration = request.getParameterNames();
            while(enumeration.hasMoreElements()){
                String parameterName = enumeration.nextElement();
                String parameterValue = request.getParameter(parameterName);
                orderingMap.put(parameterName, parameterValue);
            }

            String signatureType = request.getParameter(PARAMETER_SIGNATURE_TYPE);
            SignatureType signatureTypeToUse = (signatureType != null ? SignatureType.of(signatureType) : SignatureType.MD5);
            String signature = buildSignature(orderingMap, appKey, signatureTypeToUse);

            return signature.equals(request.getParameter(PARAMETER_SIGNATURE));
        }finally {
            if(orderingMap != null){
                // For reuse
                orderingMap.clear();
            }
        }
    }

    private static String generateSignedQuery(String query, String appKey, SignatureType signatureType) throws IllegalSignatureTypeException {
        Assertion.notNull(query, "'query' must not be null");
        Assertion.notNull(appKey, "'appKey' must not be null");

        TreeMap<String, String> orderingMap = null;
        try {
            // Append 'signatureType' parameter to tail
            String queryToSign = query + "&" + PARAMETER_SIGNATURE_TYPE + "=" + signatureType.getAlgorithm();
            orderingMap = getCurrentOrderingMap();
            populateOrderingMap(queryToSign, orderingMap);

            orderingMap.put(PARAMETER_SIGNATURE_TYPE, signatureType.getAlgorithm());

            return new StringBuilder().append(queryToSign)
                    .append('&').append(PARAMETER_SIGNATURE).append('=').append(buildSignature(orderingMap, appKey, signatureType))
                    .toString();
        }finally {
            if(orderingMap != null){
                // For reuse
                orderingMap.clear();
            }
        }
    }

    static void populateOrderingMap(String query, TreeMap<String, String> orderingMap) {
        String[] parameterPairs = query.split("&");
        for (String parameterPair : parameterPairs) {
            String[] parameterNameAndValue = parameterPair.split("=");
            orderingMap.put(parameterNameAndValue[0], parameterNameAndValue[1]);
        }
    }

    /**
     * Convenience method for {@link #buildSignature(Map, String, SignatureType)} to accepting any {@code Map}s
     * @since 2018/04/19 11:42
     */
    static String buildSignature(String query, String appKey, SignatureType signatureType) throws IllegalSignatureTypeException {
        TreeMap<String, String> orderingMap = new TreeMap<>();
        populateOrderingMap(query, orderingMap);
        return buildSignature(orderingMap, appKey, signatureType);
    }

    /**
     * Convenience method for {@link #buildSignature(TreeMap, String, SignatureType)} to accepting any {@code Map}s
     * @since 2018/04/19 11:42
     */
    public static String buildSignature(Map<String, String> orderingMap, String appKey, SignatureType signatureType) throws IllegalSignatureTypeException {
        TreeMap<String, String> orderingMapToUse;
        if(orderingMap instanceof TreeMap){
            orderingMapToUse = (TreeMap<String, String>) orderingMap;
        }
        else{
            orderingMapToUse = new TreeMap<>(orderingMap);
        }
        return buildSignature(orderingMapToUse, appKey, signatureType);
    }

    static String buildSignature(TreeMap<String, String> orderingMap, String appKey, SignatureType signatureType) throws IllegalSignatureTypeException {
        StringBuilder stringToSign = new StringBuilder(orderingMap.size() * 10);
        boolean first = true;
        outer: for (Map.Entry<String, String> entry : orderingMap.entrySet()) {
            for (String nonSignatureFactor : NON_SIGNATURE_FACTORS) {
                if(nonSignatureFactor.equals(entry.getKey())){
                    continue outer;
                }
            }

            if(!first){
                stringToSign.append('&');
            }else{
                first = false;
            }

            stringToSign.append(entry.getKey()).append('=').append(entry.getValue());
        }
        stringToSign.append("&appKey=").append(appKey);

        switch (signatureType){
            case MD5:{
                try {
                    // 通过URL-encoding得到ASCII字符(兼容)，因此不惧在任何时候的getBytes()
                    return Md5Utils.md5WithUpperCase(URLEncoder.encode(stringToSign.toString(), ENCODING));
                } catch (UnsupportedEncodingException impossible) {
                }
            }
            default: throw new IllegalSignatureTypeException(signatureType, (String) null);
        }
    }

    private static TreeMap<String, String> getCurrentOrderingMap() {
        TreeMap<String, String> orderingMap;
        if((orderingMap = reusableOrderingMap.get()) == null){
            orderingMap = new TreeMap<>();
            reusableOrderingMap.set(orderingMap);
        }
        return orderingMap;
    }
}
