package com.parse.demo;

public class InitialDataModel {

	private String mText = "";
	private String mImage = "";
	private String mSubtext = "";

	/*********** Set Methods ******************/

	public void setMainText(String mainText) {
		this.mText = mainText;
	}

	public void setImage(String Image) {
		this.mImage = Image;
	}

	public void setSubText(String subText) {
		this.mSubtext = subText;
	}

	/*********** Get Methods ****************/

	public String getMainText() {
		return this.mText;
	}

	public String getImage() {
		return this.mImage;
	}

	public String getSubText() {
		return this.mSubtext;
	}
}
