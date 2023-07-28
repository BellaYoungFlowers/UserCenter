package com.example.myusercenterback.excelToWord.test;

import com.alibaba.excel.EasyExcel;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.HackLoopTableRenderPolicy;
import com.example.myusercenterback.excelToWord.model.FieldData;
import com.example.myusercenterback.excelToWord.model.TableData;
import com.example.myusercenterback.once.DemoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @author:xxxxx
 * @create: 2023-07-06 14:00
 * @Description: 测试
 */
@RestController
@Slf4j
public class TestExcelToWord {


    /**
     * 销售订单信息导出word --- poi-tl（包含动态行表格、循环列表中的动态行表格）
     *
     * @throws IOException
     */
    @RequestMapping("/exportDataWord4")
    public void exportDataWord4(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Map<String, Object> params = new HashMap<>();
            //表数据
            List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 3; i++) {
                Map<String, Object> detailMap = new HashMap<String, Object>();
                detailMap.put("index", i + 1);//序号
                detailMap.put("product_type", "二级分类1");//商品二级分类
                detailMap.put("title", "商品" + i);//商品名称
                detailMap.put("product_description", "套");//商品规格
                detailMap.put("buy_num", 3 + i);//销售数量
                detailMap.put("saleprice", 100 + i);//销售价格
                detailMap.put("technical_parameter", "技术参数" + i);//技术参数
                detailList.add(detailMap);
            }

            //表头
            List<Map<String, Object>> tList = new ArrayList<Map<String, Object>>();
            Map<String, Object> tMap = new HashMap<String, Object>();
            tMap.put("index", 1);
            tMap.put("sub_type", "监督技术装备");
            tMap.put("detailList", detailList);
            tMap.put("buy_price", 100);
            tList.add(tMap);

            tMap = new HashMap<String, Object>();
            tMap.put("index", 2);
            tMap.put("sub_type", "火灾调查装备");
            tMap.put("detailList", detailList);
            tMap.put("buy_price", 200);
            tList.add(tMap);

            tMap = new HashMap<String, Object>();
            tMap.put("index", 3);
            tMap.put("sub_type", "工程验收装备");
            tMap.put("detailList", detailList);
            tMap.put("buy_price", 300);
            tList.add(tMap);


            ClassPathResource classPathResource = new ClassPathResource("test.docx");
            String resource = classPathResource.getURL().getPath();

            //渲染表格  动态行
            HackLoopTableRenderPolicy policy = new HackLoopTableRenderPolicy();
            Configure config = Configure.newBuilder().bind("detailList", policy).build();

            XWPFTemplate template = XWPFTemplate.compile(resource, config).render(
                    new HashMap<String, Object>() {{
                        put("typeProducts", tList);
                    }}
            );
            //=================生成文件保存在本地D盘某目录下=================
            String temDir = "D:/mimi/" + File.separator + "file/word/";//生成临时文件存放地址
            File filedir = new File(temDir);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            //生成文件名
            Long time = new Date().getTime();
            // 生成的word格式
            String formatSuffix = ".docx";
            // 拼接后的文件名
            String fileName = time + formatSuffix;//文件名  带后缀

            FileOutputStream fos = new FileOutputStream(temDir + fileName);
            template.write(fos);
            //=================生成word到设置浏览默认下载地址=================
            // 设置强制下载不打开
            response.setContentType("application/force-download");
            // 设置文件名
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            OutputStream out = response.getOutputStream();
            template.write(out);
            out.flush();
            out.close();
            template.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @RequestMapping("/exportDataWord")
    public void exportDataWord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //读取文件数据
        String fileName1 = "E:\\code\\findFriends\\myFindFriends\\findfriendsBack\\src\\main\\resources\\table.xls";
        List<Map<String, String>> listMap = EasyExcel.read(fileName1).sheet().headRowNumber(1).doReadSync();

        List<TableData> tList = new ArrayList<>();
        List<FieldData> tableDataList = new ArrayList<>();
        int tableIndex = 0;
        int fieldIndex = 0;
        String temptableName = listMap.get(0).get(0).toString();//保存需要对比的表格名
        String tableName = "";  //记录的表数据
        String tableComment = "";
        int lastTableIndex = 0;//检查是否是最后一个
        for (Map data : listMap) {
            String currnetTableName = data.get(0).toString();
            if(temptableName.equals(currnetTableName)){
                FieldData fieldData = new FieldData();
                fieldData.setFieldComment(data.get(6)==null?"":data.get(6).toString());//备注 fieldComment
                fieldData.setFieldName(data.get(2)==null?"":data.get(2).toString());//列名 fieldName
                fieldData.setFieldLength(data.get(4)==null?"":data.get(4).toString());//长度 Length
                fieldData.setFieldType(data.get(3)==null?"":data.get(3).toString());//字段类型 type
                fieldData.setFieldIsNull(data.get(5)==null?"":data.get(5).toString().toLowerCase());//是否为空 isNull
                fieldData.setFieldIndex(fieldIndex);
                fieldIndex++;
                tableDataList.add(fieldData);
                tableComment = data.get(1)==null?"":data.get(1).toString();  //记录表的数据
                tableName = data.get(0)==null?"":data.get(0).toString();
            }else{
                temptableName = currnetTableName;
                fieldIndex = 0;
                TableData tableData = new TableData();
                tableData.setTableComment(tableComment); //表名注释 tableComment
                tableData.setTableIndex(tableIndex);
                tableIndex++;
                tableData.setTableName(tableName); //表名 tableName
                tableData.setTableDataList(tableDataList);
                tableDataList = new ArrayList<>();
                tList.add(tableData);
            }

            lastTableIndex++;
            //读取到最后一条
            if(lastTableIndex == listMap.size()) {
                TableData tableData = new TableData();
                tableData.setTableComment(tableComment); //表名注释 tableComment
                tableData.setTableIndex(tableIndex);
                tableData.setTableName(tableName); //表名 tableName
                tableData.setTableDataList(tableDataList);
                tList.add(tableData);
            }
        }

        try {
            ClassPathResource classPathResource = new ClassPathResource("temp.docx");
            String resource = classPathResource.getURL().getPath();

            //渲染表格  动态行
            HackLoopTableRenderPolicy policy = new HackLoopTableRenderPolicy();
            Configure config = Configure.newBuilder().bind("tableDataList", policy).build();
            XWPFTemplate template = XWPFTemplate.compile(resource, config).render(
                    new HashMap<String, Object>() {{
                        put("typeProducts", tList);
                    }}
            );
            //=================生成文件保存在本地D盘某目录下=================
            String temDir = "D:/mimi/" + File.separator + "file/word/";//生成临时文件存放地址
            File filedir = new File(temDir);
            if (!filedir.exists()) {
                boolean mkdirs = filedir.mkdirs();
                if(mkdirs){
                    //生成文件名
                    Long time = new Date().getTime();
                    // 生成的word格式
                    String formatSuffix = ".docx";
                    // 拼接后的文件名
                    String fileName = time + formatSuffix;//文件名  带后缀

                    FileOutputStream fos = new FileOutputStream(temDir + fileName);
                    template.write(fos);
                    //=================生成word到设置浏览默认下载地址=================
                    // 设置强制下载不打开
                    response.setContentType("application/force-download");
                    // 设置文件名
                    response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                    OutputStream out = response.getOutputStream();
                    template.write(out);
                    out.flush();
                    out.close();
                    template.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

