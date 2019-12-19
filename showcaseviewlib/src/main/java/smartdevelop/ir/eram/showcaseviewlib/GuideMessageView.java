package smartdevelop.ir.eram.showcaseviewlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Spannable;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

/**
 * Created by Mohammad Reza Eram  on 20/01/2018.
 */

class GuideMessageView extends LinearLayout {

	private Paint mPaint;
	private RectF mRect;

	private TextView mContentTextView;
	private Gravity gravity;
	private Path pointerPath;
	private int rectRoundRadius = getPX(8);
	private int padding = getPX(8);


	GuideMessageView(Context context) {
		super(context);

		setWillNotDraw(false);
		setOrientation(VERTICAL);
		mRect = new RectF();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		pointerPath = new Path();
		mContentTextView = new TextView(context);
		mContentTextView.setTextColor(Color.BLACK);
		mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		mContentTextView.setPadding(padding, padding, padding, padding);
		mContentTextView.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
		addView(mContentTextView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}


	public void setContentText(String content) {
		mContentTextView.setText(content);
	}

	public void setContentSpan(Spannable content) {
		mContentTextView.setText(content);
	}

	public void setContentTypeFace(Typeface typeFace) {
		mContentTextView.setTypeface(typeFace);
	}

	public void setContentTextSize(int size) {
		mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
	}

	public void setTextAlignment(int textAlignment) {
		mContentTextView.setTextAlignment(textAlignment);
	}

	public void setColor(int color) {

		mPaint.setAlpha(255);
		mPaint.setColor(color);

		invalidate();
	}

	int location[] = new int[2];

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);


		this.getLocationOnScreen(location);

