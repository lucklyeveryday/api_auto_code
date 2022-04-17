package com.lemon_yy.testcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon_yy.base.BaseCase;
import com.lemon_yy.pojo.CaseInfo;
import com.lemon_yy.data.GlobalEnvironment;
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
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(2);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoDatas")
    public void testGetUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //发起接口请求
        Response res =
        given().
                headers(headersMap).
        when().
                get(caseInfo.getUrl()).
        then().
                extract().response();
        //接口请求介绍之后把请求/响应的信息添加到allure中（附件的形式）
        //第一个参数：附件的名字 第二个参数 FileInputStream
        addLogToAllure(logFilePath);

        //断言
        assertExpected(caseInfo,res);

    }

    @DataProvider
    public Object[] getUserInfoDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }







    public static void main(String[] args) {
        Integer memberID = 1111;
        String str1 = "/member/{{member_id}}/info{{mobile_phone}}";

        //参数化替换功能替换
        //正则表达式：
        //"." 匹配任意的字符
        //"*" 匹配前面的字符零次或者任意次数
        //"?" 贪婪匹配
        // .*?
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //2、通过正则表达式编译出来一个匹配器pattern
        Pattern pattern = Pattern.compile(regex);
        //3、开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(str1);
        //4、连续查找、连续匹配
        String findStr = "";
        while(matcher.find()){
            //输出找到匹配的结果
           System.out.println("group(0)::"+matcher.group(0));
            findStr=matcher.group(0);
            System.out.println("group(1)::"+matcher.group(1));
            //每一次匹配上就去进行替换
            findStr = str1.replace(findStr, "110110110");
            str1=findStr;

        }
        System.out.println(findStr);
    }
}
