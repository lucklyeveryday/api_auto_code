package com.test.authentication;

import com.lemon.encryption.EncryptUtils;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Date;

import static io.restassured.RestAssured.given;

public class TestAuthentication {
    @Test
    public void test01(){
        String loginParams = "{\n" +
                "  \"mobile_phone\": \"13323251004\",\n" +
                "  \"pwd\": \"lemon123456\"\n" +
                "}";
        //登录请求
        Response res =
                given().log().all().
                        header("X-Lemonban-Media-Type","lemonban.v3").
                        contentType("application/json").
                        body(loginParams).
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        log().all().
                        extract().response();
        String token = res.path("data.token_info.token");
        Integer memberId = res.path("data.id");

        //V3版本的鉴权
        //1、获取到时间戳timestamp(秒级)，不是毫秒级的
        long timestamp = new Date().getTime()/1000;
        //2、截取token的前面50位
        String tempStr = token.substring(0,50);
        //3、拼接上timestamp
        tempStr =tempStr+timestamp;
        //4、使用RSA加密算法进行加密->得到签名
        String sign = EncryptUtils.rsaEncrypt(tempStr);

        String rechargeParams = "{\"member_id\": "+memberId+",\"amount\": 0.01,\"timestamp\":"+timestamp+",\"sign\":\""+sign+"\"}";
        //充值请求
        Response res2 =
                given().log().all().
                        header("X-Lemonban-Media-Type","lemonban.v3").
                        header("Authorization","Bearer "+token).
                        contentType("application/json").
                        body(rechargeParams).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().
                        extract().response();
    }

    public static void main(String[] args) {
        //获取毫秒级时间戳
        System.out.println(System.currentTimeMillis());
        //获取秒级时间戳
        System.out.println(new Date().getTime()/1000);
    }
}
