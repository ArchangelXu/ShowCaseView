package smartdevelop.ir.eram.showcaseviewlib;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.text.Spannable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

/**
 * Created by Mohammad Reza Eram on 20/01/2018.
 */

public class GuideView extends FrameLayout {


	static final String TAG = "GuideView";

	private static final int MESSAGE_VIEW_SPACING = 4;
	private static final int GUIDE_VIEW_WIDTH = 150;
	private static final int APPEARING_ANIMATION_DURATION = 400;

	private static final int BACKGROUND_COLOR = 0x99000000;

	private final Paint selfPaint = new Paint();
	private final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Xfermode X_FER_MODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

	private View target;
	private RectF targetRect;
	private final Rect selfRect = new Rect();

	private float density;
	private boolean mIsShowing;
	private int yMessageView = 0;
	private int guideViewWidth;
	private boolean isTouchOutsideEnabled = true;
	private int messageViewSpacing;

	private GuideListener mGuideListener;
	private Gravity mGravity = Gravity.TOP_START;
	private GuideMessageView mMessageView;


	private GuideView(Context context, View view) {
		super(context);
		setWillNotDraw(false);
		setLayerType(View.LAYER_TYPE_HARDWARE, null);
		this.target = view;
		density = context.getResources().getDisplayMetrics().density;
		init();

		int[] locationTarget = new int[2];
		target.getLocationOnScreen(locationTarget);
		calculateTargetRect(locationTarget);

		mMessageView = new GuideMessageView(getContext());
		mMessageView.setColor(Color.WHITE);
		mMessageView.setMessageGravity(mGravity);
		LayoutParams params = new LayoutParams(guideViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

		addView(mMessageView, params);

		setMessageLocation(resolveMessageViewLocation());

		ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				getViewTreeObserver().removeOnGlobalLayoutListener(this);

				setMessageLocation(resolveMessageViewLocation());
				int[] locationTarget = new int[2];
				target.getLocationOnScreen(locationTarget);

				calculateTargetRect(locationTarget);

				selfRect.set(getPaddingLeft(),
						getPaddingTop(),
						getWidth() - getPaddingRight(),
						getHeight() - getPaddingBottom());

				getViewTreeObserver().addOnGlobalLayoutListener(this);
			}
		};
		getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
	}

	private void calculateTargetRect(int[] locationTarget) {
		int radius = Math.min(target.getWidth(), target.getHeight()) / 2;
		targetRect = new RectF(locationTarget[0],
				locationTarget[1],
				locationTarget[0],
				locationTarget[1]);
		targetRect.offset(target.getWidth() / 2, target.getHeight() / 2);
		targetRect.inset(-radius, -radius);
	}

	private void init() {
		messageViewSpacing = (int) (MESSAGE_VIEW_SPACING * density);
		guideViewWidth = (int) (GUIDE_VIEW_WIDTH * density);
	}


	private int getNavigationBarSize() {
		Resources resources = getContext().getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	private boolean isLandscape() {
		int display_mode = getResources().getConfiguration().orientation;
		return display_mode != Configuration.ORIENTATION_PORTRAIT;
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		if (target != null) {

			selfPaint.setColor(BACKGROUND_COLOR);
			selfPaint.setStyle(Paint.Style.FILL);
			selfPaint.setAntiAlias(true);
			canvas.drawRect(selfRect, selfPaint);

			targetPaint.setXfermode(X_FER_MODE_CLEAR);
			targetPaint.setAntiAlias(true);

			canvas.drawOval(targetRect, targetPaint);
		}
	}

	public boolean isShowing() {
		return mIsShowing;
	}

	public void dismiss() {
		((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
		mIsShowing = false;
		if (mGuideListener != null) {
			mGuideListener.onDismiss(target);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (isTouchOutsideEnabled) {
				//allow click anywhere to dismiss
				if (targetRect.contains(x, y)) {
					target.performClick();
				}
				dismiss();
			} else if (targetRect.contains(x, y)) {
				target.performClick();
				dismiss();
			}
			return true;
		}
		return false;
	}

	private boolean isViewContains(View view, float rx, float ry) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		int w = view.getWidth();
		int h = view.getHeight();

		return !(rx < x || rx > x + w || ry < y || ry > y + h);
	}

	private void setMessageLocation(Point p) {
		mMessageView.setX(p.x);
		mMessageView.setY(p.y);
		postInvalidate();
	}

	public void updateGuideViewLocation() {
		requestLayout();
	}

	private Point resolveMessageViewLocation() {

		int xMessageView = 0;
		boolean isRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
		int start = (int) (isRtl ? targetRect.right : targetRect.left);
		int end = (int) (isRtl ? targetRect.left : targetRect.right);
		int top = (int) targetRect.top;
		int bottom = (int) targetRect.bottom;
		int width = mMessageView.getWidth();
		int height = mMessageView.getHeight();
		Gravity gravity = getFixedGravity(isRtl);
		switch (gravity) {

			case TOP_START:
				xMessageView = start + (isRtl ? -1 : 1) * (int) targetRect.width() / 2;
				yMessageView = top - height - messageViewSpacing;
				break;
			case TOP_CENTER:
				xMessageView = (int) (targetRect.left - width / 2 + targetRect.width() / 2);
				yMessageView = top - height - messageViewSpacing;
				break;
			case TOP_END:
				xMessageView = end - width - (isRtl ? -1 : 1) * (int) targetRect.width() / 2;
				yMessageView = top - height - messageViewSpacing;
				break;
			case BOTTOM_START:
				xMessageView = start + (isRtl ? -1 : 1) * (int) targetRect.width() / 2;
				yMessageView = bottom + messageViewSpacing;
				break;
			case BOTTOM_CENTER:
				xMessageView = (int) (targetRect.left - width / 2 + targetRect.width() / 2);
				yMessageView = bottom + messageViewSpacing;
				break;
			case BOTTOM_END:
				xMessageView = end - width - (isRtl ? -1 : 1) * (int) targetRect.width() / 2;
				yMessageView = bottom + messageViewSpacing;
				break;
			case START_TOP:
				if (isRtl) {
					xMessageView = start + messageViewSpacing;
				} else {
					xMessageView = start - width - messageViewSpacing;
				}
				yMessageView = top + (int) targetRect.height() / 2;
				break;
			case START_CENTER:
				if (isRtl) {
					xMessageView = start + messageViewSpacing;
				} else {
					xMessageView = start - width - messageViewSpacing;
				}
				yMessageView = (int) (top - height / 2 + targetRect.height() / 2);
				break;
			case START_BOTTOM:
				if (isRtl) {
					xMessageView = start + messageViewSpacing;
				} else {
					xMessageView = start - width - messageViewSpacing;
				}
				yMessageView = bottom - height - (int) targetRect.height() / 2;
				break;
			case END_TOP:
				if (isRtl) {
					xMessageView = end - width - messageViewSpacing;
				} else {
					xMessageView = end + messageViewSpacing;
				}
				yMessageView = top + (int) targetRect.height() / 2;
				break;
			case END_CENTER:
				if (isRtl) {
					xMessageView = end - width - messageViewSpacing;
				} else {
					xMessageView = end + messageViewSpacing;
				}
				yMessageView = (int) (top - height / 2 + targetRect.height() / 2);
				break;
			case END_BOTTOM:
				if (isRtl) {
					xMessageView = end - width - messageViewSpacing;
				} else {
					xMessageView = end + messageViewSpacing;
				}
				yMessageView = bottom - height - (int) targetRect.height() / 2;
				break;
		}
		if (isLandscape()) {
			xMessageView -= getNavigationBarSize();
		}

		if (xMessageView + mMessageView.getWidth() > getWidth())
			xMessageView = getWidth() - mMessageView.getWidth();
		if (xMessageView < 0)
			xMessageView = 0;

		if (yMessageView < 0)
			yMessageView = 0;


		return new Point(xMessageView, yMessageView);
	}

	private Gravity getFixedGravity(boolean isRtl) {
		switch (mGravity) {
			case TOP_START:
				return isRtl ? Gravity.TOP_END : Gravity.TOP_START;
			case TOP_END:
				return isRtl ? Gravity.TOP_START : Gravity.TOP_END;
			case BOTTOM_START:
				return isRtl ? Gravity.BOTTOM_END : Gravity.BOTTOM_START;
			case BOTTOM_END:
				return isRtl ? Gravity.BOTTOM_START : Gravity.BOTTOM_END;
			default:
				return mGravity;
		}
	}

	public void show() {
		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		this.setClickable(false);

		((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
		AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
		startAnimation.setDuration(APPEARING_ANIMATION_DURATION);
		startAnimation.setFillAfter(true);
		this.startAnimation(startAnimation);
		mIsShowing = true;
	}

	public void setContentText(String str) {
		mMessageView.setContentText(str);
	}

	public void setTouchOutsideEnabled(boolean touchOutsideEnabled) {
		isTouchOutsideEnabled = touchOutsideEnabled;
	}

	public void setContentSpan(Spannable span) {
		mMessageView.setContentSpan(span);
	}

	public void setContentTypeFace(Typeface typeFace) {
		mMessageView.setContentTypeFace(typeFace);
	}

	public void setContentTextSize(int size) {
		mMessageView.setContentTextSize(size);
	}

	public void setTextAlignment(int textAlignment) {
		mMessageView.setTextAlignment(textAlignment);
	}

	public void setViewGravity(Gravity mGravity) {
		this.mGravity = mGravity;
		switch (mGravity) {

			case TOP_START:
				mMessageView.setMessageGravity(Gravity.BOTTOM_START);
				break;
			case TOP_CENTER:
				mMessageView.setMessageGravity(Gravity.BOTTOM_CENTER);
				break;
			case TOP_END:
				mMessageView.setMessageGravity(Gravity.BOTTOM_END);
				break;
			case BOTTOM_START:
				mMessageView.setMessageGravity(Gravity.TOP_START);
				break;
			case BOTTOM_CENTER:
				mMessageView.setMessageGravity(Gravity.TOP_CENTER);
				break;
			case BOTTOM_END:
				mMessageView.setMessageGravity(Gravity.TOP_END);
				break;
			case START_TOP:
				mMessageView.setMessageGravity(Gravity.END_TOP);
				break;
			case START_CENTER:
				mMessageView.setMessageGravity(Gravity.END_CENTER);
				break;
			case START_BOTTOM:
				mMessageView.setMessageGravity(Gravity.END_BOTTOM);
				break;
			case END_TOP:
				mMessageView.setMessageGravity(Gravity.START_TOP);
				break;
			case END_CENTER:
				mMessageView.setMessageGravity(Gravity.START_CENTER);
				break;
			case END_BOTTOM:
				mMessageView.setMessageGravity(Gravity.START_BOTTOM);
				break;
		}
	}

	public static class Builder {
		private View targetView;
		private String contentText;
		private Gravity gravity;
		private Context context;
		private Spannable contentSpan;
		private Typeface contentTypeFace;
		private GuideListener guideListener;
		private int contentTextSize;
		private Boolean isTouchOutsideEnabled;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTargetView(View view) {
			this.targetView = view;
			return this;
		}

		/**
		 * gravity GuideView
		 *
		 * @param gravity it should be one type of Gravity enum.
		 **/
		public Builder setGravity(Gravity gravity) {
			this.gravity = gravity;
			return this;
		}


		/**
		 * defining a description for the target view
		 *
		 * @param contentText a description. for example: this button can for submit your information..
		 **/
		public Builder setContentText(String contentText) {
			this.contentText = contentText;
			return this;
		}

		/**
		 * setting spannable type
		 *
		 * @param span a instance of spannable
		 **/
		public Builder setContentSpan(Spannable span) {
			this.contentSpan = span;
			return this;
		}

		/**
		 * setting font type face
		 *
		 * @param typeFace a instance of type face (font family)
		 **/
		public Builder setContentTypeFace(Typeface typeFace) {
			this.contentTypeFace = typeFace;
			return this;
		}

		/**
		 * adding a listener on show case view
		 *
		 * @param guideListener a listener for events
		 **/
		public Builder setGuideListener(GuideListener guideListener) {
			this.guideListener = guideListener;
			return this;
		}

		/**
		 * the defined text size overrides any defined size in the default or provided style
		 *
		 * @param size title text by sp unit
		 * @return builder
		 */
		public Builder setContentTextSize(int size) {
			this.contentTextSize = size;
			return this;
		}

		public Builder setTouchOutsideEnabled(boolean touchOutsideEnabled) {
			this.isTouchOutsideEnabled = touchOutsideEnabled;
			return this;
		}


		public GuideView build() {
			GuideView guideView = new GuideView(context, targetView);
			guideView.setViewGravity(gravity != null ? gravity : Gravity.BOTTOM_START);
			float density = context.getResources().getDisplayMetrics().density;
			if (contentText != null)
				guideView.setContentText(contentText);
			if (contentTextSize != 0)
				guideView.setContentTextSize(contentTextSize);
			if (contentSpan != null)
				guideView.setContentSpan(contentSpan);
			if (contentTypeFace != null) {
				guideView.setContentTypeFace(contentTypeFace);
			}
			if (isTouchOutsideEnabled != null) {
				guideView.setTouchOutsideEnabled(isTouchOutsideEnabled);
			}
			if (guideListener != null) {
				guideView.mGuideListener = guideListener;
			}
			return guideView;
		}


	}
}

