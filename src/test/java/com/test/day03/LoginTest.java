package com.test.day03;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class LoginTest {

    List<CaseInfo> caseInfoslist;

    @BeforeTest
    public void setup(){
        //从excel读取登录接口模块所需要的用例数据
        caseInfoslist=getCaseDataFromExcel(1);
    }

    @Test(dataProvider = "getLoginDatas")
    public void testLogin01(CaseInfo caseInfo) throws JsonProcessingException {
        //String jsonStr = "{\"mobile_phone\":\"13323231011\",\"pwd\":\"12345678\"}";
        /*String requestHeader = caseInfo.getRequestHeader();
        System.out.println("请求头："+requestHeader);
        System.out.println("请求头："+caseInfo.getInputParams());*/
        //字符串请求头转换成Map -- >
        //实现思路：原始字符串转换会比较麻烦，所有，把原始的字符串通过json数据类型包保存，通过ObjectMapper来去转换为Map
        //通过jackson 把json字符串 转换成 Map
        //1.实例化objectMapper对象
        ObjectMapper objectMapper=new ObjectMapper();
        //readValue方法参数解释;
        //第一个参数：json字符串 第二个参数： 转成的类型（Map）
        Map headersmap=objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Response res=
        given().
                headers(headersmap).
                body(caseInfo.getInputParams()).
                when().
                post("http://api.lemonban.com/futureloan"+caseInfo.getUrl()).
                then().
                log().body().extract().response();
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
            GlobalEnvironment.memberId=memberId;
        }
    }
    @DataProvider
    public Object[] getLoginDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]

        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoslist.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象
//        return datas;
    }

    /*
    * 从excel读取所需的用例数据
    * index --》sheet的索引，从0开始的
    */
    public List<CaseInfo> getCaseDataFromExcel(int index){
        //从Excel读取所有的用例数据
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        File excelFile = new File("src/test/resources/api_testcases_futureloanv1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class,importParams);
        return list;
    }

    //
    public static void main(String[] args) {
        Integer memberId=1111;
        String str1="/member/{{member_id}}/info";
        String str2="{\n" +
                "    \"code\": 0,\n" +
                "    \"msg\": \"OK\",\n" +
                "    \"data.id\": {{member_id}},\n" +
                "    \"data.mobile_phone\": \"13323234110\"   \n" +
                "}";
        //参数化替换的功能替换的实现
        //正则表达式：
        //"." 匹配任意的字符（只能匹配一个字符）
        //"*" 匹配前面的字符零次或者任意次数
        //"?" 贪婪匹配
        // .*?
        //1.定义正则表达式
        String regex="\\{\\{(.*?)\\}\\}";
        //2.通过正则表达式编译出来一个匹配器
        Pattern pattern=Pattern.compile(regex);
        //3.开始进行匹配 参数：你要去在那一个字符串里面去进行匹配
        Matcher matcher=pattern.matcher(str1);
        //4、连续查找、连续匹配
        while (matcher.find()){
            //输出找到匹配的结果
            //System.out.println(matcher.group(0));
            //findStr=matcher.group(0);
            //System.out.println(matcher.group(1));
        }


    }
}
