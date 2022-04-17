package com.lemon_yy.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon_yy.base.BaseCase;
import com.lemon_yy.pojo.CaseInfo;
import com.lemon_yy.data.GlobalEnvironment;
import com.lemon_yy.util.JDBCUtils;
import com.lemon_yy.util.PhoneRandom;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class RegisterTest extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){
        //读取用例的数据
        caseInfoList =  getCaseDataFromExcel(0);



    }

    @Test(dataProvider = "getRegisterDatas")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException {
        //随机生成三个没有注册过的随机号码
        if (caseInfo.getCaseId()==1) {
            String mobilephone1 = PhoneRandom.getRandomPhone();
            //存到环境变量中
            GlobalEnvironment.envData.put("mobile_phone1", mobilephone1);
        }else if (caseInfo.getCaseId()==2) {
            String mobilephone2 = PhoneRandom.getRandomPhone();
            GlobalEnvironment.envData.put("mobile_phone2", mobilephone2);
        }else if (caseInfo.getCaseId()==3) {
            String mobilephone3 = PhoneRandom.getRandomPhone();
            GlobalEnvironment.envData.put("mobile_phone3", mobilephone3);
        }
        //参数化替换 --当前的case
        caseInfo=paramsReplaceCaseInfo(caseInfo);

//请求头由json字符串转Map
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        String logFilePath = addLogToFile(caseInfo.getInterfaceName(),caseInfo.getCaseId());
        Response res =
                given().
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                when().
                        post(caseInfo.getUrl()).
                then().
                        log().all().
                        extract().response();

        //接口请求介绍之后把请求/响应的信息添加到allure中（附件的形式）
        //第一个参数：附件的名字 第二个参数 FileInputStream
        addLogToAllure(logFilePath);

        //断言
        //1.断言响应结果
        assertExpected(caseInfo,res);
        //2.断言数据库
        assertSQL(caseInfo);
        //在登录模块用例执行结束之后将memberId保存到环境变量中
        /*String checkSQL=caseInfo.getCheckSQL();
        if (checkSQL !=null){
        Map checkSQLMap=fromJsonToMap(checkSQL);
        //2、循环遍历取到map里面每一组键值对
        Set<Map.Entry<String, Object>> set = checkSQLMap.entrySet();
        for (Map.Entry<String,Object> mapEntry : set) {
            String sql = mapEntry.getKey();
            //查询数据库
            Object actual = JDBCUtils.querySingle(sql);
            if (actual instanceof Long) {
                //把expeced转成Long类型
                Long expected = new Long(mapEntry.getValue().toString());
                //实际值
                Assert.assertEquals(actual, expected);
            }
            //实际值
            //Integer expected=(Integer)mapEntry.getValue();
        }
        }*/
        //3、注册成功的密码--从用例数据里面
        String inputParams =  caseInfo.getInputParams();
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map inputParamsMap = objectMapper1.readValue(inputParams,Map.class);
        Object pwd = inputParamsMap.get("pwd");


        //1、拿到正常用例返回响应信息里面的memberId
        if (caseInfo.getCaseId()==1){
            //2、保存到环境变量中
            GlobalEnvironment.envData.put("mobile_phone1", res.path("data.mobile_phone"));
            GlobalEnvironment.envData.put("member_id1", res.path("data.id"));
            GlobalEnvironment.envData.put("pwd1",pwd+"");

        }else if(caseInfo.getCaseId()==2){
            //2、保存到环境变量中
            GlobalEnvironment.envData.put("mobile_phone2",res.path("data.mobile_phone"));
            GlobalEnvironment.envData.put("member_id2", res.path("data.id"));
            GlobalEnvironment.envData.put("pwd2",pwd+"");

        }else if (caseInfo.getCaseId()==3){
            //2、保存到环境变量中
            GlobalEnvironment.envData.put("mobile_phone3",res.path("data.mobile_phone"));
            GlobalEnvironment.envData.put("member_id3", res.path("data.id"));
            GlobalEnvironment.envData.put("pwd3",pwd+"");

        }


    }
    @DataProvider
    public Object[] getRegisterDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象

    }
}
