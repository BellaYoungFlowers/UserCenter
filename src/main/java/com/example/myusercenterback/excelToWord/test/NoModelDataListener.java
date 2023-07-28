package com.example.myusercenterback.excelToWord.test;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.myusercenterback.excelToWord.model.FieldData;
import com.example.myusercenterback.excelToWord.model.TableData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@Slf4j
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {


    int index = 0;
    String tableName = "";
    String currentTable = "";


    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }
}
