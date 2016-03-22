package com.parse.demo;

public class DataModelDevice {
	/**
	 * lDevice.mDeviceToken = [object valueForKey:@"deviceToken"];
	 * lDevice.mDeviceStatus =[object valueForKey:@"deviceStatus"];
	 * lDevice.mDeviceName = [object valueForKey:@"deviceName"];
	 * lDevice.mDeviceCurrentUser = [object valueForKey:@"deviceCurrentUser"];
	 * lDevice.mDeviceVersion = [object valueForKey:@"deviceVersion"];
	 * lDevice.mDeviceMake = [object valueForKey:@"deviceMake"];
	 * lDevice.mDeviceStatus = [object valueForKey:@"deviceStatus"];
	 * lDevice.mDeviceModel = [object valueForKey:@"deviceModel"];
	 * lDevice.mDeviceRegistrationNumber = [object
	 * valueForKey:@"registrationNumber"]; [mArrayOfDeviceForTable
	 * addObject:lDevice];
	 */
	private String mDeviceToken = "";
	private String mDeviceStatus = "";
	private String mDeviceName = "";
	private String mDeviceVersion = "";
	private String mDeviceCurrentUser = "";
	private String mDeviceMake = "";
	private String mDeviceModel = "";
	private String mDeviceRegistrationNumber = "";

	/*********** Set Methods ******************/

	public void setDevicveToken(String deviceToken) {
		this.mDeviceToken = deviceToken;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.mDeviceStatus = deviceStatus;
	}

	public void setDeviceName(String deviceName) {
		this.mDeviceName = deviceName;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.mDeviceVersion = deviceVersion;
	}

	public void setDeviceCurrentUser(String deviceCurrentUser) {
		this.mDeviceCurrentUser = deviceCurrentUser;
	}

	public void setDeviceMake(String deviceMake) {
		this.mDeviceMake = deviceMake;
	}

	public void setDeviceModel(String deviceModel) {
		this.mDeviceModel = deviceModel;
	}

	public void setDeviceRegistrationNumber(String deviceRegistrationNumber) {
		this.mDeviceRegistrationNumber = deviceRegistrationNumber;
	}

	/*********** Get Methods ****************/

	public String getDevicveToken() {
		return this.mDeviceToken;
	}

	public String getDeviceStatus() {
		return this.mDeviceStatus;
	}

	public String getDeviceName() {
		return this.mDeviceName;
	}

	public String getDeviceVersion() {
		return this.mDeviceVersion;
	}

	public String getDeviceCurrentUser() {
		return this.mDeviceCurrentUser;
	}

	public String getDeviceMake() {
		return this.mDeviceMake;
	}

	public String getDeviceModel() {
		return this.mDeviceModel;
	}

	public String getDeviceRegistrationNumber() {
		return this.mDeviceRegistrationNumber;
	}

}
