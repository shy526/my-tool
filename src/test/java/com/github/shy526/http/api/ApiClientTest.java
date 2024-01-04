package com.github.shy526.http.api;


import com.alibaba.fastjson.JSONObject;
import com.github.shy526.http.HttpClientFactory;
import com.github.shy526.http.HttpClientProperties;
import com.github.shy526.http.HttpClientService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiClientTest   {

    @Test
    public void testExec() {

        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        ApiTestEnum[] values = ApiTestEnum.values();
        for (int i=0;i<ApiTestEnum.values().length;i++) {
            JSONObject exec = ApiClient.exec("https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", httpClientService,values[i]);
            System.out.println(values[i]);
            System.out.println(exec.getJSONObject("data").get("country")+"-"+exec.getJSONObject("data").get("province")+"->" + exec.getJSONObject("data").get("addr"));

        }

    }

    @Test
    public void testExec2() {
        HttpClientService httpClientService = HttpClientFactory.getHttpClientService(new HttpClientProperties());
        JSONObject exec = ApiClient.exec("https://proxy.shy526.workers.dev?ipEcho=1&targetUrls=https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo,https://api.live.bilibili.com/xlive/web-room/v1/index/getIpInfo", httpClientService,ApiTestEnum.TOOLFK);
        System.out.println(exec.getJSONObject("data").get("country")+"-"+exec.getJSONObject("data").get("province")+"->" + exec.getJSONObject("data").get("addr"));

    }


    @Test
    public void testExec3() {
        String str="测试数据";
        byte[] bytes = str.getBytes();
        List<Integer> colors = new ArrayList<>();
        List<Integer> rgb = new ArrayList<>(3);
        int length=bytes.length;
        int a = ((length >> 24) & 0xff);
        int r =  ((length >> 16) & 0xff);
        int g =  ((length >> 8) & 0xff);
        int b =  (length & 0xff);
        int color = getColor(Lists.newArrayList(r, g, b));
        colors.add(color);
        colors.add(getColor(Lists.newArrayList(a, 0, 0)));
        for (byte aByte : bytes) {
            rgb.add(Byte.toUnsignedInt(aByte)) ;
            if (rgb.size()==3){
                colors.add(getColor(rgb));
                rgb.clear();
            }
        }
        BufferedImage br = new BufferedImage(colors.size(), 1, BufferedImage.TYPE_INT_RGB);
        int width = br.getWidth();
        int height = br.getHeight();
        Iterator<Integer> iterator = colors.iterator();
        for (int y=0;y<height;y++){
            for (int x=0;x<width;x++){

                br.setRGB(x,y,iterator.next());
            }
        }
        File file = new File("D:\\个人文件\\图片\\test.png");
        try {
            boolean png = ImageIO.write(br, "png", new FileOutputStream(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void testExec4() {
        try {
            BufferedImage br = ImageIO.read(new File("D:\\个人文件\\图片\\test.png"));
            int width = br.getWidth();
            int height = br.getHeight();
            int rgb1 = br.getRGB(0, 0);
            byte[] colorRGB = getColorRGB(rgb1);
            int color1 = getColor(Lists.newArrayList((int) colorRGB[0], (int) colorRGB[1], (int) colorRGB[2]));
            int rgb2 = br.getRGB(1, 0);
            colorRGB = getColorRGB(rgb2);
            int lent =color1|((int)colorRGB[0])<<24;
            System.out.println("rgb2 = " + rgb2);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (int y=0;y<height;y++){
                for (int x=0;x<width;x++){
                    int color = br.getRGB(x, y);
                    byte[] rgb = getColorRGB(color);
                    bos.write(rgb);
                }
            }
            bos.write(0);
            bos.write(0);
            byte[] bytes = bos.toByteArray();
            String string = new String(bytes,6,12);
            System.out.println("string = " + string);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void testExec5() {
        int v=100;
        byte a = (byte) ((v >> 24) & 0xff);
        byte b = (byte) ((v >> 16) & 0xff);
        byte c = (byte) ((v >> 8) & 0xff);
        byte d = (byte) (v & 0xff);

        System.out.println("d = " + d);
    }

    private static int getColor(List<Integer> rgb) {
        int r=rgb.get(0);
        int g=rgb.get(1);
        int b=rgb.get(2);
        return (r<<16) | (g<<8) | b;
    }

    public static byte[] getColorRGB(int color) {
        byte r = (byte) ((color >> 16) & 0xff);
        byte g = (byte) ((color >> 8) & 0xff);
        byte b = (byte) (color & 0xff);
        return new byte[]{r, g, b};
    }
}