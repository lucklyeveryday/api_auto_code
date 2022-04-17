package com.lemon_yy.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lemon.encryption.EncryptUtils;
import com.lemon_yy.base.BaseCase;
import com.lemon_yy.data.GlobalEnvironment;
import com.lemon_yy.pojo.CaseInfo;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class RechargeTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(3);

    /*    //V3鉴权处理
//到环境变量中获取token
        String token=(String)GlobalEnvironment.envData.get("token");
        //V3版本的鉴权
        //1.获取时间戳timestamp(秒级),不是毫秒级
        //毫秒级：long timestamp=System.currentTimeMillis();
        // 秒级;
        long timestamp=new Date().getTime()/1000;
        //2。截取token的前面50位
        String tempStr=token.substring(0,50);
        //3.拼接上timestamp
        tempStr=tempStr+timestamp;
        //4.使用RSA加密算法进行加密-->得到签名
        String sign= EncryptUtils.rsaEncrypt(tempStr);
        //保存到环境变量中
        GlobalEnvironment.envData.put("timestamp",timestamp);
        GlobalEnvironment.envData.put("sign",sign);

        //参数化替换
        caseInfoList = paramsReplace(caseInfoList);*/
    }

    @Test(dataProvider = "getRechageDatas")
    public void testGetRechage(CaseInfo caseInfo) throws JsonProcessingException {
        //V3鉴权处理
//到环境变量中获取token
        String token=(String)GlobalEnvironment.envData.get("token");
        //V3版本的鉴权
        //1.获取时间戳timestamp(秒级),不是毫秒级
        //毫秒级：long timestamp=System.currentTimeMillis();
        // 秒级;
        long timestamp=new Date().getTime()/1000;
        //2。截取token的前面50位
        String tempStr=token.substring(0,50);
        //3.拼接上timestamp
        tempStr=tempStr+timestamp;
        //4.使用RSA加密算法进行加密-->得到签名
       // String sign= EncryptUtils.rsaEncrypt(tempStr);
        //保存到环境变量中
        // GlobalEnvironment.envData.put("timestamp",timestamp);
        //GlobalEnvironment.envData.put("sign",sign);

        //参数化替换
        caseInfo = paramsReplaceCaseInfo(caseInfo);


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
                        post(caseInfo.getUrl()).
                        then().log().all().
                        extract().response();

        //接口请求介绍之后把请求/响应的信息添加到allure中（附件的形式）
        //第一个参数：附件的名字 第二个参数 FileInputStream
        addLogToAllure(logFilePath);
        //接口响应的断言
        assertExpected(caseInfo,res);
        //数据库断言
        assertSQL(caseInfo);
    }

    @DataProvider
    public Object[] getRechageDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }


}
