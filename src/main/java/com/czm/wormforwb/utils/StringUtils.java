package com.czm.wormforwb.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static String concatStringList(List<String> stringList){
        if(stringList.size() ==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String str : stringList){
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static Boolean isNotBlank(CharSequence str){
        return !isBlank(str);
    }

}
