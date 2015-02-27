package com.yoavst.quickapps.news.types;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yoav.
 */
public class AuthResponse{
	@SerializedName("access_token")
	@Expose
	private String accessToken;
	@SerializedName("refresh_token")
	@Expose
	private String refreshToken;
	@SerializedName("token_type")
	@Expose
	private String tokenType;
	@SerializedName("expires_in")
	@Expose
	private int expiresIn;
	@Expose
	private String plan;
	@Expose
	private String id;
	@Expose
	private String provider;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
