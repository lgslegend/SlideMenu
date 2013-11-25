package cn.fu.slidemenu.scenes;

import java.util.ArrayList;


import javax.microedition.khronos.opengles.GL10;
import cn.fu.slidemenu.R;
import cn.fu.slidemenu.model.Particle;
import cn.fu.slidemenu.model.ParticleRect;
import cn.fu.slidemenu.model.TextureInfo;
import cn.fu.slidemenu.model.WeatherType;
import cn.fu.slidemenu.util.CacheTextrue;
import cn.fu.slidemenu.util.MatrixState;
import cn.fu.slidemenu.util.OpenGLUtils;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES20;
public class GLHazeScene extends BaseScene {

	private float mBreathInterval;  
	private float mOnInterval;	 
	private float mOffInterval;   
	private float mLastFrameTime;
	private int mCurrentAlpha;
	private float mFrameStep = 15;
	private boolean mLightOn;
	private boolean mDrawNotify;  
	
	private ArrayList<Particle> particleList = new ArrayList<Particle>();
	public GLHazeScene() {
		setCategory(WeatherType.HAZE);
		init();
	}
	private void calc() {
		mLastFrameTime = mLastFrameTime + mFrameStep;
		if (!mDrawNotify) {
			if (mLightOn) { // draw light on
				mCurrentAlpha = 255;
				if (mLastFrameTime > mOnInterval) {
					mDrawNotify = true;
					mLastFrameTime = 0;
				}
			} else { // draw light off
				mCurrentAlpha = 0;
				if (mLastFrameTime > mOffInterval) {
					mLightOn = true;
					mLastFrameTime = 0;
				}
			}
		} else {
			if (mLastFrameTime < mBreathInterval) { // draw flicker
				mCurrentAlpha = (int) (255 - 255*(mLastFrameTime/mBreathInterval));
				if (mCurrentAlpha < 0) {
					mCurrentAlpha = 0;
				}
			} else {
				mLastFrameTime = 0;
				mDrawNotify = false;
				mLightOn = false;
			}
		}
	}
	
	private void init() {
		this.mBreathInterval = 1200;
		mOnInterval = mBreathInterval - 600;
		mOffInterval = mBreathInterval - 600;
		mCurrentAlpha = 255;
		mLightOn = true;
		
		
		float scaleBig = 1.6f;
		if ((mDensity > 0.0F) && (mDensity <= 1.0F)) {
			scaleBig = 0.8f;
		} else if ((mDensity > 1.0F) && (mDensity <= 1.5F)) {
			scaleBig = 1.2f;
		} else if (mDensity > 1.5F) {
			scaleBig = 1.6f;
		}
		
		textureInfos.add(new TextureInfo(R.drawable.haze_bln_day,scaleBig));
		for (TextureInfo ti : textureInfos)
		{			
			Bitmap bitmap = OpenGLUtils.decodeResource(ti.drawableId, ti.scaleX	, ti.scaleY, 0);
			ti.textureId = OpenGLUtils.loadTexture(bitmap);
			ti.width = bitmap.getWidth();
			ti.height = bitmap.getHeight();
			bitmap.recycle();
		}
		
		float bigHalfWidth = textureInfos.get(0).width / 2.0f;
		float bigHalfHeight = textureInfos.get(0).height / 2.0f;
		
		Particle lightBig1 = new Particle();
		lightBig1.setXY(0.6792f * mScreenWidth - bigHalfWidth, 0.3475f * mScreenHeight - bigHalfHeight);
		lightBig1.setAlpha(255);
		lightBig1.setScale(scaleBig);
		lightBig1.setType(0);
		particleList.add(lightBig1);
		
		Particle lightBig2 = new Particle();
		lightBig2.setXY(0.608f * mScreenWidth - bigHalfWidth, 0.456f * mScreenHeight - bigHalfHeight);
		lightBig2.setAlpha(255);
		lightBig2.setScale(scaleBig);
		lightBig2.setType(0);
		particleList.add(lightBig2);
		
		Particle lightBig3 = new Particle();
		lightBig3.setXY(0.6792f * mScreenWidth - bigHalfWidth, 0.456f * mScreenHeight - bigHalfHeight);
		lightBig3.setAlpha(255);
		lightBig3.setScale(scaleBig);
		lightBig3.setType(0);
		particleList.add(lightBig3);
		
		Particle lightBig4 = new Particle();
		lightBig4.setXY(0.7583f * mScreenWidth - bigHalfWidth, 0.456f * mScreenHeight - bigHalfHeight);
		lightBig4.setAlpha(255);
		lightBig4.setScale(scaleBig);
		lightBig4.setType(0);
		particleList.add(lightBig4);
		
		
		for (Particle p : particleList)
		{			
			TextureInfo ti = textureInfos.get(p.getType());
			p.setTextureId(ti.textureId);
			p.setBmParams(ti.width * p.getScaleX() / ti.scaleX , ti.height * p.getScaleY() / ti.scaleY , true);    
		}
		
	}
	
	
	
