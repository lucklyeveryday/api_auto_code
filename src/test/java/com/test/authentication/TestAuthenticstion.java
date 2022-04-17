package com.test.authentication;

// import com.lemon.encryption.EncryptUtils;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Date;

import static io.restassured.RestAssured.given;

public class TestAuthenticstion {
    @Test
    public void test01() {
        //登录请求
        String loginParams = "{\n" +
                "  \"mobile_phone\": \"13323251004\",\n" +
                "  \"pwd\": \"lemon123456\"\n" +
                "}";
        Response res =
                given().log().all().
                        headers("X-Lemonban-Media-Type", "lemonban.v3").
                        contentType("application/json").
                        body(loginParams).
                        when().
                        post("http://api.lemonban.com/futureloan/member/login").
                        then().
                        log().body().extract().response();
        String token = res.path("data.token_info.token");
        Integer memberId = res.path("data.id");
        System.out.println("==========" + token);
        //V3版本的鉴权
        //1.获取时间戳timestamp(秒级),不是毫秒级
        //毫秒级：long timestamp=System.currentTimeMillis();
        // 秒级;
        long timestamp = new Date().getTime() / 1000;
        //2。截取token的前面50位
        String tempStr = token.substring(0, 50);
        System.out.println("==========" + tempStr);
        //3.拼接上timestamp
        tempStr = tempStr + timestamp;
//        //4.使用RSA加密算法进行加密-->得到签名
//        String sign=EncryptUtils.rsaEncrypt(tempStr);
//        System.out.println("=====sign====="+sign);

//    //充值请求
//        String rechargeParams="{\"member_id\":"+memberId+",\"amount\":0.01,\"timestamp\":"+timestamp+",+\"sign\":\""+sign+"\"}";
//
//        Response res2=
//                given().log().all().
//                        header("X-Lemonban-Media-Type","lemonban.v3").
//                        header("Authorization","Bearer "+token).
//                        contentType("application/json").
//                        body(rechargeParams).
//                        when().
//                        post("http://api.lemonban.com/futureloan/member/recharge").
//                        then().
//                        log().body().extract().response();
//
//    }
//
//    public static void main(String[] args) {
//     /*  // 1.加密引用jar包
//        String str =EncryptUtils.rsaEncrypt("123456");
//        System.out.println(str);
//        String strmd5=EncryptUtils.md5Encrypt("123456");
//        System.out.println(strmd5);*/
//
//        //获取毫秒级时间戳
//        System.out.println(System.currentTimeMillis());
//        //获取秒级的时间戳
//        System.out.println(new Date().getTime()/1000);
//    }
    }
}
