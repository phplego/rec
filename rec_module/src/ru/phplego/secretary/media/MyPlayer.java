package ru.phplego.secretary.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import ru.phplego.secretary.db.ActiveCall;
import ru.phplego.core.debug.Log;
import java.util.HashSet;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 22.04.12
* Time: 1:36
* To change this template use File | Settings | File Templates.
*/

public class MyPlayer implements MediaPlayer.OnCompletionListener {
	private MediaPlayer mPlayer =new MediaPlayer();

	private ActiveCall mCurrentCall;

	private HashSet <MediaPlayer.OnCompletionListener> mOnCompletionListeners;

	private HashSet <OnProgressListenter> mOnProgressListeners;

	private Handler mHandler =new Handler();

	private int mLastDuration =0;

	private boolean mIsPaused =false;

	public interface OnProgressListenter {
		public void onProgress(int progress);

		public Context getContext();

	}
	public MyPlayer() {
		mOnCompletionListeners = new HashSet<MediaPlayer.OnCompletionListener>();
		mOnProgressListeners = new HashSet<OnProgressListenter>();
		mPlayer = createMediaPlayer();
		startProgressUpdater();

		if(App.isVersionFree()){
				// Сообщение о присутствии лимита
				setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
								if(!mIsPaused && mLastDuration >= CallRecorder.MAX_DURATION_LIMIT)
										App.toast(R.string.duration_limit_reached);
						}
				});
		}
	}

	private MediaPlayer createMediaPlayer() {
		MediaPlayer mp = MediaPlayer.create(App.getContext(), R.raw.switch_out);
		if(mp == null){
			throw new Error("Cannot create MediaPlayer. Method 'MediaPlayer.create()' fails.");
		}
		mp.setOnCompletionListener(this);
		mp.reset();
		return mp;
	}

	private int mLastProgress =0;

	private void startProgressUpdater() {
		int position = 1;
		if(getDuration() > 0)
				position = getCurrentPosition() * 1000 / getDuration();
		// Если позиция осталась прежней, нет смысла вызывать обработчики
		if(mLastProgress != position){
				for(OnProgressListenter one: mOnProgressListeners)
						one.onProgress(position);
		}
		mLastProgress = position;
		Runnable notification = new Runnable() {
				public void run() {
						startProgressUpdater();
				}
		};
		mHandler.postDelayed(notification, 100);
	}

	public boolean play(ActiveCall ac) {
		Log.d("!");
		boolean allow_to_resume = isPaused() && isCurrent(ac);
		Log.d("allow_to_resume = "+allow_to_resume);
		if(allow_to_resume){
				mIsPaused = false;
				mPlayer.start();
				return true;
		}

		if(isPlaying() || isPaused()){
				Log.d("isPlaying() || isPaused()");
				stop();
		}

		mCurrentCall = ac;
		mPlayer = createMediaPlayer();

		try{
				Log.d("file_name = " + ac.getFilename());
				mPlayer.setDataSource(ac.getFilename());

				mPlayer.prepare();
		}
		catch (Exception e){
				Log.d("Exception prepare!");
				stop();
				return false;
		}
		mLastDuration = mPlayer.getDuration();

		mIsPaused = false;
		mPlayer.start();
		return true;
	}

	public void pause() {
		mPlayer.pause();
		mIsPaused = true;
	}

	public void stop() {
		Log.d("!");
		if(isPlaying()) mPlayer.stop();
		onCompletion(mPlayer);
		mCurrentCall = null;
	}

	public ActiveCall getCurrentCall() {
		return mCurrentCall;
	}

	public boolean isCurrent(ActiveCall ac) {
		if(mCurrentCall == null) return false;
		if(ac == null) return false;
		return ac.getId() == mCurrentCall.getId();
	}

	public boolean isPlaying() {
		try{
				boolean playing = mPlayer.isPlaying();
				Log.d("playing="+playing);
				return playing;
		}
		catch (Exception e){}
		return false;
	}

	public boolean isPaused() {
		return mIsPaused;
		/*
		try{
				int cur_pos = mPlayer.getCurrentPosition();
				boolean paused = cur_pos > 0 && cur_pos < 7200000 && !isPlaying();
				Log.d("paused="+paused);
				Log.d("currentPosition="+mPlayer.getCurrentPosition());
				return paused;
		}catch (Exception e){
				return false;
		} */
	}

	public int getDuration() {
		if(mCurrentCall == null) return 0;
		return mPlayer.getDuration();
	}

	public int getCurrentPosition() {
		return mPlayer.getCurrentPosition();
	}

	public void seekTo(int position) {
		try{
				mPlayer.seekTo(position);
		}catch (Exception e){}
	}

	public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
		//mOnCompletionListeners.clear();
		mOnCompletionListeners.add(listener);
		Log.d("mOnCompletionListeners.size(): "+mOnCompletionListeners.size());
	}

	public void removeOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
		mOnCompletionListeners.remove(listener);
	}

	public void setOnProgressListener(OnProgressListenter listener) {
		//mOnProgressListeners.clear();
		mOnProgressListeners.add(listener);
		Log.d("mOnProgressListeners.size(): "+mOnProgressListeners.size());
	}

	public void removeOnProgressListener(OnProgressListenter listener) {
		mOnProgressListeners.remove(listener);
	}

	public void removeOnProgressListeners(Context context) {
		HashSet forRemove = new HashSet();
		for(OnProgressListenter one: mOnProgressListeners)
				if(context.equals(one.getContext()))
						forRemove.add(one);
		for(Object one: forRemove)
				mOnProgressListeners.remove((OnProgressListenter)one);
	}

	public void onCompletion(MediaPlayer mediaPlayer) {
		Log.d("! mOnCompletionListeners="+mOnCompletionListeners.size());
		for(MediaPlayer.OnCompletionListener listener: mOnCompletionListeners)
				listener.onCompletion(mPlayer);
		mCurrentCall = null;
		try{ mPlayer.reset(); } catch (Exception e){}
		try{ mPlayer.release(); } catch (Exception e){}
	}

}
