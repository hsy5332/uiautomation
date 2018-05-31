package Uiautomation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class GetDevices {


	public static Object[] getUdid() {
		List<String> deviceslist = new ArrayList<String>();
		Runtime runtime = Runtime.getRuntime();
		try {
			BufferedReader cmdadbdevices = new BufferedReader(
					new InputStreamReader(runtime.exec("adb devices")
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

		return deviceslist.toArray();
	}

	

}
