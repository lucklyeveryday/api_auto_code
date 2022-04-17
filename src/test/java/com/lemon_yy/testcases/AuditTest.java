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

public class AuditTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(5);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getRechageDatas")
    public void testGetRechage(CaseInfo caseInfo) throws JsonProcessingException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认是Float存储的）
        //RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //发起接口请求
        Response res =
                given().
                        //让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认是Float存储的）
                        //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                                headers(headersMap).
                        body(caseInfo.getInputParams()).
                        when().
                        patch(caseInfo.getUrl()).
                        then().log().all().
                        extract().response();
        System.out.println("============="+caseInfo);
        System.out.println("=======项目id======"+caseInfo.getInputParams());
        //接口请求介绍之后把请求/响应的信息添加到allure中（附件的形式）
        //第一个参数：附件的名字 第二个参数 FileInputStream
        addLogToAllure(logFilePath);
        //接口响应的断言
        assertExpected(caseInfo,res);
        //数据库断言
        //assertSQL(caseInfo);
    }

    @DataProvider
    public Object[] getRechageDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }
}
