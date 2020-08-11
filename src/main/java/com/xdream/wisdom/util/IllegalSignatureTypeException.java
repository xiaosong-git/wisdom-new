package com.xdream.wisdom.util;

public class IllegalSignatureTypeException extends Exception {

    private SignatureType signatureType;


    public IllegalSignatureTypeException(SignatureType signatureType, String message) {
        super(message);
        this.signatureType = signatureType;
    }

    public IllegalSignatureTypeException(SignatureType signatureType, String message, Throwable cause) {
        super(message, cause);
        this.signatureType = signatureType;
    }

    public IllegalSignatureTypeException(SignatureType signatureType, Throwable cause) {
        super(cause);
        this.signatureType = signatureType;
    }

    public IllegalSignatureTypeException(SignatureType signatureType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this.signatureType = signatureType;
    }


    public SignatureType getSignatureType() {
        return signatureType;
    }
}
