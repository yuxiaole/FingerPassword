package com.zl.widget;


import com.zl.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBar extends LinearLayout {

	private Context mContext;

	private TextView rightTextView;

	private TextView leftTextView;

	private TextView titleTextView;

	private ImageView leftIcon;
	private ImageView rightIcon;
	private ImageView titleIcon;

	private onTitleClickListener mLeftClickListener;
	private onTitleClickListener mRightClickListener;
	private onTitleClickListener mMidClickListener;

	private View mView;

	private View mCustomMiddleView;

	private ViewGroup mLeftArea;

	private ViewGroup mMiddleArea;

	private ViewGroup mRightArea;

	private OnClickAntiFrequentListener mListener = new OnClickAntiFrequentListener() {

		@Override
		public void onClickAntiFrequent(View v) {
			if (v==mLeftArea) {
				if (mLeftClickListener != null) {
					mLeftClickListener.onClick(v);
				} else if (mContext instanceof Activity) {
					((Activity) mContext).finish();
				}
			} else if(v==mMiddleArea){
				if (mMidClickListener != null) {
					mMidClickListener.onClick(v);
				}
			}else if (v==mRightArea) {
				if (mRightClickListener != null) {
					mRightClickListener.onClick(v);
				}
			}
		}
	};

	public TitleBar(Context context) {
		super(context);
		init(context);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.finger_title_bar2, this, true);
		//addView(mView);
		rightTextView = (TextView) findViewById(R.id.titleRightText);
		leftTextView = (TextView) findViewById(R.id.titleLeftText);
		titleTextView = (TextView) findViewById(R.id.title);
		mLeftArea = (ViewGroup) findViewById(R.id.titleLeftArea);
		mMiddleArea = (ViewGroup) findViewById(R.id.titleMiddleArea);
		mRightArea = (ViewGroup) findViewById(R.id.titleRightArea);

		leftIcon = (ImageView) findViewById(R.id.titleLeftIcon);
		rightIcon = (ImageView) findViewById(R.id.titleRightIcon);
		titleIcon = (ImageView) findViewById(R.id.titleIcon);

		mMiddleArea.setOnClickListener(mListener);
		setLeftAreaValid(true);

	}

	public void setTitle(CharSequence title) {
		titleTextView.setText(title);
	}

	public void setTitle(int resId) {
		titleTextView.setText(resId);
	}

	public void setLeftText(CharSequence text) {
		setLeftAreaValid(true);
		leftTextView.setText(text);
		leftTextView.setVisibility(View.VISIBLE);
	}

	public void setLeftText(int resId) {
		setLeftAreaValid(true);
		leftTextView.setText(resId);
		leftTextView.setVisibility(View.VISIBLE);
	}

	public void setRightText(CharSequence text) {
		setRightAreaValid(true);
		rightTextView.setVisibility(View.VISIBLE);
		rightTextView.setText(text);
	}

	public void setRightText(int resId) {
		setRightAreaValid(true);
		rightTextView.setVisibility(View.VISIBLE);
		rightTextView.setText(resId);
	}

	public void setLeftAreaValid(boolean enable) {
		if (!enable) {
			mLeftArea.setVisibility(INVISIBLE);
			mLeftArea.setOnClickListener(null);
			mLeftArea.setClickable(false);
		} else {
			mLeftArea.setVisibility(VISIBLE);
			mLeftArea.setClickable(true);
			mLeftArea.setOnClickListener(mListener);
		}
	}

	public void setRightAreaValid(boolean enable) {
		if (!enable) {
			mRightArea.setVisibility(INVISIBLE);
			mRightArea.setOnClickListener(null);
			mRightArea.setClickable(false);
		} else {
			mRightArea.setVisibility(VISIBLE);
			mRightArea.setClickable(true);
			mRightArea.setOnClickListener(mListener);
		}
	}

	public void setLeftIcon(Drawable d) {
		setLeftAreaValid(true);
		leftIcon.setVisibility(VISIBLE);
		leftIcon.setImageDrawable(d);
	}

	public void setLeftIcon(int resId) {
		setLeftAreaValid(true);
		leftIcon.setVisibility(VISIBLE);
		leftIcon.setImageResource(resId);

	}
	
	public ImageView getLeftIcon(){
		return leftIcon;
	}
	
	public void setMidIcon(Drawable d) {
		titleIcon.setVisibility(VISIBLE);
		titleIcon.setImageDrawable(d);
	}

	public void setMidIcon(int resId) {
		titleIcon.setVisibility(VISIBLE);
		titleIcon.setImageResource(resId);
	}

	public void setRightIcon(int resId) {
		setRightAreaValid(true);
		rightIcon.setVisibility(VISIBLE);
		rightIcon.setImageResource(resId);
	}

	public void setRightIcon(Drawable d) {
		setRightAreaValid(true);
		rightIcon.setVisibility(VISIBLE);
		rightIcon.setImageDrawable(d);
	}

	public void hideLeftText() {
		leftTextView.setVisibility(GONE);
	}

	public void hideRightText() {
		rightTextView.setVisibility(GONE);
	}
	public void hideLeftIcon() {
		leftIcon.setVisibility(GONE);
	}

	public void hideRightIcon() {
		rightIcon.setVisibility(GONE);
	}

	public void setLeftTitleAreaOnClickListener(onTitleClickListener l) {
		mLeftClickListener = l;
	}

	public void setRightTitleAreaOnClickListener(onTitleClickListener l) {
		mRightClickListener = l;
	}

	public void setMidTitleAreaOnClickListener(onTitleClickListener l) {
		mMidClickListener = l;
	}

	public void setCustomView(View v) {
		this.removeAllViews();
		addView(v);
	}

	public void setCustomMiddleView(View v) {
		mMiddleArea.removeAllViews();
		mCustomMiddleView = v;
		mMiddleArea.addView(v);
	}

	public View getCustomMiddleView() {
		return mCustomMiddleView;
	}

	public static interface onTitleClickListener {
		public void onClick(View v);
	}

}
