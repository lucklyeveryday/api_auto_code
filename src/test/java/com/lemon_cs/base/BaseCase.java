package com.lemon_cs.base;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon_cs.pojo.CaseInfo;
import com.lemon_cs.data.GlobalEnvironment;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 所有测试用例类的父类，里面放置公用方法
 */
public class BaseCase {


    /**
     * 从Excel读取所需的用例数据
     * @param index sheet的索引，从0开始的
     */
    public List<CaseInfo> getCaseDataFromExcel(int index){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);
        File excelFile = new File("src/test/resources/api_testcases_futureloanv2.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class,importParams);
        return list;
    }

    /**
     * 参数化替换
     * @param caseInfoList 当前测试类中的所有测试用例数据
     * @return 参数化替换之后的用例数据
     */
    public List<CaseInfo> paramsReplace(List<CaseInfo> caseInfoList){
        //对四块做参数化处理（请求头、接口地址、参数输入、期望返回结果）
        for (CaseInfo caseInfo : caseInfoList){
            //如果数据是为空的，没有必要去进行参数化的处理
            if(caseInfo.getRequestHeader() != null) {
                String requestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(requestHeader);
            }
            if(caseInfo.getUrl() != null) {
                String url = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(url);
            }
            if(caseInfo.getInputParams() != null) {
                String inputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(inputParams);
            }
            if(caseInfo.getExpected() != null) {
                String expected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(expected);
            }
        }
        return caseInfoList;
    }


    /**
     * 正则替换
     * @param sourceStr 原始的字符串
     * @return 查找匹配替换之后的内容
     */
    public String regexReplace(String sourceStr){
        //  /member/{{member_id}}/info
        //1、定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //2、通过正则表达式编译出来一个匹配器pattern
        Pattern pattern = Pattern.compile(regex);
        //3、开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(sourceStr);
        //保存匹配到的整个表达式，比如：{{member_id}}
        String findStr = "";
        //保存匹配到的()里面的内容  比如：member_id
        String singleStr ="";
        //4、连续查找、连续匹配
        while(matcher.find()){
            //输出找到匹配的结果 匹配到整个正则对应的字符串内容
            findStr = matcher.group(0);
            //大括号里面的内容
            singleStr=matcher.group(1);
            //5、先去找到环境变量里面对应的值
            //System.out.println("参数化替换GlobalEnvironment.envData：："+GlobalEnvironment.envData.get(singleStr));
            Object replaceStr = GlobalEnvironment.envData.get(singleStr);
            //6、替换原始字符串中的内容
            //"/member/{{member_id}}/info"  -->"/member/1111/info"
            sourceStr= sourceStr.replace(findStr, replaceStr + "");
            // "/member/{{member_id}}/info{{mobile_phone}}"
            //第一次替换之后：sourceStr --》"/member/1111/info{{mobile_phone}}"
            //第二次替换之后：sourceStr --》"/member/1111/info 13323234545"
        }

        //返回原样
        return sourceStr;
    }
}

