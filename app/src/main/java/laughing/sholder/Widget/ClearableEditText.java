package laughing.sholder.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import laughing.sholder.Adapter.RippleAnimatorAdapter;
import laughing.sholder.R;


public class ClearableEditText extends EditText {

	private RippleAnimatorAdapter rippleAnimator;

	private boolean showRipple = true;
	private boolean showClose = false;
	private boolean cursorAutoLast = false;
	private Drawable drawable = null;
	private Editable editable;


	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.ClearableEditText, defStyle, 0);

		showClose = a.getBoolean(R.styleable.ClearableEditText_clear_et_show_close,
				showClose);

		showRipple = a.getBoolean(R.styleable.ClearableEditText_clear_et_show_ripple,
				showRipple);

		cursorAutoLast = a.getBoolean(R.styleable.ClearableEditText_clear_et_cursor_auto_last,
				cursorAutoLast);
		a.recycle();
		init();
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClearableEditText(Context context) {
		this(context, null);
	}

	public void init() {
		rippleAnimator = new RippleAnimatorAdapter(this).setBlackBackground();
		drawable = getCompoundDrawables()[2];
		if (drawable == null) {
			drawable = getResources().getDrawable(R.mipmap.darkclose_24);
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		setClearIconVisible(false);

		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					setClearIconVisible(ClearableEditText.this.getText()
							.toString().length() != 0);
				} else {
					setClearIconVisible(false);
				}
			}
		});

		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ClearableEditText.this.setFocusableInTouchMode(true);
				return false;
			}
		});

		this.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(start!=0 && cursorAutoLast){
					editable = ClearableEditText.this.getText();
					Selection.setSelection(editable, editable.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isFocused()) {
					setClearIconVisible(ClearableEditText.this.getText()
							.toString().length() != 0);
				}
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		event.getActionMasked();
		if (rippleAnimator!=null && showRipple) {
			rippleAnimator.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				if (this.getCompoundDrawables()[2] != null && ifClicked(event)) {
					this.getText().clear();
					return true;
				}

				break;
			}

		}
		return super.onTouchEvent(event);
	}

	public boolean ifClicked(MotionEvent e) {
		int xRange = drawable.getIntrinsicWidth()+getPaddingRight() * 2;
		int minOfX = getWidth() -xRange;
		int realX = (int) (e.getX() - minOfX);

		boolean result = false;
		if (realX >= 0 && realX < xRange) {
			result = true;
		}

		return result;

	}

	protected void setClearIconVisible(boolean visiable) {
		if(!showClose){
			if(getCompoundDrawables()[2] != null){
				setCompoundDrawables(getCompoundDrawables()[0],
						getCompoundDrawables()[1],
						null,
						getCompoundDrawables()[3]);
			}
			return ;
		}

		boolean wasVisible = (getCompoundDrawables()[2] != null);
		if (visiable != wasVisible) {
			Drawable x = visiable ? drawable : null;
			setCompoundDrawables(getCompoundDrawables()[0],
					getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
		}
	}

	public void setShowClose(boolean showClose){
		this.showRipple = showClose;
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(rippleAnimator!=null && showRipple){
			rippleAnimator.onMeasure();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(rippleAnimator!=null && showRipple){
			rippleAnimator.onDraw(canvas);
		}
		super.onDraw(canvas);
	}
}
