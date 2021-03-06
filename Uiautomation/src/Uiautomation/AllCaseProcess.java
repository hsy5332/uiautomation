package Uiautomation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AllCaseProcess {
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();


	private int totalCaseNumber;
	private List<Object[]> records = new ArrayList<Object[]>();
	String value[], reportValue[];
	Cell cell0;
	String excelPath;

  test test1=new test();
  //createExcel excelall = new createExcel();
	ExcelOperation eOP = new ExcelOperation();
	
public  void writeAllResult(String execResult, int i) {
	try {
		test.testmethod();
		String result1= test1.str4;
		//String excelPath=result1+"总体报告"+".xlsx";	
		File file = new File(excelPath);
		InputStream fis = new FileInputStream(file);
		OPCPackage opcPackage = OPCPackage.open(fis);
		Workbook wb = new XSSFWorkbook(opcPackage);
		Sheet sheet = wb.getSheetAt(0);
	    sheet.setColumnWidth(0, 30 * 256);//讲第二列宽度设为30个字符宽度
//			sheet.setColumnWidth(5, 40 * 256);//讲第6列宽度设为40个字符宽度
		//Row row = (Row) sheet.getRow(0);
		FileOutputStream out = new FileOutputStream(excelPath);
		Row row = sheet.createRow((sheet.getLastRowNum() + 1));
		
		//int rowNum = sheet.getLastRowNum();
		reportValue = (String[]) records.get(i);
	   cell0 = row.createCell(0, Cell.CELL_TYPE_STRING);
       cell0.setCellValue(reportValue[0]);//用例编号
       cell0 = row.createCell(1, Cell.CELL_TYPE_STRING);
       cell0.setCellValue(reportValue[1]);//用例名称
       cell0 = row.createCell(2, Cell.CELL_TYPE_STRING);
       cell0.setCellValue(execResult);//执行结果
       cell0 = row.createCell(3, Cell.CELL_TYPE_STRING);
       cell0.setCellValue(reportValue[3]);//用例描述
    	//Cell cell0 = row.createCell(0, Cell.CELL_TYPE_STRING);
        //cell0.setCellValue(tag);
        
//			for (int i = 0; i < inputFile.length; i++) {
//				if (i == 0) {
//				Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
//					cell.setCellValue(rowNum);
//				}
//				else {
//					Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
//				//	cell.setHyperlink((XSSFHyperlink) cell.getHyperlink());
//					cell.setCellValue(inputFile[i]);
//				}
//				}
		out.flush();
		wb.write(out);
		out.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
public void writeallHeader(String device) throws IOException{

		 test1.testmethod();
		 String result4= test1.str4;//获取测试报告的生成路径
		 excelPath=result4+device+"-总体测试报告"+String.valueOf(DateUtil.getYear(eOP.date))+"-"+String.valueOf(DateUtil.getMonth(eOP.date))+"-"+String.valueOf(DateUtil.getDay(eOP.date))+"-"+String.valueOf(DateUtil.getHour(eOP.date))+"-"+String.valueOf(DateUtil.getMinute(eOP.date))+".xlsx";
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("testdata1");
		FileOutputStream outputStream = new FileOutputStream(excelPath);
		try {
			Row row0 = sheet.createRow(0);
			String[] headers = new String[] { "编号", "测试用例名称", "测试结果", "测试用例用途描述"};
			for (int i = 0; i < headers.length; i++) {
				Cell cell_1 = row0.createCell(i, Cell.CELL_TYPE_STRING);

				CellStyle style = eOP.getStyle(workbook);
				cell_1.setCellStyle(style);
				cell_1.setCellValue(headers[i]);//完成写excel的操作
				sheet.autoSizeColumn(i);
			}
           workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


public List<Object[]> getallTests(File file) throws IOException{

		try
		{
		InputStream inputStream = new FileInputStream(file);
		//List<Object[]> records = new ArrayList<Object[]> ();
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}//解析xlsx格式
		
		Sheet sheet=workbook.getSheet("Sheet1");//第一个工作表
		totalCaseNumber=sheet.getLastRowNum()-sheet.getFirstRowNum();//sheet1数据的行数

		for(int i=1;i<= totalCaseNumber; i++)
		{

			
			Row row = sheet.getRow(i);//获取行对象
			
			value = new String [row.getLastCellNum()];//声明字符串数组存放每行数据
			
			row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
			value[0]=row.getCell(0).getStringCellValue();//步骤编号
			value[1] = row.getCell(1).getStringCellValue();//测试用例名称
			value[2] = row.getCell(2).getStringCellValue();//是否要执行该用例
			value[3] = row.getCell(3).getStringCellValue();//测试用例用途描述
			//row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
			records.add(value);
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return records;
	}

}
