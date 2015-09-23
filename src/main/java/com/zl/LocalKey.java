package com.zl;

public class LocalKey {
	public static final String IS_FINGER_SETTING="isFingerSetting";//是否设置手势密码
	public static final String ENCRYPTSTR="encryptStr";//保存本地的加密session
	public static final int MAX_TRY_COUNT = 5;//最大试错次数
	public static final int MAX_RESETFLAG=2;//最大try
	
	public static final String IS_FIRST_OPEN_APP= "is_first_open_app";//是否第一次打开应用
	
	public final static int ALLOW_BACKGROUND_TIME = 10000;//应用后台运行时间
	
	public static final String APP_ON_TASK_TOP="app_on_task_top";//应用后台
	
	public static final String SHOW_FINGER_TRAIL="show_finger_trail";//显示轨迹
}
