package com.example.myusercenterback.excelToWord.model;

import lombok.Data;

import java.util.List;

/**
 * @author:xxxxx
 * @create: 2023-07-06 11:43
 * @Description: 数据表实体类
 */
@Data
public class TableData {
    private int tableIndex;
    private String tableComment;
    private String tableName;
    private List<FieldData> tableDataList;
}


