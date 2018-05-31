package Uiautomation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppiumServerManager {
	/**
	 * star server
	 * 
	 * @param count
	 * @return
	 */
	
	public static List<Integer> startAppiumServer(int count) {
		int[] ports = radomPort(count);
		Properties prop = System.getProperties();
		List<Integer> successPorts = new ArrayList<Integer>();
		if (ports != null) {
			for (int i = 0; i < ports.length; i++) {
				int p = excuteCmd(ports[i]);
				if (p != 0) {
					successPorts.add(p);
				}
			}
		}
		if (prop.getProperty("os.name") != null
				&& prop.getProperty("os.name").indexOf("Mac") > -1) {
			test test1 = new test();
			String[] devicesport = test1.port.split(",");
			for (int i=0; i < devicesport.length; i++){
				successPorts.add(Integer.parseInt(devicesport[i]));
			}
			System.out.print(successPorts+"\r\n");
			return successPorts;
		}
		else{
			return successPorts;
		}
		
	}

	/**
	 * 执行cmd命令
	 * 
	 * @param port
	 */
	public static int excuteCmd(int port) {
		Runtime runtime = Runtime.getRuntime();
		Properties prop = System.getProperties();
		int defPort = 0;
		if (prop.getProperty("os.name") != null
				&& prop.getProperty("os.name").indexOf("Mac") > -1) {
			defPort = 0;
		}
		else{
			try {
				runtime.exec("cmd.exe /c start cmd.exe /k \"appium -a 127.0.0.1 -p "
						+ port
						+ " --session-override -dc \"{\"\"noReset\"\": \"\"true\"\"}\"\"");
				Thread.sleep(2000);
				defPort = port;
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				System.out.println("启动Appium失败，请检查本地端口是否存在。");
			}
			
		}
		return defPort;
		
	}

	/**
	 * 随机端口号
	 * 
	 * @param count
	 * @return
	 */
	public static int[] radomPort(int count) {
		if (count < 1) {
			System.out.println("count must be >0!!");
			return null;
		}
		int[] randoms = new int[count];
		for (int i = 0; i < count; i++) {
			int num = (int) (Math.random() * 4999) + 4000;
			randoms[i] = num;
		}
		return randoms;
	}

	public static void main(String[] args) {
		AppiumServerManager.startAppiumServer(1);
	}
}
