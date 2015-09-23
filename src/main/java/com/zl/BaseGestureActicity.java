package com.zl;


import javax.crypto.BadPaddingException;

import com.zl.util.SPUtils;
import com.zl.widget.GestureContentView;
import com.zl.widget.GestureDrawline.GestureCallBack;
import com.zl.widget.GestureDrawline.GestureSetCallBack;
import com.zl.widget.LockIndicator;
import com.zl.widget.TitleBar;
import com.zl.widget.TitleBar.onTitleClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseGestureActicity extends Activity  implements OnClickListener{
	public static final String BASE_GESTURE_ACTIVITY="BaseGestureActicity"; 
	public static final String TAG="tag";
	private TextView mTextForget;
	private TextView mTextOtherLogin;
	private TextView mTextPhoneNumber;
	private LockIndicator mLockIndicator;
	private TextView mTextReset;
	private ImageView mImgUserLogo;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextTip;
	private int psd_count = LocalKey.MAX_TRY_COUNT;
	private boolean isFingerSetting;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String sessionId;
	private Application context;
	private TitleBar mTitleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context=getAPPContext();
		//判断是否设置手势
		
		isFingerSetting=(Boolean)SPUtils.get(context, LocalKey.IS_FINGER_SETTING, false);
		if (isFingerSetting) {
			setContentView(R.layout.activity_gesture_verify);
		} else {
			setContentView(R.layout.activity_gesture_edit);
		}
		setUpViews();
		getImei();
		toFingerPsw();
		
	}

	private void toFingerPsw() {
		if (isFingerSetting) {
			//解锁手势
			//GestureVerityCodeCallBack callBack=new GestureVerityCodeCallBack(BaseGestureActicity.this,mGestureContentView);
			setUpGesture(isFingerSetting,gestureVerityCallBack,null);
			String userName=getUsername();
			String userNameEncrypt= userName.substring(0, 3) + "****" + userName.substring(userName.length() - 4, userName.length());

			mTextPhoneNumber.setText(userNameEncrypt);
			//CheckSuccess();
		} else {
			//设置手势
			setUpGesture(isFingerSetting,null,gestureSetCallBack);
			updateCodeList("");
		}
	}

	private void setUpGesture(boolean isFirst,GestureCallBack callBack,GestureSetCallBack setCallBack) {
		mGestureContentView=new GestureContentView(this, isFirst, callBack,setCallBack);
		mGestureContentView.setParentView(mGestureContainer);
		boolean showFingerTrail=(Boolean)SPUtils.get(context, LocalKey.SHOW_FINGER_TRAIL, true);
		if (showFingerTrail) {
			BeansManager.getInstance().setShowFingerTrail(true);
		} else {
			BeansManager.getInstance().setShowFingerTrail(false);
		}
	}

	private void setUpViews() {
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		mTitleBar=(TitleBar)findViewById(R.id.titleBar);
		if (isFingerSetting) {//验证手势
			mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
			mTextForget.setOnClickListener(this);
			mTextOtherLogin=(TextView)findViewById(R.id.text_other_login);
			mTextOtherLogin.setOnClickListener(this);
			mImgUserLogo = (ImageView) findViewById(R.id.user_logo);
			mImgUserLogo.setImageResource(R.drawable.user_logo);
			mTextPhoneNumber = (TextView) findViewById(R.id.text_phone_number);
			mTitleBar.setTitle("手势密码");
			mTitleBar.setLeftAreaValid(false);
		} else {//设置手势
			mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
			mTextReset = (TextView) findViewById(R.id.text_reset);
			mTextReset.setClickable(false);
			mTitleBar.setTitle("设置手势密码");
			mTitleBar.setLeftAreaValid(true);
			mTitleBar.setLeftTitleAreaOnClickListener(new onTitleClickListener() {
				
				@Override
				public void onClick(View v) {
					titleBarBackDefine();
				}
			});
		}
	}
	private void getImei() {
		if (BeansManager.getInstance().getImeiBean() == null) {
			TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			BeansManager.getInstance().setImeiBean(imei);
		}
	}
	private void updateCodeList(String inputCode) {
		// 
		mLockIndicator.setPath(inputCode);
	}
	
	@Override
	public void onClick(View v) {
		if(v==mTextForget){
			shwoDialog("温馨提示","忘记手势密码，删除手势，重新登录？");
			
		}else if (v==mTextReset) {
			mIsFirstInput = true;
			updateCodeList("");
			mTextTip.setText(getString(R.string.set_gesture_pattern));
		}else if (v==mTextOtherLogin) {
			shwoDialog("温馨提示","删除账户手势密码，新用户登录？");
		}
	}

	private void shwoDialog(String title,String message) {
		Dialog dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SPUtils.put(context, LocalKey.IS_FINGER_SETTING, false);
						SPUtils.put(context, LocalKey.IS_FIRST_OPEN_APP, true);
						SPUtils.remove(context, LocalKey.ENCRYPTSTR);
						
						forgetPsw2Login();
						BaseGestureActicity.this.finish();
					}
				})
				.setNegativeButton("否",null)
				.create();
		dialog.show();
		
	}

	private GestureSetCallBack gestureSetCallBack=new GestureSetCallBack() {
		
		@Override
		public void onGestureCodeInput(String inputCode) {
			if (!isInputPassValidate(inputCode)) {
				mTextTip.setText(Html
						.fromHtml("<font color='#c70c1e'>最少链接4个点，请重新输入</font>"));
				mGestureContentView.clearDrawlineState(0L);
				return;
			}
			if (mIsFirstInput) {
				mFirstPassword = inputCode;
				updateCodeList(inputCode);
				mGestureContentView.clearDrawlineState(0L);
				mTextReset.setClickable(true);
				mTextReset.setText(getString(R.string.reset_gesture_code));
				mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>请再输一遍</font>"));
			} else {
				if (inputCode.equals(mFirstPassword)) {
					mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>设置成功</font>"));
					//得到session
					sessionId=getSessionId();
					if (sessionId==null) {
						sessionId=BeansManager.getInstance().getSession();
					}
					String password=BeansManager.getInstance().getImeiBean()+inputCode;
					String encryptStr = null;
					try {
						encryptStr = ZlaesManager.encrypt(password, sessionId);
						SPUtils.put(context, LocalKey.IS_FINGER_SETTING, true);
						SPUtils.put(context, LocalKey.IS_FIRST_OPEN_APP, false);
						
						SPUtils.put(context, LocalKey.ENCRYPTSTR, encryptStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mGestureContentView.clearDrawlineState(0L);
					setFingerGestureSuccess();
					
					BaseGestureActicity.this.finish();
				} else {
					mTextTip.setText(Html
							.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
					// 动画
					Animation shakeAnimation = AnimationUtils
							.loadAnimation(
									context,
									R.anim.shake);
					mTextTip.startAnimation(shakeAnimation);
					// 清除
					mGestureContentView.clearDrawlineState(1000L);
				}
			}
			mIsFirstInput = false;
			
		}
	};
	
	private GestureCallBack gestureVerityCallBack=new GestureCallBack() {
		
		
		@Override
		public void checkedSuccess() {
			mGestureContentView.clearDrawlineState(0L);
			validateFingerGesture();
			BaseGestureActicity.this.finish();
			//CheckSuccessBean.getIntance().setCheckBean(true);
		}
		
		@Override
		public void checkedFail() {
			//CheckSuccessBean.getIntance().setCheckBean(false);
			psd_count--;
			if (psd_count <= 0) {
				SPUtils.put(context, LocalKey.IS_FINGER_SETTING, false);
				SPUtils.put(context, LocalKey.ENCRYPTSTR, "");
				SPUtils.put(context, LocalKey.IS_FIRST_OPEN_APP, true);
				PersistentCookieStore persistentCookieStore=new PersistentCookieStore(context);
				persistentCookieStore.clear();
				inValidateFingerGesture();
				BaseGestureActicity.this.finish();
			}
			mGestureContentView.clearDrawlineState(500L);
			mTextTip.setVisibility(View.VISIBLE);
			mTextTip.setText(Html
					.fromHtml("<font color='#c70c1e'>密码错误，还可再试</font>"
							+ "<font color='#c70c1e'>"
							+ psd_count
							+ "</font>"
							+ "<font color='#c70c1e'>次</font>"));
			Animation shakeAnimation = AnimationUtils
					.loadAnimation(context,R.anim.shake);
			mTextTip.startAnimation(shakeAnimation);
		}
		
		@Override
		public void OnGestureFinish(String key) {
			//手势验证完成，组装得到密钥
			String seed=getSeed(key);
			//从文件读取密文
			String encryptStr=(String)SPUtils.get(context, LocalKey.ENCRYPTSTR, "");
			//解密
			decryptSession(seed,encryptStr);
		}
		/*
		 * 解密，传入手势密钥和密文
		 */
		private void decryptSession(String seed,String encryptStr) {
			try {
				String decryptStr=ZlaesManager.decrypt(seed, encryptStr);
				//暂时储存session
				setSession2Bean(decryptStr);
				//解锁成功
				checkedSuccess();

			} catch (BadPaddingException e) {
				//解锁失败
				checkedFail();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}; 

	private void setSession2Bean(String session) {
		BeansManager.getInstance().setSession(session);
	}

	private String getSeed(String key) {
		
		String password=BeansManager.getInstance().getImeiBean()+key;
		BeansManager.getInstance().setPassword(password);
		return password;
	}
	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	protected abstract String getSessionId();
	protected abstract Application getAPPContext();
	protected abstract String getUsername();
	protected abstract void validateFingerGesture();
	protected abstract void inValidateFingerGesture();
	protected abstract void forgetPsw2Login();
	protected abstract void setFingerGestureSuccess();
	protected abstract void titleBarBackDefine();

}
