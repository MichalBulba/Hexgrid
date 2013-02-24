/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.riotopsys.hexgrid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class HexGrid extends ViewGroup {

	private static final String TAG = HexGrid.class.getSimpleName();
	private int r = 48;
	private int wCount;
	private int hCount;
	private int widthSegment;
	private int widthExcess;
	private int unusedWidth;
	private int unusedHeight;

	public HexGrid(Context context) {
		super(context);
	}

	public HexGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HexGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		widthSegment = (int) (Math.sqrt(3) * r);
		widthExcess = (int) (r / Math.sqrt(3));
		Log.i(TAG, String.format("ws %d, we %d", widthSegment, widthExcess));

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		Log.i(TAG, String.format("w %d, h %d", width, height));

		int usableWidth = width - (getPaddingLeft() + getPaddingRight()) - widthExcess;
		int usableHeight = height - (getPaddingTop() + getPaddingBottom());

		wCount = usableWidth / widthSegment;
		hCount = (usableHeight) / (2 * r);
		Log.i(TAG, String.format("wc %d, hc %d", wCount, hCount));
		
		unusedWidth = usableWidth - wCount * widthSegment;
		unusedHeight = usableHeight - hCount * (2 * r);
		Log.i(TAG, String.format("unusedWidth %d, unusedHeight %d", unusedWidth, unusedHeight));

		int supportedChildern = wCount * hCount - (wCount / 2);
		Log.i(TAG, String.format("sc %d", supportedChildern));
		Log.i(TAG, "run over");

		// Find out how big everyone wants to be
		measureChildren(MeasureSpec.makeMeasureSpec(4 * widthExcess,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(2 * r,
				MeasureSpec.EXACTLY));

		// Check against minimum height and width
		width = Math.max(width, getSuggestedMinimumWidth());
		height = Math.max(height, getSuggestedMinimumHeight());

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}

	/**
	 * Returns a set of layout parameters with a width of
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}, a height of
	 * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and with the
	 * coordinates (0, 0).
	 */
	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, 0, 0);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int supportedChildern = wCount * hCount - (wCount / 2);
		int a;
		int b;
		a = b = 0;
		for (int c = 0; c < supportedChildern; c++) {
			Log.i(TAG, String.format("a %d, b %d, c %d", a, b, c));
			View child = getChildAt(c);
			if (child == null) {
				break;
			}
			if (child.getVisibility() != GONE) {
				int childLeft = getPaddingLeft() + a * widthSegment + (unusedWidth/2);
				int childTop = getPaddingTop() + b * 2 * r + (unusedHeight/2 );
				Log.i(TAG, String.format("%d + %d * 2 * %d", getPaddingTop(),
						b, r));
				if ((a % 2) == 1) {
					childTop += r;
				}
				Log.i(TAG, String.format("childLeft %d, childTop %d",
						childLeft, childTop));
				child.layout(childLeft, childTop,
						childLeft + child.getMeasuredWidth(),
						childTop + child.getMeasuredHeight());
			}
			b++;
			if ((a % 2) == 0) {
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

		// for (int i = 0; i < count; i++) {
		// View child = getChildAt(i);
		// if (child == null) {
		// break;
		// }
		// if (child.getVisibility() != GONE) {
		//
		// HexGrid.LayoutParams lp = (HexGrid.LayoutParams) child
		// .getLayoutParams();
		//
		// int childLeft = getPaddingLeft() + lp.x;
		// int childTop = getPaddingTop() + lp.y;
		// child.layout(childLeft, childTop,
		// childLeft + child.getMeasuredWidth(),
		// childTop + child.getMeasuredHeight());
		//
		// } else {
		// count++;
		// }
		// }
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

	/**
	 * Per-child layout information associated with AbsoluteLayout. See
	 * {@link android.R.styleable#AbsoluteLayout_Layout Absolute Layout
	 * Attributes} for a list of all child view attributes that this class
	 * supports.
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {
		/**
		 * The horizontal, or X, location of the child within the view group.
		 */
		public int x;
		/**
		 * The vertical, or Y, location of the child within the view group.
		 */
		public int y;

		/**
		 * Creates a new set of layout parameters with the specified width,
		 * height and location.
		 * 
		 * @param width
		 *            the width, either {@link #FILL_PARENT},
		 *            {@link #WRAP_CONTENT} or a fixed size in pixels
		 * @param height
		 *            the height, either {@link #FILL_PARENT},
		 *            {@link #WRAP_CONTENT} or a fixed size in pixels
		 * @param x
		 *            the X location of the child
		 * @param y
		 *            the Y location of the child
		 */
		public LayoutParams(int width, int height, int x, int y) {
			super(width, height);
			this.x = x;
			this.y = y;
		}

		/**
		 * Creates a new set of layout parameters. The values are extracted from
		 * the supplied attributes set and context. The XML attributes mapped to
		 * this set of layout parameters are:
		 * 
		 * <ul>
		 * <li><code>layout_x</code>: the X location of the child</li>
		 * <li><code>layout_y</code>: the Y location of the child</li>
		 * <li>All the XML attributes from
		 * {@link android.view.ViewGroup.LayoutParams}</li>
		 * </ul>
		 * 
		 * @param c
		 *            the application environment
		 * @param attrs
		 *            the set of attributes fom which to extract the layout
		 *            parameters values
		 */
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
			// TypedArray a = c.obtainStyledAttributes(attrs,
			// com.android.internal.R.styleable.AbsoluteLayout_Layout);
			// x = a.getDimensionPixelOffset(
			// com.android.internal.R.styleable.AbsoluteLayout_Layout_layout_x,
			// 0);
			// y = a.getDimensionPixelOffset(
			// com.android.internal.R.styleable.AbsoluteLayout_Layout_layout_y,
			// 0);
			// a.recycle();
		}

		/**
		 * {@inheritDoc}
		 */
		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		// @Override
		// public String debug(String output) {
		// return output + "Absolute.LayoutParams={width="
		// + sizeToString(width) + ", height=" + sizeToString(height)
		// + " x=" + x + " y=" + y + "}";
		// }
	}
}
