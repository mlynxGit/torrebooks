package com.maghribpress.torrebook.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

import com.maghribpress.torrebook.R;

public class BookShelfView extends GridView {

    private Bitmap background;
    public BookShelfView(Context context) {
        super(context);
        init();
    }
    public BookShelfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public BookShelfView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    protected void init() {
        //Set the background image of the ShelfView panel.
        background = BitmapFactory.decodeResource(getResources(), R.drawable.shelf_panel_new);
    }
    //Draw a background in the screen and create multiple panels using height & width.
    @Override
    protected void dispatchDraw(Canvas canvas) {
        int top = getChildCount() > 0 ? getChildAt(0).getTop() : 0;
        for (int y = top; y < getHeight(); y += background.getHeight()) {
            for (int x = 0; x < getWidth(); x += background.getWidth()) {
                canvas.drawBitmap(background, x, y, null);
            }
        }
        super.dispatchDraw(canvas);
    }
}
