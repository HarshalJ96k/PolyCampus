package com.polycampus.android.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class OverlayView extends View {
    private Rect boundingBox;
    private final Paint paint;

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);
    }

    public void setBoundingBox(Rect boundingBox) {
        this.boundingBox = boundingBox;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boundingBox != null) {
            canvas.drawRect(boundingBox, paint);
        }
    }
}
