package com.example.myusercenterback.excelToWord.model;

import lombok.Data;

/**
 * @author:xxxxx
 * @create: 2023-07-06 15:29
 * @Description: 列数据
 */

@Data
public class FieldData {
    int fieldIndex;
    String fieldName;
    String fieldType;
    String fieldLength;
    String fieldIsNull;
    String fieldComment;
}
