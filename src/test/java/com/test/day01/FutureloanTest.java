package com.test.day01;

import org.testng.annotations.Test;


import static io.restassured.RestAssured.given;

public class FutureloanTest {
    @Test
    public void testRegister(){
        String jsonstr="{\"mobile_phone\": \"13323232000\",\"pwd\": \"12345678\",\"type\":1}";
        given().
                contentType("application/json; charset=utf-8").
                //请求头
                header("X-Lemonban-Media-Type","lemonban.v1").
                //请求体
                body(jsonstr).
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                post("http://api.lemonban.com/futureloan/member/register").
        then().
                //对响应结果做什么事
                log().all();
    }

    @Test
    public void testLogin(){
        String jsonstr="{\"mobile_phone\": \"13323232000\",\"pwd\": \"12345678\",\"type\":1}";
        given().
                contentType("application/json; charset=utf-8").
                //请求头
                header("X-Lemonban-Media-Type","lemonban.v1").
                //请求体
                 body(jsonstr).
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
               post("http://api.lemonban.com/futureloan/member/login").
        then().
                //对响应结果做什么事
               log().all();
    }

}
