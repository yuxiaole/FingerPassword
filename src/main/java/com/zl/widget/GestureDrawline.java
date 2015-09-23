package com.zl.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zl.BeansManager;
import com.zl.LocalKey;
import com.zl.util.AppUtil;
import com.zl.util.Constants;
import com.zl.util.SPUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

/**
 * 手势密码路径绘制
 * 
 */
public class GestureDrawline extends View {
	private int mov_x;// 声明起点坐标
	private int mov_y;
	private Paint paint;// 声明画笔
	private Canvas canvas;// 画布
	private Bitmap bitmap;// 位图
	private List<GesturePoint> list;// 装有各个view坐标的集�?
	private List<Pair<GesturePoint, GesturePoint>> lineList;// 记录画过的线
	private Map<String, GesturePoint> autoCheckPointMap;// 自动选中的情况点
	private boolean isDrawEnable = true; // 是否允许绘制

	/**
	 * 屏幕的宽度和高度
	 */
	private int[] screenDispaly;

	/**
	 * 手指当前在哪个Point
	 */
	private GesturePoint currentPoint;
	/**
	 * 用户绘图的回掉
	 */
	private GestureCallBack callBack;
	private GestureSetCallBack setCallBack;

	/**
	 * 用户当前绘制的图形密吗
	 */
	private StringBuilder passWordSb;

	/**
	 * 是否为校验
	 */
	private boolean isVerify;
	
	/*
	 * 是否显示轨迹
	 */
	private boolean isShowFingerTrail=true;

