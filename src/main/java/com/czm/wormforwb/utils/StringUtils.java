package com.czm.wormforwb.utils;

/**
 * String工具类
 * @author Slience
 * @date 2022/3/10 16:35
 **/
public class StringUtils {

    public static Boolean isBlank(CharSequence str){
        int strLen;
        if(str != null && (strLen = str.length()) != 0){
            for(int i=0;i<strLen;++i){
                if(!Character.isWhitespace(str.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean isNotBlank(CharSequence str){
        return !isBlank(str);
    }

}
