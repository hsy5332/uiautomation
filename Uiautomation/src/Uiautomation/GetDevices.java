package Uiautomation;

import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class GetDevices {


	public static Object[] getUdid() {
		Object[] device;
		AndroidDebugBridge.init(false); // 很重要
		device = getDevices();
		if (device!=null) {
			for (int i = 0; i < device.length; i++) {
				System.out.println(device[i].toString());
			}
		}
		
		return device;
	}

	public static Object[] getDevices() {
		AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();
		waitDevicesList(bridge);
		IDevice devices[] = bridge.getDevices();
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < devices.length; i++) {
			if (devices[i].isOnline()) {
				list.add(devices[i].toString());
			}
			System.out.println("设备名称：-------" + devices[i].toString()
					+ "---isonline---" + devices[i].isOnline());
			System.out.println(devices[0].getFileListingService().getRoot());
		}

		return list.toArray();
	}

	public static void waitDevicesList(AndroidDebugBridge bridge) {
		int count = 0;
		while (bridge.hasInitialDeviceList() == false) {
			try {
				Thread.sleep(500);
				count++;
			} catch (InterruptedException e) {
			}
			if (count > 60) {
				System.err.print("等待获取设备超时");
				break;
			}
		}
	}

}
