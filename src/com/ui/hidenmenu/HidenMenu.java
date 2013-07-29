package com.ui.hidenmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

@SuppressLint("ViewConstructor")
public class HidenMenu extends ViewGroup {

	private ViewGroup menu_View = null;
	private ViewGroup content_View = null;
	private ScrollController scroller = null;
	
	public HidenMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.init(context,attrs);
	}

	public HidenMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.init(context,attrs);
	}
	
	private void init(Context context, AttributeSet attrs)
	{
		setClipChildren(false);
	    setClipToPadding(false);
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollMenuAttrs);
		final int menuLayout = a.getResourceId(R.styleable.ScrollMenuAttrs_menu_layout, 0);
	    final int contentLayout = a.getResourceId(R.styleable.ScrollMenuAttrs_content_layout, 0);
	    a.recycle();
	    
	    if(menuLayout == 0 || contentLayout == 0)
	    	return;
	    
	    this.menu_View = new FrameLayout(context);
	    LayoutInflater inflater = LayoutInflater.from(context);
	    inflater.inflate(menuLayout, this.menu_View, true);	    
	    this.addView(this.menu_View, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    
	    this.content_View = new FrameLayout(context);
	    inflater.inflate(contentLayout, this.content_View);
	    this.addView(this.content_View, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    this.scroller = new ScrollController(new Scroller(context));
	}	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		
		for(int idx = 0;idx < this.getChildCount();idx++)
		{
			View v= this.getChildAt(idx);			
			if(v.equals(menu_View))
				v.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
			else
				v.measure(widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final int childrenCount = getChildCount();
	    
	    for (int i=0; i<childrenCount; ++i) {
	      final View v = getChildAt(i);
	      v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
	    }
	}
		
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		if(this.scroller.isOpen() && ev.getY() >= -content_View.getScrollY())
		{
			this.scroller.CloseMenu();
			return false;
		}
		else
			return super.dispatchTouchEvent(ev);
	}

	public void ToggleMenu()
	{
		this.scroller.Toggle();
	}
	
	private class ScrollController implements Runnable
	{
		private Scroller scroller;

		public ScrollController(Scroller scroller)
		{
			this.scroller = scroller;
		}
		
		public void OpenMenu()
		{			
			if(!this.isOpen())
			{
				this.scroller.startScroll(0, 0, 0, -menu_View.getMeasuredHeight());
				content_View.post(this);
			}
		}
		
		public void CloseMenu()
		{		
			if(this.isOpen())
			{
				this.scroller.startScroll(0, -menu_View.getMeasuredHeight(), 0, menu_View.getMeasuredHeight());
				content_View.post(this);
			}
		}
		
		public void Toggle()
		{
			if(this.isOpen())
				this.CloseMenu();
			else
				this.OpenMenu();
		}
		
		public boolean isOpen()
		{
			return content_View.getScrollY() != 0;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(this.scroller.computeScrollOffset())
				content_View.post(this);
			else
				this.scroller.forceFinished(true);			
			
			content_View.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
		}		
	}
}
