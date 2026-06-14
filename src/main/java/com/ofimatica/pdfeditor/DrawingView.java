package com.ofimatica.pdfeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import java.io.InputStream;

public class DrawingView extends View {

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint drawPaint;
    private Paint eraserPaint;
    private float lastX, lastY;
    private boolean isDrawing = false;
    private boolean isEraser = false;

    public DrawingView(Context context, Bitmap pageBitmap) {
        super(context);
        this.bitmap = pageBitmap.copy(pageBitmap.getConfig(), true);
        this.canvas = new Canvas(bitmap);
        
        // Pincel para dibujo
        drawPaint = new Paint();
        drawPaint.setColor(0xFF000000);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setAntiAlias(true);

        // Pincel para borrador
        eraserPaint = new Paint();
        eraserPaint.setStrokeWidth(20);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawing) return false;
        
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Paint currentPaint = isEraser ? eraserPaint : drawPaint;
                canvas.drawLine(lastX, lastY, x, y, currentPaint);
                invalidate();
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                Paint finalPaint = isEraser ? eraserPaint : drawPaint;
                canvas.drawLine(lastX, lastY, x, y, finalPaint);
                invalidate();
                break;
        }
        return true;
    }

    public void enableDrawing() {
        isDrawing = true;
        isEraser = false;
    }

    public void enableEraser() {
        isDrawing = true;
        isEraser = true;
    }

    public void addText(String text, Paint paint) {
        if (canvas != null) {
            canvas.drawText(text, 50, 150, paint);
            invalidate();
        }
    }

    public void addImage(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

            int maxWidth = 500;
            int maxHeight = 300;
            
            if (imageBitmap.getWidth() > maxWidth || imageBitmap.getHeight() > maxHeight) {
                float scaleX = (float) maxWidth / imageBitmap.getWidth();
                float scaleY = (float) maxHeight / imageBitmap.getHeight();
                float scale = Math.min(scaleX, scaleY);
                
                int newWidth = (int) (imageBitmap.getWidth() * scale);
                int newHeight = (int) (imageBitmap.getHeight() * scale);
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, true);
            }

            canvas.drawBitmap(imageBitmap, 50, 200, null);
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}