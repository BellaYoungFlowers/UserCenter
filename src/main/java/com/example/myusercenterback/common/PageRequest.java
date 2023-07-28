package com.example.myusercenterback.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:xxxxx
 * @create: 2023-07-19 14:12
 * @Description: 通用的分页请求类
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -5860707094194210842L;
    /**
     * 默认一页10个数据
     */

    protected int pageNum = 1;

    protected int pageSize = 10;

}
