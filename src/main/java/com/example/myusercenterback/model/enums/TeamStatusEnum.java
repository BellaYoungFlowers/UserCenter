package com.example.myusercenterback.model.enums;

/**
 * @author:xxxxx
 * @create: 2023-07-20 09:57
 * @Description: 队伍状态枚举类
 */
public enum TeamStatusEnum {

    Public(0,"公开"),
    Private(1,"私有"),
    Secret(2,"加密");

    public static TeamStatusEnum getTeamStatusEnumByValue(Integer value){
        if(value == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if(value.equals(teamStatusEnum.getValue())){
                return teamStatusEnum;
            }
        }
        return null;

    }

    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
