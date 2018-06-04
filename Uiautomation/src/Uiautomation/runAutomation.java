package Uiautomation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;

class RunnableDemo implements Runnable {
	private static AndroidDriver driver;
	private Thread t;
	private String threadName;
	private String executeDevicename;
	private String executeUdid;
	private int executePort;
	private String appPackages;
	private String appActivitys;
	private String platformNames;
	private String platformVersions;
	private String appPath;

	RunnableDemo(String name, String deviceName, String udid, int port, String appPackage, String appActivity,
			String platformName, String platformVersion, String appPaths) {
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
		System.out.print("设备：" + executeDevicename + ",设备UDID：" + executeUdid + ",设备端口号：" + executePort + "\r\n");
	}

	public void run() {
		List<WebElement> bot;
		String actRes;
		String uninstallcmd = "adb -s " + executeUdid + " uninstall " + appPackages;
		boolean acceptNextAlert = true;
		StringBuffer verificationErrors = new StringBuffer();
		int totalCaseNumbers;
		List<Object[]> records = new ArrayList<Object[]>();
		String testCaseResult = "";
		test test1 = new test();
		try {
			test.testmethod();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File allTest, singleTest;
		String result3 = test1.str3;// 读取环境配置信息：test.preperites
		String result1 = test1.str1;
		allTest = new File(result3 + "测试用例集.xlsx");
		if (!allTest.exists()) {
			System.out.println("测试用例集文件不存在");
			return;
		}
		allCaseProcess allCP = new allCaseProcess();
		try {
			allCP.writeallHeader();// 生成总体测试报告的标题行
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			records = allCP.getallTests(allTest);// 读取所有测试用例，返回所有用例的数据信息
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		totalCaseNumbers = records.size();
		DesiredCapabilities capabilities = new DesiredCapabilities();
		// 配置连接的设备信息
		capabilities.setCapability("deviceName", executeDevicename);
		capabilities.setCapability("udid", executeUdid);
		capabilities.setCapability("platformName", platformNames);
		capabilities.setCapability("platformVersion", platformVersions);
		capabilities.setCapability("app", appPath);
		capabilities.setCapability("appPackage", appPackages);
		capabilities.setCapability("appActivity", appActivitys);
		capabilities.setCapability("unicodeKeyboard", "true");
		capabilities.setCapability("resetKeyboard", "true");
		capabilities.setCapability("noReset", "true");
		capabilities.setCapability("noSign", "true");
		try {
			String excutesystem = test1.excutesystem;
			String linuxip = test1.linuxip;
			String connectlink;
			if (excutesystem.indexOf("linux") != -1) {
				connectlink = linuxip + ":" + executePort + "/wd/hub";
				System.out.print(connectlink);
			} else {
				connectlink = "http://127.0.0.1:" + executePort + "/wd/hub";
			}
			driver = new AndroidDriver(new URL(connectlink), capabilities);
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			for (int i = 0; i < totalCaseNumbers; i++) {
				Object[] value = records.get(i);
				String testCaseName = (String) value[1];// 获取测试用例配置文件中的测试用例
				String whetherExec = (String) value[2];
				whetherExec.trim();
				if (whetherExec.equals("y") || whetherExec.equals("Y")) {
					singleTest = new File(result1 + testCaseName + ".xlsx"); // 要不要执行该测试用例
					if (!singleTest.exists()) {
						System.out.println("文件" + testCaseName + "不存在");
						return;
					}
					singleCaseProcess sCP = new singleCaseProcess();
					System.out.println("设备:" + executeDevicename + "执行测试用例: " + testCaseName);
					sCP.setCaseSequence(testCaseName); // 根据case名称，设置数据文件中对应的行号
					try {
						sCP.processHandle(singleTest, driver, testCaseName, executeDevicename);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String execResult = sCP.getCaseExecResult();
					allCP.writeAllResult(execResult, i);
				}
			}
			try {
				Properties prop = System.getProperties();
				if (prop.getProperty("os.name") != null && prop.getProperty("os.name").indexOf("Mac") > -1) { // 判断是否为Mac系统
					String executecmd = "lsof -i -P";
					String line = null;
					String[] executecmdarray = new String[10];
					String[] pidlistarray = new String[10];
					StringBuffer cmdresults = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					BufferedReader cmdlines = new BufferedReader(
							new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					int count = 0;
					while ((line = cmdlines.readLine()) != null) {
						if (line.indexOf(String.valueOf(executePort)) != -1) {
							if (line.indexOf("LISTEN") != -1) {
								cmdresults.append(line);
								String cmdresult = cmdresults.toString().replaceAll(" ", ""); // 删除cmd命令结果多余的空格
								executecmdarray[count] = cmdresult;
								pidlistarray[count] = executecmdarray[0].split("steel")[0].substring(4);
								count = count + 1;
							}
						}
					}
					System.out.print("卸载" + executeUdid + " APP" + "\r\n");
					new BufferedReader(new InputStreamReader(
							runtime.exec("/volumes/software/work/eclipse/android/sdk/platform-tools/" + uninstallcmd)
									.getInputStream()));// 卸载APP
					System.out.print("关闭设备：" + executeDevicename + " Appium" + "\r\n");
					new BufferedReader(
							new InputStreamReader(runtime.exec("kill  " + pidlistarray[0]).getInputStream())); // 关闭Appium的连接端口
				} else {
					String executecmd = "netstat -aon";
					String line = null;
					String[] executecmdarray = new String[10];
					String[] pidlistarray = new String[10];
					StringBuffer cmdresults = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					BufferedReader cmdlines = new BufferedReader(
							new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					int count = 0;
					while ((line = cmdlines.readLine()) != null) {
						if (line.indexOf(String.valueOf(executePort)) != -1) {
							cmdresults.append(line);
							String cmdresult = cmdresults.toString().replaceAll(" ", ""); // 删除cmd命令结果多余的空格
							executecmdarray[count] = cmdresult;
							if (executecmdarray[count].split("LISTENING").length < 2) {

								pidlistarray[count] = executecmdarray[count].split("LISTENING")[0];
							} else {
								pidlistarray[count] = executecmdarray[count].split("LISTENING")[1];
							}
							count = count + 1;
						}
					}
					System.out.print("卸载" + executeUdid + " APP" + "\r\n");
					new BufferedReader(new InputStreamReader(runtime.exec(uninstallcmd).getInputStream()));// 卸载APP
					System.out.print("关闭设备：" + executeDevicename + " Appium" + "\r\n");
					new BufferedReader(new InputStreamReader(
							runtime.exec("taskkill -f -pid " + pidlistarray[0]).getInputStream())); // 关闭Appium的连接端口
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("关闭Appium失败，Appium连接设备是：" + executeUdid);
			}
		} catch (Exception e) {
			System.out.println("连接设备出错，请检查端口号和连接的设备udid,应该连接的设备是：" + executeUdid + "，端口号：" + executePort);
			try {
				Properties prop = System.getProperties();
				if (prop.getProperty("os.name") != null && prop.getProperty("os.name").indexOf("Mac") > -1) { // 判断是否为Mac系统
					String executecmd = "lsof -i -P";
					String line = null;
					String[] executecmdarray = new String[10];
					String[] pidlistarray = new String[10];
					StringBuffer cmdresults = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					BufferedReader cmdlines = new BufferedReader(
							new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					int count = 0;
					while ((line = cmdlines.readLine()) != null) {
						if (line.indexOf(String.valueOf(executePort)) != -1) {
							if (line.indexOf("LISTEN") != -1) {
								cmdresults.append(line);
								String cmdresult = cmdresults.toString().replaceAll(" ", ""); // 删除cmd命令结果多余的空格
								executecmdarray[count] = cmdresult;
								pidlistarray[count] = executecmdarray[0].split("steel")[0].substring(4);
								count = count + 1;
							}
						}
					}
					System.out.print("卸载" + executeUdid + " APP" + "\r\n");
					new BufferedReader(new InputStreamReader(
							runtime.exec("/volumes/software/work/eclipse/android/sdk/platform-tools/" + uninstallcmd)
									.getInputStream()));// 卸载APP
					System.out.print("关闭设备：" + executeDevicename + " Appium" + "\r\n");
					new BufferedReader(
							new InputStreamReader(runtime.exec("kill  " + pidlistarray[0]).getInputStream())); // 关闭Appium的连接端口
				} else {
					String executecmd = "netstat -aon";
					String line = null;
					String[] executecmdarray = new String[10];
					String[] pidlistarray = new String[10];
					StringBuffer cmdresults = new StringBuffer();
					Runtime runtime = Runtime.getRuntime();
					BufferedReader cmdlines = new BufferedReader(
							new InputStreamReader(runtime.exec(executecmd).getInputStream()));
					int count = 0;
					while ((line = cmdlines.readLine()) != null) {
						if (line.indexOf(String.valueOf(executePort)) != -1) {
							cmdresults.append(line);
							String cmdresult = cmdresults.toString().replaceAll(" ", ""); // 删除cmd命令结果多余的空格
							executecmdarray[count] = cmdresult;
							pidlistarray[count] = executecmdarray[count].split("LISTENING")[1];
							count = count + 1;
						}
					}
					System.out.print("卸载" + executeUdid + " APP" + "\r\n");
					new BufferedReader(new InputStreamReader(runtime.exec(uninstallcmd).getInputStream()));// 卸载APP
					System.out.print("关闭设备：" + executeDevicename + " Appium" + "\r\n");
					new BufferedReader(new InputStreamReader(
							runtime.exec("taskkill -f -pid " + pidlistarray[0]).getInputStream())); // 关闭Appium的连接端口
				}
			} catch (Exception e1) {
				e.printStackTrace();
				System.out.println("关闭Appium失败，Appium端口号是：" + executeUdid);
			}
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}
}

class runAutomation {
	public static void main(String args[]) {

		test test1 = new test();
		try {
			test.testmethod();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Object[] deviceUdid = GetDevices.getUdid();

		if (deviceUdid == null || deviceUdid.length < 1) {
			System.out.println("无设备连接");
			return;
		}
		Object[] connectPort = (Object[]) AppiumServerManager.startAppiumServer(deviceUdid.length).toArray();

		// 判断设备数是否大于端口数 deviceUdid.length 设备数
		if (deviceUdid.length > connectPort.length) {
			Object[] realdeviceUdid = new Object[connectPort.length];
			for (int deviceUdidcount = 0; deviceUdidcount < connectPort.length; deviceUdidcount++) {
				realdeviceUdid[deviceUdidcount] = deviceUdid[deviceUdidcount];
			}
			deviceUdid = realdeviceUdid;
		}

		if (deviceUdid.length < connectPort.length) {
			Object[] realconnectPort = new Object[deviceUdid.length];
			for (int connectPortcount = 0; connectPortcount < deviceUdid.length; connectPortcount++) {
				realconnectPort[connectPortcount] = connectPort[connectPortcount];
			}
			connectPort = realconnectPort;

		}

		String appPackage = test1.appPackage;
		String appActivity = test1.str11;
		String platformName = "Android";
		String platformVersion = test1.str9;
		String appPaths = test.str10;
		// 读取Test.properties 配置信息

		if (deviceUdid.length == 1) {
			RunnableDemo oneDeviceUdid = new RunnableDemo(String.valueOf(deviceUdid[0]), String.valueOf(deviceUdid[0]),
					String.valueOf(deviceUdid[0]), (int) connectPort[0], appPackage, appActivity, platformName,
					platformVersion, appPaths);
			oneDeviceUdid.start();
			// 一个设备或只有一个端口时，创建一个线程进行运行
		} else {
			RunnableDemo executedevices[] = new RunnableDemo[deviceUdid.length];
			int i = 0;
			while (i < deviceUdid.length) {
				executedevices[i] = new RunnableDemo(String.valueOf(deviceUdid[i]), String.valueOf(deviceUdid[i]),
						String.valueOf(deviceUdid[i]), (int) connectPort[i], appPackage, appActivity, platformName,
						platformVersion, appPaths);
				executedevices[i].start();
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i = i + 1;
			}
			// 多设备或只有多端口时，创建多线程进行运行
		}
	}
}