		boolean isRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
		if (gravity != null) {
			switch (gravity) {

				case TOP_START:
					setPaddingRelative(0, rectRoundRadius, 0, 0);
					calculatePath(isRtl ? Gravity.TOP_END : Gravity.TOP_START);
					break;
				case TOP_CENTER:
					setPaddingRelative(0, rectRoundRadius, 0, 0);
					calculatePath(Gravity.TOP_CENTER);
					break;
				case TOP_END:
					setPaddingRelative(0, rectRoundRadius, 0, 0);
					calculatePath(isRtl ? Gravity.TOP_START : Gravity.TOP_END);
					break;
				case BOTTOM_START:
					setPaddingRelative(0, 0, 0, rectRoundRadius);
					calculatePath(isRtl ? Gravity.BOTTOM_END : Gravity.BOTTOM_START);
					break;
				case BOTTOM_CENTER:
					setPaddingRelative(0, 0, 0, rectRoundRadius);
					calculatePath(Gravity.BOTTOM_CENTER);
					break;
				case BOTTOM_END:
					setPaddingRelative(0, 0, 0, rectRoundRadius);
					calculatePath(isRtl ? Gravity.BOTTOM_START : Gravity.BOTTOM_END);
					break;
				case START_TOP:
					setPaddingRelative(rectRoundRadius, 0, 0, 0);
					calculatePath(isRtl ? Gravity.END_TOP : Gravity.START_TOP);
					break;
				case START_CENTER:
					setPaddingRelative(rectRoundRadius, 0, 0, 0);
					calculatePath(isRtl ? Gravity.END_CENTER : Gravity.START_CENTER);
					break;
				case START_BOTTOM:
					setPaddingRelative(rectRoundRadius, 0, 0, 0);
					calculatePath(isRtl ? Gravity.END_BOTTOM : Gravity.START_BOTTOM);
					break;
				case END_TOP:
					setPaddingRelative(0, 0, rectRoundRadius, 0);
					calculatePath(isRtl ? Gravity.START_TOP : Gravity.END_TOP);
					break;
				case END_CENTER:
					setPaddingRelative(0, 0, rectRoundRadius, 0);
					calculatePath(isRtl ? Gravity.START_CENTER : Gravity.END_CENTER);
					break;
				case END_BOTTOM:
					setPaddingRelative(0, 0, rectRoundRadius, 0);
					calculatePath(isRtl ? Gravity.START_BOTTOM : Gravity.END_BOTTOM);
					break;
			}
		}
		canvas.drawRoundRect(mRect, rectRoundRadius, rectRoundRadius, mPaint);
		canvas.drawPath(pointerPath, mPaint);
	}

	private void calculateRect() {
		mRect.set(getPaddingLeft(),
				getPaddingTop(),
				getWidth() - getPaddingRight(),
				getHeight() - getPaddingBottom());
	}

	private void calculatePath(smartdevelop.ir.eram.showcaseviewlib.config.Gravity g) {
		calculateRect();
		pointerPath.reset();
		switch (g) {

			case TOP_START:
				pointerPath.moveTo(mRect.left, mRect.top - rectRoundRadius);
				pointerPath.lineTo(mRect.left, mRect.top + rectRoundRadius);
				pointerPath.lineTo(mRect.left + 2 * rectRoundRadius, mRect.top + rectRoundRadius);
				break;
			case TOP_CENTER:
				pointerPath.moveTo(mRect.centerX(), mRect.top - rectRoundRadius);
				pointerPath.lineTo(mRect.centerX() - 2 * rectRoundRadius, mRect.top + rectRoundRadius);
				pointerPath.lineTo(mRect.centerX() + 2 * rectRoundRadius, mRect.top + rectRoundRadius);
				break;
			case TOP_END:
				pointerPath.moveTo(mRect.right, mRect.top - rectRoundRadius);
				pointerPath.lineTo(mRect.right, mRect.top + rectRoundRadius);
				pointerPath.lineTo(mRect.right - 2 * rectRoundRadius, mRect.top + rectRoundRadius);
				break;
			case BOTTOM_START:
				pointerPath.moveTo(mRect.left, mRect.bottom - rectRoundRadius);
				pointerPath.lineTo(mRect.left, mRect.bottom + rectRoundRadius);
				pointerPath.lineTo(mRect.left + 2 * rectRoundRadius, mRect.bottom - rectRoundRadius);
				break;
			case BOTTOM_CENTER:
				pointerPath.moveTo(mRect.centerX(), mRect.bottom + rectRoundRadius);
				pointerPath.lineTo(mRect.centerX() - 2 * rectRoundRadius, mRect.bottom - rectRoundRadius);
				pointerPath.lineTo(mRect.centerX() + 2 * rectRoundRadius, mRect.bottom - rectRoundRadius);
				break;
			case BOTTOM_END:
				pointerPath.moveTo(mRect.right, mRect.bottom - rectRoundRadius);
				pointerPath.lineTo(mRect.right, mRect.bottom + rectRoundRadius);
				pointerPath.lineTo(mRect.right - 2 * rectRoundRadius, mRect.bottom - rectRoundRadius);
				break;
			case START_TOP:
				pointerPath.moveTo(mRect.left - rectRoundRadius, mRect.top);
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.top);
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.top + 2 * rectRoundRadius);
				break;
			case START_CENTER:
				pointerPath.moveTo(mRect.left - rectRoundRadius, mRect.centerY());
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.centerY() - 2 * rectRoundRadius);
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.centerY() + 2 * rectRoundRadius);
				break;
			case START_BOTTOM:
				pointerPath.moveTo(mRect.left - rectRoundRadius, mRect.bottom);
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.bottom);
				pointerPath.lineTo(mRect.left + rectRoundRadius, mRect.bottom - 2 * rectRoundRadius);
				break;
			case END_TOP:
				pointerPath.moveTo(mRect.right + rectRoundRadius, mRect.top);
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.top);
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.top + 2 * rectRoundRadius);
				break;
			case END_CENTER:
				pointerPath.moveTo(mRect.right + rectRoundRadius, mRect.centerY());
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.centerY() - 2 * rectRoundRadius);
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.centerY() + 2 * rectRoundRadius);
				break;
			case END_BOTTOM:
				pointerPath.moveTo(mRect.right + rectRoundRadius, mRect.bottom);
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.bottom);
				pointerPath.lineTo(mRect.right - rectRoundRadius, mRect.bottom - 2 * rectRoundRadius);
				break;
		}
		pointerPath.close();
	}

	private int getPX(float dp) {
		float density = getResources().getDisplayMetrics().density;
		return Math.round(dp * density);
	}

	public void setMessageGravity(smartdevelop.ir.eram.showcaseviewlib.config.Gravity mGravity) {
		gravity = mGravity;
	}
}