	public GestureDrawline(Context context, List<GesturePoint> list,
			boolean isVerify, GestureCallBack callBack,GestureSetCallBack setCallBack) {
		super(context);
		screenDispaly = AppUtil.getScreenDispaly(context);
		paint = new Paint(Paint.DITHER_FLAG);// 创建画笔
		bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0],
				Bitmap.Config.ARGB_8888); // 设置位图的宽
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);// 设置非填
		paint.setStrokeWidth(10);// 笔宽5像素
		//paint.setColor(Color.rgb(245, 142, 33));// 设置默认连线颜色
		boolean showFingerTrail=(Boolean)SPUtils.get(context, LocalKey.SHOW_FINGER_TRAIL, true);
		
		isShowFingerTrail=showFingerTrail;
		if (isShowFingerTrail) {
			paint.setColor(Color.rgb(56, 177, 239));
		} else {
			paint.setColor(Color.TRANSPARENT);
		}
		paint.setAntiAlias(true);// 

		this.list = list;
		this.lineList = new ArrayList<Pair<GesturePoint, GesturePoint>>();

		initAutoCheckPointMap();
		this.callBack = callBack;
		this.setCallBack=setCallBack;

		// 初始化密码缓�?
		this.isVerify = isVerify;
		this.passWordSb = new StringBuilder();
	}

	private void initAutoCheckPointMap() {
		autoCheckPointMap = new HashMap<String, GesturePoint>();
		autoCheckPointMap.put("1,3", getGesturePointByNum(2));
		autoCheckPointMap.put("1,7", getGesturePointByNum(4));
		autoCheckPointMap.put("1,9", getGesturePointByNum(5));
		autoCheckPointMap.put("2,8", getGesturePointByNum(5));
		autoCheckPointMap.put("3,7", getGesturePointByNum(5));
		autoCheckPointMap.put("3,9", getGesturePointByNum(6));
		autoCheckPointMap.put("4,6", getGesturePointByNum(5));
		autoCheckPointMap.put("7,9", getGesturePointByNum(8));
	}

	private GesturePoint getGesturePointByNum(int num) {
		for (GesturePoint point : list) {
			if (point.getNum() == num) {
				return point;
			}
		}
		return null;
	}
	

	
	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isDrawEnable == false) {
			
			return true;
		}
		//paint.setColor(Color.rgb(245, 142, 33));// 设置默认连线颜色
		paint.setColor(Color.rgb(56, 177, 239));// 设置默认连线颜色
		if (isShowFingerTrail) {
			paint.setColor(Color.rgb(56, 177, 239));
		} else {
			paint.setColor(Color.TRANSPARENT);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mov_x = (int) event.getX();
			mov_y = (int) event.getY();
			// 判断当前点击的位置是处于哪个点之�?
			currentPoint = getPointAt(mov_x, mov_y);
			if (currentPoint != null) {
				currentPoint.setPointState(Constants.POINT_STATE_SELECTED,isShowFingerTrail);
				passWordSb.append(currentPoint.getNum());
			}
			// canvas.drawPoint(mov_x, mov_y, paint);// 画点
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			clearScreenAndDrawList();

			
			GesturePoint pointAt = getPointAt((int) event.getX(),
					(int) event.getY());
			
			if (currentPoint == null && pointAt == null) {
				return true;
			} else {
				if (currentPoint == null) {// 先判断当前的point是不是为null
					// 如果为空，那么把手指移动到的点赋值给currentPoint
					currentPoint = pointAt;
				
					currentPoint.setPointState(Constants.POINT_STATE_SELECTED,isShowFingerTrail);
					passWordSb.append(currentPoint.getNum());
				}
			}
			if (pointAt == null
					|| currentPoint.equals(pointAt)
					|| Constants.POINT_STATE_SELECTED == pointAt
							.getPointState()) {
				
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), event.getX(), event.getY(),
						paint);// 画线
			} else {
				
				canvas.drawLine(currentPoint.getCenterX(),
						currentPoint.getCenterY(), pointAt.getCenterX(),
						pointAt.getCenterY(), paint);// 画线
				pointAt.setPointState(Constants.POINT_STATE_SELECTED,isShowFingerTrail);

				
				GesturePoint betweenPoint = getBetweenCheckPoint(currentPoint,
						pointAt);
				if (betweenPoint != null
						&& Constants.POINT_STATE_SELECTED != betweenPoint
								.getPointState()) {
					// 存在中间点并且没有被选中
					Pair<GesturePoint, GesturePoint> pair1 = new Pair<GesturePoint, GesturePoint>(
							currentPoint, betweenPoint);
					lineList.add(pair1);
					passWordSb.append(betweenPoint.getNum());
					Pair<GesturePoint, GesturePoint> pair2 = new Pair<GesturePoint, GesturePoint>(
							betweenPoint, pointAt);
					lineList.add(pair2);
					passWordSb.append(pointAt.getNum());
					
					betweenPoint.setPointState(Constants.POINT_STATE_SELECTED,isShowFingerTrail);
					// 赋�?�当前的point;
					currentPoint = pointAt;
				} else {
					Pair<GesturePoint, GesturePoint> pair = new Pair<GesturePoint, GesturePoint>(
							currentPoint, pointAt);
					lineList.add(pair);
					passWordSb.append(pointAt.getNum());
				
					currentPoint = pointAt;
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:// 
			if (isVerify) {
				String seed=passWordSb.toString();
				callBack.OnGestureFinish(seed);
			} else {
				setCallBack.onGestureCodeInput(passWordSb.toString());
				/*callBack.onGestureCodeInput(passWordSb.toString());*/
			}
			break;
		default:
			break;
		}
		return true;
	}


	public void clearDrawlineState(long delayTime) {
		if (delayTime > 0) {
			// 绘制红色提示路线
			isDrawEnable = false;
			drawErrorPathTip();
		}
		new Handler().postDelayed(new clearStateRunnable(), delayTime);
	}

	
	final class clearStateRunnable implements Runnable {
		public void run() {
			// 重置passWordSb
			passWordSb = new StringBuilder();
			// 清空保存点的集合
			lineList.clear();
			// 重新绘制界面
			clearScreenAndDrawList();
			for (GesturePoint p : list) {
				p.setPointState(Constants.POINT_STATE_NORMAL,isShowFingerTrail);
			}
			invalidate();
			isDrawEnable = true;
		}
	}

	private GesturePoint getPointAt(int x, int y) {

		for (GesturePoint point : list) {
			// 先判断x
			int leftX = point.getLeftX();
			int rightX = point.getRightX();
			if (!(x >= leftX && x < rightX)) {
				// 如果为假，则跳到下一个对�?
				continue;
			}

			int topY = point.getTopY();
			int bottomY = point.getBottomY();
			if (!(y >= topY && y < bottomY)) {
				// 如果为假，则跳到下一个对�?
				continue;
			}

			// 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
			return point;
		}

		return null;
	}

	private GesturePoint getBetweenCheckPoint(GesturePoint pointStart,
			GesturePoint pointEnd) {
		int startNum = pointStart.getNum();
		int endNum = pointEnd.getNum();
		String key = null;
		if (startNum < endNum) {
			key = startNum + "," + endNum;
		} else {
			key = endNum + "," + startNum;
		}
		return autoCheckPointMap.get(key);
	}

	/**
	 * 清掉屏幕上所有的线，然后画出集合里面的线
	 */
	private void clearScreenAndDrawList() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
	}


	private void drawErrorPathTip() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		//paint.setColor(Color.rgb(154, 7, 21));// 设置默认线路颜色
		paint.setColor(Color.rgb(228, 15, 2));// 设置默认线路颜色
		for (Pair<GesturePoint, GesturePoint> pair : lineList) {
			pair.first.setPointState(Constants.POINT_STATE_WRONG,isShowFingerTrail);
			pair.second.setPointState(Constants.POINT_STATE_WRONG,isShowFingerTrail);
			canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
					pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
		}
		invalidate();
	}
	
	
	
	public interface GestureSetCallBack{
		public abstract void onGestureCodeInput(String inputCode);
	}

	public interface GestureCallBack {

	
		public abstract void checkedSuccess();

		/**
		 * 代表用户绘制的密码与传入的密码不相同
		 */
		public abstract void checkedFail();
		
		public abstract void OnGestureFinish(String key);
	}

}
