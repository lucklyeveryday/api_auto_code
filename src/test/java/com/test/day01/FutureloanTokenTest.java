package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class FutureloanTokenTest {
    @Test
    public void testLogin(){
        //获取响应结果（xtract().response()）
        String jsonstr="{\"mobile_phone\": \"13323232000\",\"pwd\": \"12345678\",\"type\":1}";
        Response res=
        given().
                contentType("application/json; charset=utf-8").
                //请求头
                        header("X-Lemonban-Media-Type","lemonban.v2").
                //请求体
                        body(jsonstr).
                //given()配置参数，请求头，请求参数，请求数据
                        when().
                //when是用来发起请求（get/post）
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                //对响应结果做什么事
                        log().all().
                        extract().response();
        //获取响应信息里面所有的内容：响应头+响应体  --》转成字符串格式：asString()
        System.out.println(res.asString());
        //提取响应状态码
        System.out.println(res.statusCode());
        //获取接口响应时间，单位为毫秒
        System.out.println("获取接口响应时间：【"+res.time()+"】毫秒");
        //提取响应头
        String headerValue=res.header("Content-Type");
        System.out.println(res.header("Content-Type"));
        //提取响应体
        //path方法：--》使用Gpath路径表达语法提取

        //1.获取注册时间
        String regtimeValue=res.path("data.reg_time");
        System.out.println(regtimeValue);

        //2.提取响应结果对应的字段值token
        String tokenValue=res.path("data.token_info.token");
        System.out.println(tokenValue);

        //3.提取会员id
        int menberIdValue=res.path("data.id");
        System.out.println(menberIdValue);

        //充值请求
        //把请求数据里放到map里
        //Map<String, Integer> map=new HashMap<String, Integer>();//Integer：int类型的值
        Map<String, Object> map=new HashMap<String, Object>();// Object:传任意类型的数据
        map.put("member_id",menberIdValue);
        map.put("amount",10000);
        given().
                contentType("application/json; charset=utf-8").
                //请求头
                header("X-Lemonban-Media-Type","lemonban.v2").
                //按照接口文档规定，如果按照鉴权的方式lemonban.v2，那么久必须要加token发送请求
                header("Authorization","Bearer "+tokenValue).
                //请求体
                body(map).
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                post("http://api.lemonban.com/futureloan/member/recharge").
        then().
                //对响应结果做什么事
                log().all();
    }
}
