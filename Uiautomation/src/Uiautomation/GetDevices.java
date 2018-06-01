package Uiautomation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class GetDevices {


	public static Object[] getUdid() {
		List<String> deviceslist = new ArrayList<String>();
		Runtime runtime = Runtime.getRuntime();
		Properties prop = System.getProperties();
		String adbdevicecmd = "adb devices";
		if (prop.getProperty("os.name") != null&& prop.getProperty("os.name").indexOf("Mac") > -1) {
			try {
				BufferedReader cmdadbdevices = new BufferedReader(
						new InputStreamReader(runtime.exec("/volumes/software/work/eclipse/android/sdk/platform-tools/" + adbdevicecmd)
								.getInputStream()));
				String line = null;
			
				while ((line = cmdadbdevices.readLine()) != null) {
					if (line.indexOf(String.valueOf("device")) != -1) {
						line = line.toString().split("device")[0].replaceAll(" ",
								"").replaceAll("	", "");
						deviceslist.add(line);
						
					}
				}
				deviceslist.remove(0);
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else{
			try {
				BufferedReader cmdadbdevices = new BufferedReader(
						new InputStreamReader(runtime.exec(adbdevicecmd)
								.getInputStream()));
				String line = null;
			
				while ((line = cmdadbdevices.readLine()) != null) {
					if (line.indexOf(String.valueOf("device")) != -1) {
						line = line.toString().split("device")[0].replaceAll(" ",
								"").replaceAll("	", "");
						deviceslist.add(line);
					}
				}
				deviceslist.remove(0);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return deviceslist.toArray();
	}

	

}