	@Override
	public int loadGLTexture(GL10 gl) {
		for (TextureInfo ti : textureInfos)
		{		
			int value = CacheTextrue.get(ti.drawableId, ti.scaleX, ti.scaleY ,ti.angle);
			if (isNeedCaChe && value >0 )
			{
				ti.textureId = value;
				ti.width = CacheTextrue.getWidth(ti.drawableId, ti.scaleX, ti.scaleY ,ti.angle);
				ti.height = CacheTextrue.getHeight(ti.drawableId, ti.scaleX, ti.scaleY ,ti.angle);
			}else {			
				Bitmap bitmap = OpenGLUtils.decodeResource(ti.drawableId, ti.scaleX, ti.scaleY ,ti.angle);
				ti.textureId = OpenGLUtils.loadTexture(bitmap);
				ti.width = bitmap.getWidth();
				ti.height = bitmap.getHeight();
				if (isNeedCaChe)
				{					
					CacheTextrue.put(ti.drawableId, ti.scaleX, ti.scaleY ,ti.angle, ti.textureId,(int) ti.width,(int)ti.height);
				}
				bitmap.recycle();
			}		
		}
		for (Particle p : particleList)
		{			
			TextureInfo ti = textureInfos.get(p.getType());
			p.setTextureId(ti.textureId);
			p.setBmParams(ti.width * p.getScaleX() / ti.scaleX , ti.height * p.getScaleY() / ti.scaleY , true);    
		}
		isFirst = false;
		isLoadBitmap = true;
		return 0;
	}

	@Override
	public void draw(GL10 gl) {
		if (isLoadBitmap == false)
		{
			loadGLTexture(gl);
			return;
		}
		GLES20.glUseProgram(mProgram);
		GLES20.glVertexAttribPointer(maTexCoorHandle, 3, GLES20.GL_FLOAT, false, texture.length, mTexCoorBuffer);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLES20.glEnableVertexAttribArray(maTexCoorHandle);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		calc();
		for (int i=0; i<particleList.size(); i++)
		{
			Particle p = particleList.get(i);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, p.getTextureId());			
			GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, itemVertices.length, mVertexBuffer);					
		
			MatrixState.pushMatrix();	
			float glPos[] = MatrixState.convertUVToGLpoint(p.getX(), p.getY() ,mScreenWidth, mScreenHeight);
			MatrixState.translate(glPos[0], glPos[1], 3.0f);
			MatrixState.scale( p.getWidth() / UNIT, p.getHeight() / UNIT, 1);		
			float alpha = (float) p.getAlpha() / 255;
			GLES20.glUniform1f(mAlphaHandle,alpha * GLOBE_ALPHA);
			
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, vCount, GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);
			
			MatrixState.popMatrix();
			if (isStatic)
			{
				p.setAlpha(255);
			}else {
				p.setAlpha(mCurrentAlpha);				
			}
		}
	}
	
	@Override
	public String getBackground() {
		return "bg_haze";
	}

}
