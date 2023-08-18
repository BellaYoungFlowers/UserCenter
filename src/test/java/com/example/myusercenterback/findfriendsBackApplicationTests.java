package com.example.myusercenterback;

import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class findfriendsBackApplicationTests {

	/**
	 * Java实现修改文件名的方法
	 */

	@Test
	void contextLoads() {
		File folder = new File("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\扣件病害图片");
		File[] files = folder.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				String oldName = file.getName();
				String newName = oldName.split("_")[0] + "kj" + ".jpg";
				File newFile = new File(folder.getPath() + File.separator + newName);
				if (file.renameTo(newFile)) {
					System.out.println(oldName + "重命名成功！");
				} else {
					System.out.println(oldName + "重命名失败！");
				}
			}
		}

		folder = new File("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨面病害图片");
		files = folder.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				String oldName = file.getName();
				String newName = oldName.split("_")[0] + "gm" + ".jpg";
				File newFile = new File(folder.getPath() + File.separator + newName);
				if (file.renameTo(newFile)) {
					System.out.println(oldName + "重命名成功！");
				} else {
					System.out.println(oldName + "重命名失败！");
				}
			}
		}


		folder = new File("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨道板病害图片");
		files = folder.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				String oldName = file.getName();
				String newName = oldName.split("_")[0] + "gdb" + ".jpg";
				File newFile = new File(folder.getPath() + File.separator + newName);
				if (file.renameTo(newFile)) {
					System.out.println(oldName + "重命名成功！");
				} else {
					System.out.println(oldName + "重命名失败！");
				}
			}
		}
	}


	/**
	 * java修改excel的内容
	 */
	@Test
	void testJava() throws IOException {
		// 打开 Excel 文件
		InputStream inp = new FileInputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨道板病害.xlsx");
		Workbook wb = WorkbookFactory.create(inp);

		// 获取第一个 Sheet 和第一个单元格
		Sheet sheet = wb.getSheetAt(0);
		int i =0;
		for (Row row : sheet) {
			if(i==0){
				i++;
				continue;
			}
			Cell cell = row.getCell(7);
			String oldname = cell.getStringCellValue();
			String newname = oldname.split("_")[0] + "gdb" + ".jpg";
			System.out.println(oldname);
			cell.setCellValue(newname);
			if(oldname.equals("1337_轨道板_异物.jpg")){
				break;
			}

		}

		// 将修改后的 Workbook 对象写入文件
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨道板病害.xlsx");
		wb.write(out);
		out.close();
	}

	@Test
	void testJava1() throws IOException {
		// 打开 Excel 文件
		InputStream inp = new FileInputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨面病害.xlsx");
		Workbook wb = WorkbookFactory.create(inp);

		// 获取第一个 Sheet 和第一个单元格
		Sheet sheet = wb.getSheetAt(0);
		int i =0;
		for (Row row : sheet) {
			if(i==0){
				i++;
				continue;
			}
			Cell cell = row.getCell(7);
			String oldname = cell.getStringCellValue();
			String newname = oldname.split("_")[0] + "gm" + ".jpg";
			System.out.println(oldname);
			cell.setCellValue(newname);
			if(oldname.equals("1649_轨面_波磨.jpg")){
				break;
			}
		}

		// 将修改后的 Workbook 对象写入文件
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\轨面病害.xlsx");
		wb.write(out);
		out.close();
	}


	@Test
	void testJava2() throws IOException {
		// 打开 Excel 文件
		InputStream inp = new FileInputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-草房上行\\区间管理\\全线数据\\成果数据\\总上行.xlsx");
		Workbook wb = WorkbookFactory.create(inp);

		// 获取第一个 Sheet 和第一个单元格
		Sheet sheet = wb.getSheetAt(0);
		int i =0;
		for (Row row : sheet) {
			if(i==0){
				i++;
				continue;
			}
			Cell cell = row.getCell(7);
			String oldname = cell.getStringCellValue();
			String newname = oldname.split("_")[0] + "kj" + ".jpg";
			System.out.println(oldname);
			cell.setCellValue(newname);

		}

		// 将修改后的 Workbook 对象写入文件
		FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\新建文件夹\\海淀五路居-金安桥下行\\区间管理\\全线数据\\成果数据\\扣件病害.xlsx");
		wb.write(out);
		out.close();
	}

}
