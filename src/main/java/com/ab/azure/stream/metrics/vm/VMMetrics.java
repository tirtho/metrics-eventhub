package com.ab.azure.stream.metrics.vm;

import com.ab.azure.stream.metrics.Metrics;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

public class VMMetrics implements Metrics {

//	private OperatingSystemMXBean osMXBean;

	private SystemInfo sysInfo;
	private HardwareAbstractionLayer hardware;
	private FileSystem fs;
	private OperatingSystem os;
	
	public VMMetrics() {
		super();
//		osMXBean = ManagementFactory.getOperatingSystemMXBean();
		sysInfo = new SystemInfo();
		hardware = sysInfo.getHardware();
		os = sysInfo.getOperatingSystem();
		fs = os.getFileSystem();
	}
	
	public String getMetrics() {
		long diskSpaceFree = 0;
		for (OSFileStore fileStore : fs.getFileStores(true)) {
			diskSpaceFree += fileStore.getFreeSpace();
		};
		long userProcessorLoad = 0;
		for (long[] processor : hardware.getProcessor().getProcessorCpuLoadTicks()) {
			userProcessorLoad += processor[0];
		}
		StringBuffer mx = new StringBuffer();
		mx.append(hardware.getSensors().getCpuTemperature()).append(",")
			.append(userProcessorLoad).append(",") // User load
			.append(hardware.getMemory().getAvailable()).append(",")
			.append(os.getProcessCount()).append(",")
			.append(diskSpaceFree);
		return mx.toString();
	}
	
}
