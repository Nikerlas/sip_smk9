package com.smkn9.semarang.sip_smk9.activities.homepresensiwajah;

import com.google.gson.annotations.SerializedName;

public class ResponseLoadServer{

	@SerializedName("link")
	private String link;

	@SerializedName("status")
	private String status;

	public String getLink(){
		return link;
	}

	public String getStatus(){
		return status;
	}
}