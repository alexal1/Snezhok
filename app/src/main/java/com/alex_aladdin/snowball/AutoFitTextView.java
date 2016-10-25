package com.alex_aladdin.snowball;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoFitTextView extends TextView {
    private float minTextSize;
    private float maxTextSize;
    private OnSizeChangeListener mListener;

    public AutoFitTextView(Context context) {
        super(context);
        init();
    }

    public AutoFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        maxTextSize = this.getTextSize();
        if (maxTextSize < 35) {
            maxTextSize = 30;
        }
        minTextSize = 20;
    }

    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight();
            float trySize = maxTextSize;

            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            while ((trySize > minTextSize)
                    && (this.getPaint().measureText(text) > availableWidth)) {
                trySize -= 1;
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
            //Теперь должно сработать событие выставления нового размера
            if (mListener != null) mListener.onEvent();
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start,
                                 final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        if (w != old_w) {
            refitText(this.getText().toString(), w);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        refitText(this.getText().toString(), parentWidth);
    }

    //Создаем интерфейс, с которым будет работать активность (событие выставления подходящего размера)
    public interface OnSizeChangeListener {
        void onEvent();
    }

    public void setSizeChangeListener(OnSizeChangeListener eventListener) {
        mListener = eventListener;
    }
}