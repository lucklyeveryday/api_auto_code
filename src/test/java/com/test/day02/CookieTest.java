package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CookieTest {
    //设置cookies的全局变量
    Map<String, String> cookieMap=new HashMap<String, String>();
    /*
    * cookie+session的鉴权方式
    */
    @Test
    public void testAuthenticationWithSession(){
        //登录的接口请求
        Response res=
        given().
                contentType("application/x-www-form-urlencoded; charset=UTF-8").
                header("X-Lemonban-Media-Type","lemonban.v1").
                formParam("loginname","admin").formParam("password","e10adc3949ba59abbe56e057f20f883e").
        when().
                post("http://erp.lemfix.com/user/login").
        then().
                log().all().extract().response();
        cookieMap=res.getCookies();
        //获取Set-Cookie：
        //1.通过header来获取
        //System.out.println(res.header("Set-Cookie"));
        //推荐
        //2.通过提供的API获取
        //System.out.println("Cookies:"+res.getCookies());




    }
    @Test
    public void testXXX(){
        //getUserSession接口请求 必须要携带cookies里面保存的sessionid
        given().
                cookies(cookieMap).
                when().
                get("http://erp.lemfix.com/user/getUserSession").
                then().
                log().all();
    }

}
