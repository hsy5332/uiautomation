package Uiautomation;


import io.appium.java_client.android.AndroidDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import Uiautomation.singleCaseProcess;
import Uiautomation.allCaseProcess;
import Uiautomation.test;



class RunnableDemo implements Runnable {
	   private static AndroidDriver driver; 
	   private Thread t;
	   private String threadName;
	   private String executeDevicename;
	   private String executeUdid;
	   private String executePort;
	   private String appPackages;
	   private String appActivitys;
	   private String platformNames;
	   private String platformVersions;
	   private String appPath;
	   
	   
	   RunnableDemo( String name, String deviceName,String udid, String port, String appPackage, String appActivity, String platformName,String platformVersion,String appPaths) {
	      threadName = name;
	      executeDevicename = deviceName;
	      executeUdid = udid;
	      executePort = port;
	      appPackages = appPackage;
	      appPath = appPaths;
	      appActivitys = appActivity;
	      platformNames = platformName;
	      platformVersions = platformVersion;
	      // appium 需要的参数进行重新赋值
	      System.out.print("设备："+executeDevicename+",设备UDID："+ executeUdid + ",设备端口号：" + executePort +"\r\n");
	   }
	      
	      
	   public void run() {
		  List<WebElement> bot;
		  String actRes;
		  boolean acceptNextAlert = true;
		  StringBuffer verificationErrors = new StringBuffer();
		  int totalCaseNumbers;
		  List<Object[]> records = new ArrayList<Object[]>();
		  String testCaseResult="";
		  test test1=new test();
		  try {
			  test.testmethod();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		  
		  File allTest, singleTest;
		  String result3= test1.str3;//读取环境配置信息：test.preperites
		  String result1= test1.str1;
		  allTest = new File(result3+"测试用例集.xlsx");
		  if(!allTest.exists()){
				System.out.println("测试用例集文件不存在");
				return;
			}
			allCaseProcess allCP = new allCaseProcess();
			try {
				allCP.writeallHeader();//生成总体测试报告的标题行
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		  try {
				records = allCP.getallTests(allTest);//读取所有测试用例，返回所有用例的数据信息
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		  
		  totalCaseNumbers = records.size();
		  DesiredCapabilities capabilities = new DesiredCapabilities();
		//配置连接的设备信息
		  capabilities.setCapability("deviceName",executeDevicename); 
		  capabilities.setCapability("udid",executeUdid); 
		  capabilities.setCapability("platformName",platformNames);  
		  capabilities.setCapability("platformVersion",platformVersions);
		  capabilities.setCapability("app", appPath);
		  capabilities.setCapability("appPackage", appPackages);
		  capabilities.setCapability("appActivity", appActivitys);
		  capabilities.setCapability("unicodeKeyboard", true);
		  capabilities.setCapability("resetKeyboard", true);
		  capabilities.setCapability("noReset", "true");
		  try{
			  String connectlink = "http://127.0.0.1:"+executePort+"/wd/hub";
			  driver = new AndroidDriver(new URL(connectlink), capabilities);
			  driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
			  for (int i=0; i<totalCaseNumbers; i++){
				  Object[] value = records.get(i);
				  String testCaseName = (String) value[1];//获取测试用例配置文件中的测试用例
				  String whetherExec = (String) value[2];
				    whetherExec.trim();
				    if (whetherExec.equals("y") ||  whetherExec.equals("Y"))
				    {
						singleTest = new File(result1+testCaseName+".xlsx");   //要不要执行该测试用例
						if(!singleTest.exists()){
							System.out.println("文件"+testCaseName+"不存在");
							return;
						}
						    singleCaseProcess sCP = new singleCaseProcess();
						    System.out.println("设备:"+executeDevicename+"执行测试用例: "+testCaseName);
						    sCP.setCaseSequence(testCaseName); //根据case名称，设置数据文件中对应的行号
							try {
								sCP.processHandle(singleTest, driver, testCaseName,executeDevicename);
							} catch (IOException e) {
								e.printStackTrace();
							}
							String execResult = sCP.getCaseExecResult();
							allCP.writeAllResult(execResult, i);
				    }
			  }
			  String uninstallcmd = "adb -s " + executeUdid +" uninstall "+ appPackages;
			  try {
				  Properties prop = System.getProperties();
				  if (prop.getProperty("os.name") !=null && prop.getProperty("os.name").indexOf("Mac") > -1){ //判断是否为Mac系统
					  String executecmd = "lsof -i -P";
					  String line=null;
					  String[] executecmdarray = new String[10] ;
					  String[] pidlistarray = new String[10] ;
					  StringBuffer cmdresults = new StringBuffer();
					  Runtime runtime = Runtime.getRuntime();
					  BufferedReader cmdlines = new BufferedReader(new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					  int count = 0;
					  while((line = cmdlines.readLine())!=null){
						  	if (line.indexOf(executePort) != -1){
						  		if(line.indexOf("LISTEN") != -1){
						  			cmdresults.append(line);
							  		String cmdresult = cmdresults.toString().replaceAll(" ", ""); //删除cmd命令结果多余的空格
							  		executecmdarray[count] = cmdresult;
							  		pidlistarray[count] = executecmdarray[0].split("steel")[0].substring(4);
							  		count = count + 1;
						  		}
						  	}
					  }
					  System.out.print("关闭设备："+ executeDevicename + " Appium" + "\r\n");
					  new BufferedReader(new InputStreamReader(runtime.exec("kill  "+ pidlistarray[0]).getInputStream())); //关闭Appium的连接端口
				  }
				  else{
					  String executecmd = "netstat -aon";
					  String line=null;
					  String[] executecmdarray = new String[10] ;
					  String[] pidlistarray = new String[10] ;
					  StringBuffer cmdresults = new StringBuffer();
					  Runtime runtime = Runtime.getRuntime();
					  BufferedReader cmdlines = new BufferedReader(new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					  int count = 0;
					  while((line = cmdlines.readLine())!=null){
						  	if (line.indexOf(executePort) != -1){
						  		cmdresults.append(line);
						  		String cmdresult = cmdresults.toString().replaceAll(" ", ""); //删除cmd命令结果多余的空格
						  		executecmdarray[count] = cmdresult;
						  		pidlistarray[count] = executecmdarray[count].split("LISTENING")[1];
						  		count = count + 1;
						  	}
					  }
					  System.out.print("关闭设备："+ executeDevicename + " Appium" + "\r\n");
					  new BufferedReader(new InputStreamReader(runtime.exec("taskkill -f -pid "+ pidlistarray[0]).getInputStream())); //关闭Appium的连接端口
				  }
			  }catch (Exception e){
				  e.printStackTrace();
				  System.out.println("关闭Appium失败，Appium连接设备是："+ executeUdid);
			  }
		  }catch (Exception e) {
			  	//e.printStackTrace();
				System.out.println("连接设备出错，请检查端口号和连接的设备udid,应该连接的设备是：" + executeUdid + "，端口号："+ executePort);
				try {
					Properties prop = System.getProperties();
					  if (prop.getProperty("os.name") !=null && prop.getProperty("os.name").indexOf("Mac") > -1){ //判断是否为Mac系统
						  String executecmd = "lsof -i -P";
						  String line=null;
						  String[] executecmdarray = new String[10] ;
						  String[] pidlistarray = new String[10] ;
						  StringBuffer cmdresults = new StringBuffer();
						  Runtime runtime = Runtime.getRuntime();
						  BufferedReader cmdlines = new BufferedReader(new InputStreamReader(runtime.exec(executecmd).getInputStream()));
						  int count = 0;
						  while((line = cmdlines.readLine())!=null){
							  	if (line.indexOf(executePort) != -1){
							  		if(line.indexOf("LISTEN") != -1){
							  			cmdresults.append(line);
								  		String cmdresult = cmdresults.toString().replaceAll(" ", ""); //删除cmd命令结果多余的空格
								  		executecmdarray[count] = cmdresult;
								  		pidlistarray[count] = executecmdarray[0].split("steel")[0].substring(4);
								  		count = count + 1;
							  		}
							  	}
						  }
						  System.out.print("关闭设备："+ executeDevicename + " Appium" + "\r\n");
						  new BufferedReader(new InputStreamReader(runtime.exec("kill  "+ pidlistarray[0]).getInputStream())); //关闭Appium的连接端口
					  }
					  else{
						  String executecmd = "netstat -aon";
						  String line=null;
						  String[] executecmdarray = new String[10] ;
						  String[] pidlistarray = new String[10] ;
						  StringBuffer cmdresults = new StringBuffer();
						  Runtime runtime = Runtime.getRuntime();
						  BufferedReader cmdlines = new BufferedReader(new InputStreamReader(runtime.exec(executecmd).getInputStream()));
						  int count = 0;
						  while((line = cmdlines.readLine())!=null){
							  	if (line.indexOf(executePort) != -1){
							  		cmdresults.append(line);
							  		String cmdresult = cmdresults.toString().replaceAll(" ", ""); //删除cmd命令结果多余的空格
							  		executecmdarray[count] = cmdresult;
							  		pidlistarray[count] = executecmdarray[count].split("LISTENING")[1];
							  		count = count + 1;
							  	}
						  }
						  System.out.print("关闭设备："+ executeDevicename + " Appium" + "\r\n");
						  new BufferedReader(new InputStreamReader(runtime.exec("taskkill -f -pid "+ pidlistarray[0]).getInputStream())); //关闭Appium的连接端口
					  }
				  }catch (Exception e1){
					  e.printStackTrace();
					  System.out.println("关闭Appium失败，Appium端口号是："+ executeUdid);
				  }
			}
	   }
	   
	   public void start () {
		      if (t == null) {
		         t = new Thread (this, threadName);
		         t.start ();
		      }
	}
	   }
	 
 class runAutomation {
	 public static void main(String args[]) {
		 
		 test test1=new test();
		  try {
			test.testmethod();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		      String[] deviceUdid = test1.str8.split(",");
		      String[] connectPort = test1.port.split(",");
			  if (connectPort.length > deviceUdid.length){
					connectPort = Arrays.copyOfRange(connectPort,0,deviceUdid.length);
				}
			  if (deviceUdid.length > connectPort.length){
				  deviceUdid = Arrays.copyOfRange(deviceUdid,0,connectPort.length);
				}
			  String appPackage = test1.appPackage;
			  String appActivity = test1.str11;
			  String platformName = "Android";
			  String platformVersion = test1.str9;
			  String appPaths = test.str10;
			  // 读取Test.properties 配置信息
			  
		      if (deviceUdid.length == 1 ){
		    	  RunnableDemo oneDeviceUdid = new RunnableDemo(deviceUdid [0],deviceUdid[0],deviceUdid[0],connectPort[0],appPackage,appActivity,platformName,platformVersion,appPaths);
		    	  oneDeviceUdid.start();
		    	  //一个设备或只有一个端口时，创建一个线程进行运行
		      }
		      else{
				  RunnableDemo executedevices[] = new RunnableDemo[deviceUdid.length];
				  int i = 0;
				  while (i < deviceUdid.length){
					  executedevices[i] = new RunnableDemo(deviceUdid[i],deviceUdid[i],deviceUdid[i],connectPort[i],appPackage,appActivity,platformName,platformVersion,appPaths);
					  executedevices[i].start();
					  try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					  i = i + 1;
				  }
				//多设备或只有多端口时，创建多线程进行运行
		      }
	 }
}




