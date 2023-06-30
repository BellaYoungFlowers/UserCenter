package com.example.myusercenterback.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    private int gender;

    public DemoData(String name, int gender) {
        this.name = name;
        this.gender = gender;
    }
}