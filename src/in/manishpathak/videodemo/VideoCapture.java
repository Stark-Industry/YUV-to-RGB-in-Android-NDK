package in.manishpathak.videodemo;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class VideoCapture extends Activity implements SurfaceHolder.Callback {
	MediaRecorder recorder;
	SurfaceHolder holder;
	boolean recording = false;
	private Button buttonRecording;
		private Button buttonFlipFrontToRear;
	private Camera mCamera;
	boolean isCameraOPen;
	private OverlayView mOverSV;	
	File file;
	private SurfaceView mCamSV; 
	private SurfaceHolder mCamSH;
	private Camera mCam;
	private boolean mPreview;
	private Camera.Size mPreviewSize = null;

	public static int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
	
	private SurfaceTexture mSurfaceTexture;
	private static final int MAGIC_TEXTURE_ID = 10;
	
	static {
        System.loadLibrary("hello-jni");
    }
	public native String  stringFromJNI();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		// for S2 it would be fine
//		mCamera = Camera.open();
//		isCameraOPen = true;

		
//		initRecorder();
		setContentView(R.layout.activity_main);
		
	}
	private void initCamera() {
		mCamSV = (SurfaceView)findViewById(R.id.mypreview);
		mCamSH = mCamSV.getHolder();
		mCamSH.addCallback(this);
		mCamSH.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		ViewGroup.LayoutParams params = mCamSV.getLayoutParams();                     
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);               
		params.width =dm.widthPixels/2;
		params.height = dm.heightPixels;
		mCamSV.setLayoutParams(params);

		mOverSV = (OverlayView)findViewById(R.id.ScrollView01);
		mOverSV.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		
		if(mCam == null)
		{
			mCam = Camera.open();
		}
		mOverSV.setCamera(mCam);
//		mOverSV.setRunning(true);
		mPreview = false;
		
//		Toast.makeText(getContext(), stringFromJNI(), Toast.LENGTH_SHORT).show();
		 Log.d("JNI HELLO:   ",""+stringFromJNI());
			 
		 mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
	}

	private void stopCamera()
	{
		//		mOEL.disable();
		mOverSV.setRunning(false);
		mCam.stopPreview();
		mPreview = false;
		mCam.setPreviewCallback(null);
		mCam.release();
		mCam = null;
	}


	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceChanged(SurfaceHolder sh, int format, int width,
			int height) {
		//		pmeh.preErrStr += "Surface parameters changed: "+w+"x"+h+"\n";
		if(mCam != null)
		{
			if(mPreview) mCam.stopPreview();
			Camera.Parameters p = mCam.getParameters();
			
			//		    p.setRotation(mOrient);
			for(Camera.Size s : p.getSupportedPreviewSizes())
			{
//				p.setPreviewSize(s.width, s.height);
				p.setPreviewSize(640,480);
				mPreviewSize = s;
						        Log.v("videocapture", "Supported preview: "+s.width+"x"+s.height);
//				pmeh.preErrStr += "Supported preview: "+s.width+"x"+s.height+"\n";
				break;
			}
			mCam.setParameters(p);
			try
			{
				mCam.setPreviewTexture(mSurfaceTexture);		
		//		mCam.setPreviewDisplay(mCamSV.getHolder());
			}
			catch(Exception e)
			{
				//		    	Log.e(LOGTAG, "Camera preview not set");
			}
			mCam.startPreview();
			
			mPreview = true;
		}
		//		Toast.makeText(VideoCapture.this, "surface changed", Toast.LENGTH_LONG).show();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		//		Toast.makeText(VideoCapture.this, "surface destroyed", Toast.LENGTH_LONG).show();
		if (recording) {
			recorder.stop();
			recording = false;
		}
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		recorder.release();
		recorder = null;
		finish();
	}



	@Override
	protected void onPause() {
		//		Toast.makeText(VideoCapture.this, "on pause", Toast.LENGTH_LONG).show();
		if(file!=null)
			file.delete();
		stopCamera();
    	mCamSH.removeCallback(this);
    	this.finish();
		super.onPause();
	}

	@Override
	protected void onResume() {
		//		Toast.makeText(VideoCapture.this, "on resume", Toast.LENGTH_LONG).show();
//		onStart();
		initCamera();
		super.onResume();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}