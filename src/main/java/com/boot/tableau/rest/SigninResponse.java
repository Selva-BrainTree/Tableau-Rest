/**
 * 
 */
package com.boot.tableau.rest;

import java.io.Serializable;

/**
 * @author selva
 *
 */
public class SigninResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5865020531610932992L;

	private String authKey;

	private String dashboardKey;

	/**
	 * @param authKey
	 * @param dashboardKey
	 */
	public SigninResponse(String authKey, String dashboardKey) {
		super();
		this.authKey = authKey;
		this.dashboardKey = dashboardKey;
	}

	/**
	 * @return the authKey
	 */
	public String getAuthKey() {
		return authKey;
	}

	/**
	 * @param authKey the authKey to set
	 */
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	/**
	 * @return the dashboardKey
	 */
	public String getDashboardKey() {
		return dashboardKey;
	}

	/**
	 * @param dashboardKey the dashboardKey to set
	 */
	public void setDashboardKey(String dashboardKey) {
		this.dashboardKey = dashboardKey;
	}

}
