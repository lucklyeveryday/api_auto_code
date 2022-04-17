package com.lemon_yy.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon_yy.base.BaseCase;
import com.lemon_yy.data.Constants;
import com.lemon_yy.data.GlobalEnvironment;
import com.lemon_yy.pojo.CaseInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class AddLoanTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(4);
        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);
    }

    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(CaseInfo caseInfo) throws JsonProcessingException {
        //请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        //发起接口请求
        Response res =
                given().
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                        when().
                        post(Constants.BASE_URL +caseInfo.getUrl()).
                        then().log().all().
                        extract().response();
        //接口请求介绍之后把请求/响应的信息添加到allure中（附件的形式）
        //第一个参数：附件的名字 第二个参数 FileInputStream

        addLogToAllure(logFilePath);
        //断言
        assertExpected(caseInfo,res);
        //获取项目id
        //保存到环境变量中
        if (res.path("data_id")!= null) {
            GlobalEnvironment.envData.put("loan_id",res.path("data_id"));
        }
        System.out.println("=="+GlobalEnvironment.envData);
    }

    @DataProvider
    public Object[] getAddLoanDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }
}
