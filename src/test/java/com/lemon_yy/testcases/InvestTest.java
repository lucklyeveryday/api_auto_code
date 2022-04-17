package com.lemon_yy.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon_yy.base.BaseCase;
import com.lemon_yy.pojo.CaseInfo;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class InvestTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(6);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }
    @Test(dataProvider = "getRechageDatas")
    public void testGetRechage(CaseInfo caseInfo) throws JsonProcessingException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //发起接口请求
        Response res =
                given().

                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                        when().
                        post(caseInfo.getUrl()).
                        then().log().all().
                        extract().response();
        addLogToAllure(logFilePath);
        //接口响应的断言
        assertExpected(caseInfo,res);
    }

    @DataProvider
    public Object[] getRechageDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }
}
