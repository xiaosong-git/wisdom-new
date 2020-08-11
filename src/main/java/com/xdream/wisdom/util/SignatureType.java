package com.xdream.wisdom.util;

public enum SignatureType {

    MD5("MD5"),

    SHA1("SHA1"),

    SHA256("SHA256");


    private String algorithm;


    SignatureType(String algorithm) {
        this.algorithm = algorithm;
    }


    public String getAlgorithm() {
        return algorithm;
    }
    public static SignatureType of(String algorithm){

        return MD5;

    }
}
