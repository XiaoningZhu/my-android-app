package com.example.andorid.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 28243 on 2017/1/13.
 */
public class MyGridView extends GridView {

    private Bitmap background;
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs); //设置背景图片
        background = BitmapFactory.decodeResource(getResources(),
                R.drawable.bookshelf_layer_center);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) { //设置背景图的显示
        int count = getChildCount();
        int top = count > 0 ? getChildAt(0).getTop() : 0;
        int backgroundWidth = background.getWidth();
        int backgroundHeight = background.getHeight()+2;
        int width = getWidth();
        int height = getHeight();
        for (int y = top; y < height; y += backgroundHeight) {
            for (int x = 0; x < width; x += backgroundWidth) {
                canvas.drawBitmap(background, x, y, null);
            }
        }
        super.dispatchDraw(canvas);
    }


}
