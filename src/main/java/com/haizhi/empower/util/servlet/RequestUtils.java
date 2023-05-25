package com.haizhi.empower.util.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class RequestUtils {

    public static JSONObject parseObject(HttpServletRequest request) {
        String text = readRequest(request);
        JSONObject res = JSONArray.parseObject(text);
        log.info("RequestUtils#parse res:{}", res);
        return res;
    }

    public static JSONArray parse(HttpServletRequest request) {
        String text = readRequest(request);
        log.info("RequestUtils#parse text:{}", text);
        JSONArray res = JSON.parseArray(text);
        log.info("RequestUtils#parse res:{}", res);
        return res;
    }

    public static String readRequest(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
