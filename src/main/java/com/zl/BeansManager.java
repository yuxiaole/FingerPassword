package com.zl;

public class BeansManager {
	private String imeiBean;//手机的imei码
	private boolean isShowFingerTrail=true;//是否显示手势轨迹
	private String password;
	private String session;
	
	private static BeansManager instance=null;
	public static BeansManager getInstance(){
		if (instance==null) {
			synchronized (BeansManager.class) {
				if (instance==null) {
					instance=new BeansManager();
				}
			}
		}
		return instance;
	}
	public String getImeiBean() {
		return imeiBean;
	}
	public void setImeiBean(String imeiBean) {
		this.imeiBean = imeiBean;
	}
	public boolean isShowFingerTrail() {
		return isShowFingerTrail;
	}
	public void setShowFingerTrail(boolean isShowFingerTrail) {
		this.isShowFingerTrail = isShowFingerTrail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
}
