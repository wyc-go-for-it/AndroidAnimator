package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawView extends View {
    private final Paint paint = new Paint();
    public DrawView(Context context) {
        this(context,null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        testRegion(canvas);
    }

    private void testRegion(final Canvas canvas){
        Path ovalPath = new Path();
        RectF rectF = new RectF(50,50,200,500);
        ovalPath.addOval(rectF,Path.Direction.CCW);
        Region region = new Region();
        region.setPath(ovalPath,new Region(50,50,200,200));
        drawRegion(canvas,region);
    }

    private void drawRegion(final Canvas canvas, Region region){
        final RegionIterator iterator = new RegionIterator(region);
        final Rect r = new Rect();
        while (iterator.next(r)){
            canvas.drawRect(r,paint);
        }
    }
}
