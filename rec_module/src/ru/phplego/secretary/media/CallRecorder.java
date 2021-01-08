package ru.phplego.secretary.media;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import ru.phplego.core.AndroidUtils;
import ru.phplego.secretary.App;
import ru.phplego.secretary.Prefs;
import ru.phplego.secretary.debug.Crash;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallRecorder {
	public static final boolean AAC_SUPPORTED =Build.VERSION.SDK_INT >= 10;

	public static final int RECORDING_FORMAT_MPEG4 =1;

	public static final int RECORDING_FORMAT_3GP =2;

	public static final int RECORDING_FORMAT_WAV =3;

	public static final int MAX_DURATION_LIMIT =60000;

	private static CallRecorder mInstance =null;

	private String mFilesFolderName;

	private boolean mIsRecording =false;

	private long mLastStartTime;

	private long mLastStopTime;

	private int mAudioSource;

	private int mRecordingFormat;

	private int mBitRate =0;

	private MediaRecorder mMediaRecorder =new MediaRecorder();

	private ExtAudioRecorder mExtAudioRecorder =
	ExtAudioRecorder.getInstanse(false)
	;

	String path;

	private long current_record_id;

	public class CardNotMountedException extends IOException {
		public CardNotMountedException(String s) {
			super(s);
		}

	}
	private CallRecorder() {
		setFilename("default_call");
		current_record_id = 0;
		mInstance = this;
	}

	public static CallRecorder getInstance() {
		if(mInstance == null){
				mInstance = new CallRecorder();
		}
		mInstance.setFilesFolderName(Prefs.getRecordsPath());
		mInstance.setAudioSource(Prefs.list_preference_recording_audio_source.getInt(0));
		mInstance.setBitRate(Prefs.list_preference_recording_bitrate.getInt(64000));
		mInstance.setRecrodingFormat(Prefs.list_preference_recording_format.getInt(CallRecorder.RECORDING_FORMAT_MPEG4));
		return mInstance;
	}

	public static boolean isRecordingSt() {
		if(mInstance == null) return false;
		return mInstance.mIsRecording;
	}

	public void setAudioSource(int value) {
		this.mAudioSource = value;
	}

	public void setRecrodingFormat(int format) {
		// Если запись в MPEG4 не поддерживается
		if(!AAC_SUPPORTED){
				this.mRecordingFormat = RECORDING_FORMAT_3GP;
				return;
		}
		this.mRecordingFormat = format;
	}

	public void setBitRate(int value) {
		this.mBitRate = value;
	}

	public void setFilename(String filename) {
		filename = filename.replace("+", "");
		filename = filename.replace("*", "_star_");
						Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss");
						String full_path = mFilesFolderName +"/"+sdf.format(d)+"-"+filename;

		if (!full_path.startsWith("/")) {
				full_path = "/" + full_path;
		}

		boolean corrupting = Prefs.checkbox_preference_corrupt_extension.get(false);
		if (!full_path.contains(".")) {
				switch(this.mRecordingFormat){
						case RECORDING_FORMAT_MPEG4:
								full_path += ".mp4" + (corrupting ? "_" : "");
								break;
						case RECORDING_FORMAT_3GP:
								full_path += ".3gp" + (corrupting ? "_" : "");
								break;
						case RECORDING_FORMAT_WAV:
								full_path += ".wav" + (corrupting ? "_" : "");
								break;
				}
		}
		this.path = full_path;
	}

	public void setFilesFolderName(String new_folder_path) {
		mFilesFolderName = new_folder_path;
		if (!mFilesFolderName.startsWith("/")) {
				mFilesFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mFilesFolderName;
		}
	}

	public String getFilesFolderName() {
		return mFilesFolderName;
	}

	public void setCurrentRecordId(long id) {
		current_record_id = id;
	}

	public long getCurrentRecordId() {
		return current_record_id;
	}

	public static long getCurrentRecordIdSt() {
		if(mInstance == null) return 0;
		return mInstance.getCurrentRecordId();
	}

	public boolean isRecording() {
		return mIsRecording;
	}

	public String getPath() {
		return path;
	}

	/**
	* Starts a new mIsRecording.
	*/
	public void start() throws IOException {
		mIsRecording = true;
		mLastStartTime = System.currentTimeMillis();
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new CardNotMountedException("SD Card is not mounted.  It is " + state	+ ".");
		}

		// make sure the directory we plan to store the records in exists
		File directory = new File(path).getParentFile();

		if (!directory.exists()) {
			directory.mkdirs();
			if (!directory.exists())
				throw new IOException("Path to file could not be created. " + directory.getAbsolutePath()+" "+AndroidUtils.getFreeSpaceOnSD()+" bytes free on SD");
		}

		// Создаем файл .nomedia, Если его не существует
		File nomedia = new File(directory.getAbsolutePath()+"/.nomedia");
		if(!nomedia.exists()) nomedia.createNewFile();
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.reset();
		//mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK | MediaRecorder.AudioSource.VOICE_UPLINK);

		mMediaRecorder.setAudioSource(this.mAudioSource);

		// Ограничение продолжительности записи для бесплатной версии
        /*
		if(App.isVersionFree()){
			try{
				mMediaRecorder.setMaxDuration(MAX_DURATION_LIMIT);
			}
			catch (Exception e){}
		}
		*/


		switch(this.mRecordingFormat){
				case RECORDING_FORMAT_MPEG4:
						mMediaRecorder.setAudioSamplingRate(44100);
						if(mBitRate != 0)
								mMediaRecorder.setAudioEncodingBitRate(mBitRate);
						mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
						mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
						Crash.put("recf", "MPEG4");
						break;
				case RECORDING_FORMAT_3GP:
						// older version of Android, use crappy sounding voice codec
						/*if(Build.VERSION.SDK_INT  >= 8){
								mMediaRecorder.setAudioSamplingRate(8000);
								mMediaRecorder.setAudioEncodingBitRate(12200);
						}*/
						mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						//mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
						mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
						Crash.put("recf", "3GP");
						break;
				case RECORDING_FORMAT_WAV:
						// Start recording
						mExtAudioRecorder = ExtAudioRecorder.getInstanse(false);
						mExtAudioRecorder.setOutputFile(path);
						mExtAudioRecorder.prepare();
						mExtAudioRecorder.start();
						return;
		}

						mMediaRecorder.setOutputFile(path);
		try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
		} catch (Exception e) {
				//Crash.put("path", path);
				//Crash.put("mAudioSource", ""+this.mAudioSource);
				//Crash.send(e);
		}
	}

	/**
	* Stops a mIsRecording that has been previously started.
	*/
	public void stop() throws IOException {
		mIsRecording = false;
		mLastStopTime = System.currentTimeMillis();
		switch(this.mRecordingFormat){
				case RECORDING_FORMAT_WAV:
						// Stop recording
						mExtAudioRecorder.stop();
						mExtAudioRecorder.release();
						break;
				case RECORDING_FORMAT_3GP:
				case RECORDING_FORMAT_MPEG4:
						try{
								mMediaRecorder.stop();
						}
						catch (Exception e){
								Log.e("dub", "record not started maybe", e);
						}
						mMediaRecorder.release();
						break;
		}
	}

	public long getLastDurationMillis() {
		if(mLastStartTime == 0 || mLastStopTime == 0) return 0;
		if(mLastStartTime >= mLastStopTime) return 0;
		return mLastStopTime - mLastStartTime;
	}

}
