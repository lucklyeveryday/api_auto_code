package com.test.day01;
//静态导入 REST-Assured 类路径
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
//静态导入 REST-Assured 类路径
import static io.restassured.RestAssured.given;
public class RestApiTest {



/*        //1.简单的get请求
        *//*  given 设置测试预设（包括请求头、请求参数、请求体、cookies 等等）
            when 所要执行的操作（GET/POST 请求）
            then 解析结果、断言
*/
        //链式调用的写法：given().when().then()
         @Test
    public void testGet01(){
        given().
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                get("http://httpbin.org/get").
        then().
                //对响应结果做什么事
                log().all();
                //all:所有的响应信息（包括响应体、响应信息等）
                //body（响应体）：log().body();
              }


      @Test
    public void testGet02() {
          //2.-1带参数的get请求
          given().
                  //given()配置参数，请求头，请求参数，请求数据
                          when().
                  //when是用来发起请求（get/post）
                          get("http://httpbin.org/get?name=张三&age=20").
                  then().
                  //对响应结果做什么事
                          log().all();
      }
    @Test
    public void testGet03() {
          //3.-2带参数的get请求(queryParam)
          given().
                  queryParam("name", "张三").queryParam("age", "20").
                  //given()配置参数，请求头，请求参数，请求数据
                          when().
                  //when是用来发起请求（get/post）
                          get("http://httpbin.org/get").
                  then().
                  //对响应结果做什么事
                          log().all();
      }


        @Test
        //4.-3带参数的get请求 多个的情况下(queryParams)
        public void testGet04() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", "张三");
            map.put("age", "20");
            map.put("address", "杭州");
            map.put("sex", "女");
            given().
                    queryParams(map).
                    //given()配置参数，请求头，请求参数，请求数据
                            when().
                    //when是用来发起请求（get/post）
                            get("http://httpbin.org/get").
                    then().
                    //对响应结果做什么事
                            log().all();
        }
    @Test
    //5.-1发post请求-form表单参数
    //注意事项：如果form表单参数有中文的话，记得加charset=utf-8到content-type里，否则会有乱码的问题
    public void testPost01() {
        given().
                formParam("name","张三").
                contentType("application/x-www-form-urlencoded; charset=utf-8").
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                post("http://httpbin.org/post").
        then().
                //对响应结果做什么事
                log().all();
    }
    @Test
    //6.-2发post请求-json参数类型
    public void testPost02() {
             String jsonstr="{\"mobile_phone\": \"13888888811\",\"pwd\": \"123456\"}";
        Map<String, String> map=new HashMap<String, String>();
        map.put("mobile_phone","13323234545");
        map.put("pwd","12345678");
        given().
                contentType("application/json; charset=utf-8").
                body(map).
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                post("http://httpbin.org/post").
        then().
                //对响应结果做什么事
                log().all();
    }
    @Test
    //7.-3发post请求-xml参数类型
    public void testPost03() {
        String xmlstr="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<suite>\n" +
                "<class>测试</class>\n" +
                "</suite>";
        given().
                contentType("text/xml; charset=utf-8").
                body(xmlstr).
                //given()配置参数，请求头，请求参数，请求数据
                        when().
                //when是用来发起请求（get/post）
                        post("http://httpbin.org/post").
                then().
                //对响应结果做什么事
                        log().all();
    }
    @Test
    //8.-4发post请求-多参数表单（一般用于上传文件，传输大容量的数据）
    public void testPost04() {

        given().
                contentType("multipart/form-data; charset=utf-8").
                multiPart(new File("C:\\Users\\admin\\Desktop\\0085c8YEly1gd6gjr6333g308c08c1fn.gif")).
                //given()配置参数，请求头，请求参数，请求数据
        when().
                //when是用来发起请求（get/post）
                post("http://httpbin.org/post").
        then().
                //对响应结果做什么事
                log().all();
    }
}
