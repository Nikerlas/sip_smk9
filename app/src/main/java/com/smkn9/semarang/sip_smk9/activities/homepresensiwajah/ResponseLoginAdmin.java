package com.smkn9.semarang.sip_smk9.activities.homepresensiwajah;

import com.google.gson.annotations.SerializedName;

public class ResponseLoginAdmin{

	@SerializedName("hasil")
	private String hasil;

	public String getHasil(){
		return hasil;
	}
}