package com.parse.demo;

import java.util.Date;

public class DataModelDeviceHistory {

	private String mDeviceName = "";
	private String mDeviceModel = "";
	private String mDeviceMake = "";
	private Date mDeviceCheckoutDate = null;
	private Date mDeviceCheckinDate = null;
	private String mDeviceUser = "";
	private String mDeviceToken = "";
	private String mDeviceAction = "";
	private String mDeviceRegNumber = "";

	public void setDeviceToken(String deviceToken) {
		this.mDeviceToken = deviceToken;
	}

	public String getDeviceToken() {
		return this.mDeviceToken;
	}
	
	public void setDeviceName(String deviceName) {
		this.mDeviceName = deviceName;
	}

	public String getDeviceName() {
		return this.mDeviceName;
	}

	public void setDeviceModel(String deviceModel) {
		this.mDeviceModel = deviceModel;
	}

	public String getDeviceModel() {
		return this.mDeviceModel;
	}

	public void setDeviceMake(String deviceMake) {
		this.mDeviceMake = deviceMake;
	}

	public String getDeviceMake() {
		return this.mDeviceMake;
	}

	public void setDeviceUser(String deviceUser) {
		this.mDeviceUser = deviceUser;
	}

	public String getDeviceUser() {
		return this.mDeviceUser;
	}

	public void setDeviceAction(String deviceAction) {
		this.mDeviceAction = deviceAction;
	}

	public String getDeviceAction() {
		return this.mDeviceAction;
	}

	public void setDeviceRegNumber(String deviceRegNumber) {
		this.mDeviceRegNumber = deviceRegNumber;
	}

	public String getDeviceRegNumber() {
		return this.mDeviceRegNumber;
	}

	public void setDeviceCheckoutDate(Date deviceCheckoutDate) {
		this.mDeviceCheckoutDate = deviceCheckoutDate;
	}

	public Date getDeviceCheckoutDate() {
		return this.mDeviceCheckoutDate;
	}

	public void setDeviceCheckinDate(Date deviceCheckinDate) {
		this.mDeviceCheckinDate = deviceCheckinDate;
	}

	public Date getDeviceCheckinDate() {
		return this.mDeviceCheckinDate;
	}
}
