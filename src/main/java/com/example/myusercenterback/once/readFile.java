package com.example.myusercenterback.once;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:xxxxx
 * @create: 2023-06-29 09:56
 * @Description: easyExcel读取文件
 */
@Slf4j
public class readFile {

    public static void main(String[] args) {
        //文件名称是怎么定义的
        String fileName = "E:\\code\\findFriends\\myFindFriends\\findfriendsBack\\src\\main\\java\\com\\example\\myusercenterback\\once\\test.xlsx";
//        simpleReadListener(fileName);
//        simpleReadSync(fileName);
        readFile(fileName);
    }


    /**
     * 最简单的读 监听器读
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * 3. 直接读即可
     */
    public static void simpleReadListener(String filename) {

//        fileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(filename, DemoData.class, new DemoDataListener()).sheet().headRowNumber(0).doRead();

    }

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     * @param filename
     */
    public static void simpleReadSync(String filename) {
        List<DemoData> list = EasyExcel.read(filename).head(DemoData.class).sheet().headRowNumber(0).doReadSync();
        for (DemoData data : list) {
            log.info("读取到数据:{}", data);
        }
    }

    /**
     * 读取数据 判断有无重复的数据
     */
    public static void readFile(String filename){
        List<DemoData> list = EasyExcel.read(filename).head(DemoData.class).sheet().headRowNumber(0).doReadSync();
        System.out.println("总数是"+list.size());

        Map<Integer, List<DemoData>> collect = list.stream()
                .filter(item-> ObjectUtils.isNotEmpty(item.getGender()))
                .collect(Collectors.groupingBy(DemoData::getGender));


        System.out.println(collect);

        System.out.println("合并后的size"+collect.keySet().size());
        for (Map.Entry<Integer, List<DemoData>> integerListEntry : collect.entrySet()) {
            if(integerListEntry.getValue().size()>1){
                System.out.println("性别是: "+integerListEntry.getKey());
            }
        }
    }
}


