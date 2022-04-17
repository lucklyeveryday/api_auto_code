package com.lemon_cs.testcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon_cs.base.BaseCase;
import com.lemon_cs.pojo.CaseInfo;
import com.lemon_cs.data.GlobalEnvironment;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class RegisterTest extends BaseCase {
    List<CaseInfo> caseInfolist;
    @BeforeClass
    public void setup(){
        //读取用例数据
        getCaseDataFromExcel(0);
    }

    @Test(dataProvider = "getRegisterDatas")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Map headersmap=objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Response res=
                given().
                        headers(headersmap).
                        body(caseInfo.getInputParams()).
                when().
                        post("http://api.lemonban.com/futureloan"+caseInfo.getUrl()).
                then().
                        log().all().extract().response();
        System.out.println("请求头："+headersmap);
        System.out.println("请求体："+caseInfo.getInputParams());
        System.out.println(("请求接口地址："+"http://api.lemonban.com/futureloan" + caseInfo.getUrl()));
        //断言
        //1.把断言数据转换为map
        ObjectMapper objectMapper2=new ObjectMapper();
        Map expectedMap=objectMapper2.readValue(caseInfo.getExpected(),Map.class);
        //2.循环遍历取到Map里的每一对键值
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String,Object> map:set){
//            System.out.println(map.getKey());
//            System.out.println(map.getValue());
            //关键点 ： 怎么做断言？？？ ---》通过Gpath获取实际接口响应对应字段的值
            //我们在设计Excel里面写用例的期望结果时，期望结果里面的键名 ---》Gpath表达式
            //期望结果里面的键值 ---》期望值
            System.out.println("请求体："+res.path(map.getKey()));
            Assert.assertEquals(res.path(map.getKey()),map.getValue());


        }
        //在登录模块用例执行结束之后，将memberid保存到环境变量中
        //1.拿到正常用例返回响应信息里面的memberId
        Integer memberId=res.path("data.id");
        if (memberId != null){
            //2.保存到环境变量中
            String mobilephone = res.path("data.mobile_phone");
            GlobalEnvironment.envData.put("mobile_phone",mobilephone);
            //3.注册成功的密码 -- 从用例数据里面
            String inputParsms = caseInfo.getInputParams();
            ObjectMapper objectMapper1=new ObjectMapper();
            Map inputParsmsMap=objectMapper1.readValue(inputParsms,Map.class);
            Object pwd = inputParsmsMap.get("pwd");
            GlobalEnvironment.envData.put("pwd",pwd+"");
        }

    }
    @DataProvider
    public Object[] getRegisterDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]

        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfolist.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象
//        return datas;
    }



}
