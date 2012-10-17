package com.moarub.sharemore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.view.View;

public class GettingStartedView extends View {
	private static float LEFT_TEXT = 114.f;
	private static float RIGHT_TEXT = 468.f;

	public GettingStartedView(Context context) {
		super(context);
		Options options = new Options();
		options.inMutable = true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint blur = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG|Paint.FILTER_BITMAP_FLAG);
		//BlurMaskFilter bff = new BlurMaskFilter(10.f, Blur.NORMAL);
		//blur.setMaskFilter(bff);
		//canvas.drawBitmap(fBm,new Rect(0,0,canvas.getWidth(),canvas.getHeight()),new Rect(0,0,canvas.getWidth(),canvas.getHeight()),blur);
		
		TextPaint sm = new TextPaint(Paint.ANTI_ALIAS_FLAG|Paint.SUBPIXEL_TEXT_FLAG|Paint.DEV_KERN_TEXT_FLAG);
		sm.setTypeface(Typeface.DEFAULT_BOLD);
		sm.setTextSize(getWidth() / 5.5f);
		sm.setColor(Color.WHITE);
		//sm.setShadowLayer(10.f, 7, 7, Color.BLACK);
		DynamicLayout dl = new DynamicLayout("Sharemore", sm, getWidth(), Alignment.ALIGN_NORMAL, 0.f, 0.f, true);
		
		
		
		Paint pm = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
		Paint lightGray = new Paint(pm);
		lightGray.setColor(Color.argb(0xff, 0xcc, 0xcc, 0xcc));
		lightGray.setStyle(Style.STROKE);
		
		
		
		Rect r = new Rect(10,10,dl.getWidth()-10,getHeight()-10);
		pm.setColor(Color.argb(0xcc, 0xff, 0xff, 0xff));
		canvas.drawRect(r, pm );
		canvas.drawRect(r, lightGray);
		
		int titleB = dl.getLineBaseline(0)+10;
		RectF title = new RectF(0,0,getWidth(),titleB);
		pm.setColor(Color.DKGRAY);
		canvas.drawRect(title, pm);
		RectF titleG = new RectF(0,titleB,getWidth(),titleB+10);
		Paint titleGP = new Paint(pm);
		LinearGradient tlg = new LinearGradient(0, titleB, 0, titleB+10, Color.DKGRAY, Color.TRANSPARENT, TileMode.REPEAT);
		titleGP.setShader(tlg);
		canvas.drawRect(titleG, titleGP);
		
		
		canvas.save();
		canvas.translate(10, 0);
		dl.draw(canvas);
		canvas.restore();
		
		
	}

}
