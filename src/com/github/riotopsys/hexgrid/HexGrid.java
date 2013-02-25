/**
 * Copyright 2013 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.github.riotopsys.hexgrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class HexGrid extends ViewGroup {

	@SuppressWarnings("unused")
	private static final String TAG = HexGrid.class.getSimpleName();
	
	private int r = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources()
					.getDisplayMetrics());
	private int wCount;
	private int hCount;
	private int widthSegment;
	private int widthExcess;
	private int unusedWidth;
	private int unusedHeight;
	private int supportedChildern;
	private boolean startIndented = false;
	private int maxColumns = Integer.MAX_VALUE;
	private int maxRows = Integer.MAX_VALUE;

	public HexGrid(Context context) {
		super(context);
	}

	public HexGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public HexGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Hexgrid);
		r = a.getDimensionPixelOffset(R.styleable.Hexgrid_radius,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						48, getResources().getDisplayMetrics()));

		startIndented = a.getBoolean(R.styleable.Hexgrid_start_indented, false);
		maxColumns = a.getInteger(R.styleable.Hexgrid_max_columns,
				Integer.MAX_VALUE);
		maxRows = a.getInteger(R.styleable.Hexgrid_max_rows, Integer.MAX_VALUE);

		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		widthSegment = (int) (Math.sqrt(3) * r);
		widthExcess = (int) (r / Math.sqrt(3));

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int usableWidth = width - (getPaddingLeft() + getPaddingRight())
				- widthExcess;
		int usableHeight = height - (getPaddingTop() + getPaddingBottom());

		wCount = usableWidth / widthSegment;
		wCount = Math.min(wCount, maxColumns);

		hCount = (usableHeight) / (2 * r);
		hCount = Math.min(hCount, maxRows);

		unusedWidth = usableWidth - wCount * widthSegment;
		unusedHeight = usableHeight - hCount * (2 * r);

		supportedChildern = wCount * hCount - (wCount / 2);
		if (startIndented) {
			supportedChildern--;
		}

		// Find out how big everyone wants to be
		int spec = MeasureSpec.makeMeasureSpec(2 * r, MeasureSpec.EXACTLY);
		measureChildren(spec, spec);

		//adjust for smallest size
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
			width -= unusedWidth;
			unusedWidth = 0;
		}

		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			height -= unusedHeight;
			unusedHeight = 0;
		}

		// Check against minimum height and width
		width = Math.max(width, getSuggestedMinimumWidth());
		height = Math.max(height, getSuggestedMinimumHeight());

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int a;
		int b;
		a = b = 0;

		int leftPositionFudge = getPaddingLeft() + (unusedWidth / 2)
				+ (4 * widthExcess - 2 * r) / 2;
		int topPositionFudge = getPaddingTop() + (unusedHeight / 2);

		for (int c = 0; c < supportedChildern; c++) {
			View child = getChildAt(c);
			if (child == null) {
				break;
			}
			if (child.getVisibility() != GONE) {
				int childLeft = a * widthSegment + leftPositionFudge;
				int childTop = b * 2 * r + topPositionFudge;
				if ((a % 2) == 1 ^ startIndented) {
					childTop += r;
				}
				child.layout(childLeft, childTop,
						childLeft + child.getMeasuredWidth(),
						childTop + child.getMeasuredHeight());
			}
			b++;
			if ((a % 2) == 0 ^ startIndented) {
				if (b >= hCount) {
					b = 0;
					a++;
				}
			} else {
				if (b >= hCount - 1) {
					b = 0;
					a++;
				}
			}
		}

	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new HexGrid.LayoutParams(getContext(), attrs);
	}

	// Override to allow type-checking of LayoutParams.
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof HexGrid.LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	public int getSupportedChildern() {
		return supportedChildern;
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

	}
}
