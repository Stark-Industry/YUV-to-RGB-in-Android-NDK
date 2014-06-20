package in.manishpathak.videodemo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class OverlayView extends SurfaceView  implements SurfaceHolder.Callback {
	public Camera mCam;
	private SurfaceHolder mCamSH;
	private SurfaceHolder mOverSH;
	private byte[] mFrame;
	private IntBuffer mFrameDiff;
	private Camera.Size mFrameSize;
	private boolean mRunning;
	private OverlayView mOverSV;
	int width = 640;
	int height = 480;
	
	static {
        System.loadLibrary("hello-jni");
    }
//	public native String  stringFromJNI();
	 private native void YUVtoRBG(int[] rgb, byte[] yuv, int width, int height);
	
	public OverlayView(Context c, AttributeSet attr) {
		super(c, attr);
		mOverSH = getHolder();
		mOverSH.addCallback(this);
	}
	
	 

	public void setCamera(Camera c)
	{
		
		mCam = c;
		
		if(mCam == null) return;
		final Bitmap bmp =  Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		Camera.Parameters p = mCam.getParameters();
		int dataBufferSize = ImageFormat.getBitsPerPixel(p.getPreviewFormat()) / 8 * p.getPreviewSize().height * p.getPreviewSize().width;
		
		mCam.addCallbackBuffer(new byte[dataBufferSize]);
		//dataBufferSize stands for the byte size for a picture frame
		mCam.addCallbackBuffer(new byte[dataBufferSize]);
		mCam.addCallbackBuffer(new byte[dataBufferSize]);
				
		mCam.setPreviewCallbackWithBuffer(new PreviewCallback()
		{
			private int[] out;
			int frameSize = width*height;
		    int[] rgba = new int[frameSize+1];
			public void onPreviewFrame(final byte[] data, Camera c)
			{
				
		    long startTime = System.currentTimeMillis();	  
		    YUVtoRBG(rgba, data, width, height);
		    bmp.setPixels(rgba, 0/* offset */, width /* stride */, 0, 0, width, height);
		    long middleTime = System.currentTimeMillis();
		    
		    long executionTime1 = middleTime - startTime;		    
		    
		    /*
		    startTime = System.currentTimeMillis();	  
		    YuvImage yuvimage;
		    ByteArrayOutputStream baos;
	 
		    yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
	 
		    baos = new ByteArrayOutputStream();
		    yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
			
		    Bitmap bmpTemp = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
		    middleTime = System.currentTimeMillis();
		   
		    long e = middleTime - startTime;
		    */
		     
		    Canvas canvas = mOverSH.lockCanvas();
		    if (canvas != null) {
		        canvas.drawBitmap(bmp, (canvas.getWidth() - width) / 2, (canvas.getHeight() - height) / 2, null);
		        mOverSH.unlockCanvasAndPost(canvas);
		    } else {
		        Log.w("canvas", "Canvas is null!");
		    }
		    
		    long endTime = System.currentTimeMillis();
		    
		    long executionTime2 = endTime - middleTime;

	//	    Log.w("ExecutionTime", executionTime1 + " " + e + " " + executionTime2);
		    Log.w("ExecutionTime", executionTime1 + " " + executionTime2);
		    
		    mCam.addCallbackBuffer(data);
		    }	
			 
		});
	}

	public void setRunning(boolean r)
	{
		mRunning = r;
	}
	

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	// decode Y, U, and V values on the YUV 420 buffer described as YCbCr_422_SP by Android 
	// David Manpearl 081201 
	

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}
	
	

}
