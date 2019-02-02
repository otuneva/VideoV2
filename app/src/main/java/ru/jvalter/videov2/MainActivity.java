package ru.jvalter.videov2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    Camera camera;
    MediaRecorder mediaRecorder;

    File videoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        videoFile = new File(pictures, "myvideo.mp4");

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        SurfaceHolder holder= surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                } catch (Exception e) { e.printStackTrace(); }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        camera= Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        if (camera != null)
            camera.release();
        camera = null;
    }
    public void onClickStartRecord(View view) {
        if(prepareVideoRecorder())
        {
            mediaRecorder.start();
        } else
            {
                releaseMediaRecorder();
            }

    }
    public void onClickStopRecord(View view) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            releaseMediaRecorder();
        }
    }
    private boolean prepareVideoRecorder()
    {
        camera.unlock();

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    private void releaseMediaRecorder()
    {
        if(mediaRecorder!=null)
        {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder=null;
            camera.lock();
        }
    }
}
