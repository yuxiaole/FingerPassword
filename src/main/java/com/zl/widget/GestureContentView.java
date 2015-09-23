package com.zl.widget;

import java.util.ArrayList;
import java.util.List;

import com.zl.R;
import com.zl.util.AppUtil;
import com.zl.widget.GestureDrawline.GestureCallBack;
import com.zl.widget.GestureDrawline.GestureSetCallBack;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class GestureContentView extends ViewGroup {

	private int baseNum = 6;

	private int[] screenDispaly;

	/**
	 * 每个点区域的宽度
	 */
	private int blockWidth;
	
	private List<GesturePoint> list;
	private Context context;
	private boolean isVerify;
	private GestureDrawline gestureDrawline;

	/**
	 * 包含9个ImageView的容器，初始�?
	 * 
	 * @param context
	 * @param isVerify
	 *            是否为校验手势密�?
	 * @param passWord
	 *            用户传入密码
	 * @param callBack
	 *            手势绘制完毕的回�?
	 */
	public GestureContentView(Context context, boolean isVerify,GestureCallBack callBack,GestureSetCallBack setCallBack) {
		super(context);
		screenDispaly = AppUtil.getScreenDispaly(context);
		blockWidth = screenDispaly[0] / 4;
		this.list = new ArrayList<GesturePoint>();
		this.context = context;
		this.isVerify = isVerify;
	
		addChild();
		// 初始化一个可以画线的view
		gestureDrawline = new GestureDrawline(context, list, isVerify, callBack,setCallBack);
	}
	public GestureDrawline getGestureDrawline(){
		
		return gestureDrawline;
	}

	private void addChild() {
		for (int i = 0; i < 9; i++) {
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.gesture_node_normal);
			this.addView(image);
			invalidate();
		
			int row = i / 3;
		
			int col = i % 3;
		
			int leftX = col * blockWidth + blockWidth / baseNum;
			int topY = row * blockWidth + blockWidth / baseNum;
			int rightX = col * blockWidth + blockWidth - blockWidth / baseNum;
			int bottomY = row * blockWidth + blockWidth - blockWidth / baseNum;
			GesturePoint p = new GesturePoint(leftX, rightX, topY, bottomY,
					image, i + 1);
			this.list.add(p);
		}
	}

	public void setParentView(ViewGroup parent) {
		
		int width = screenDispaly[0]*3/4;
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
		layoutParams.gravity = Gravity.CENTER;
		this.setLayoutParams(layoutParams);
		
		gestureDrawline.setLayoutParams(layoutParams);
		
		parent.addView(gestureDrawline);
		parent.addView(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
		
			int row = i / 3;
		
			int col = i % 3;
			View v = getChildAt(i);
			v.layout(col * blockWidth + blockWidth / baseNum, row * blockWidth
					+ blockWidth / baseNum, col * blockWidth + blockWidth
					- blockWidth / baseNum, row * blockWidth + blockWidth
					- blockWidth / baseNum);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}


	public void clearDrawlineState(long delayTime) {
		gestureDrawline.clearDrawlineState(delayTime);
	}

}
