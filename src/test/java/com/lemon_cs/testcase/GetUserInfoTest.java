package com.lemon_cs.testcase;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon_cs.base.BaseCase;
import com.lemon_cs.pojo.CaseInfo;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTest extends BaseCase {

    List<CaseInfo> caseInfoslist;

    @BeforeClass
    public void setup(){
        //从excel读取用户信息接口模块所需要的用例数据
        caseInfoslist=getCaseDataFromExcel(2);
        //参数化替换
        caseInfoslist=paramsReplace(caseInfoslist);
    }

    @Test(dataProvider = "getUserInfoDatas")
    public void testUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
        //参数化替换
        /*//1、接口URL地址{{member_id}}给替换成环境变量中保存的值
        String url=regexReplace(caseInfo.getUrl());
        //2、响应结果中{member_id}}给替换成环境变量中保存的值
        String expecred = regexReplace((caseInfo).getExpected());
        //3.请求头Beater
        String requestHeaders=regexReplace(caseInfo.getRequestHeader());*/
        //请求头由json字符串转Map
        ObjectMapper objectMapper=new ObjectMapper();

        Map headersmap=objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Response res=
                given().
                        headers(headersmap).
                        //body(caseInfo.getInputParams()).
                        when().
                        get("http://api.lemonban.com/futureloan"+caseInfo.getUrl()).
                        then().
                        extract().response();

        //断言
        //1.把断言数据转换为map
        ObjectMapper objectMapper2=new ObjectMapper();
        Map expectedMap=objectMapper2.readValue(caseInfo.getExpected(),Map.class);
        //2.循环遍历取到Map里的每一对键值
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String,Object> map:set){
            //System.out.println("请求体："+res.path(map.getKey()));
            Assert.assertEquals(res.path(map.getKey()),map.getValue());

        }
    }
    @DataProvider
    public Object[] getUserInfoDatas(){
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
        File excelFile = new File("src/test/resources/api_testcases_futureloanv2.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class,importParams);
        return list;
    }



    public static void main(String[] args) {
        Integer memberId = 1111;
        String str1 = "/member/{{member_id}}/info{{member_phone}}";
        String str2 = "{\n" +
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
        String regex = "\\{\\{(.*?)\\}\\}";
        //2.通过正则表达式编译出来一个匹配器
        Pattern pattern = Pattern.compile(regex);
        //3.开始进行匹配 参数：你要去在那一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(str1);
        //4、连续查找、连续匹配
        //保存匹配到的整个表达式  比如：{{member_id}}
        String findStr = "";
        //保存匹配到的（）里面的内容  比如：member_id
        String singleStr="";
        while (matcher.find()) {
            //输出找到匹配的结果
            System.out.println("group(0):"+matcher.group(0));
            findStr=matcher.group(0);
            System.out.println("group(0):"+matcher.group(1));
            findStr=str1.replace(findStr,"110110110");
            //System.out.println("group(1):"+matcher.group(1));
            str1=findStr;
        }
        System.out.println(findStr);
    }
}
