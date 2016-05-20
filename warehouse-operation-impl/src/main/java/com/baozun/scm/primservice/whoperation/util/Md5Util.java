package com.baozun.scm.primservice.whoperation.util;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Md5Util implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 580199397105589205L;

    public static final Logger log = LoggerFactory.getLogger(Md5Util.class);

    public static String getMd5(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance("MD5");
        byte[] inputByteArray = str.getBytes();
        messageDigest.update(inputByteArray);
        byte[] outputByteArray = messageDigest.digest();
        StringBuffer buf = new StringBuffer();
        for (int offset = 0; offset < outputByteArray.length; offset++) {
            int val = ((int) outputByteArray[offset]) & 0xff;
            if (val < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(val));
        }
        return buf.toString();
    }

}
