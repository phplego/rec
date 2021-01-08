package ru.phplego.secretary.db;

import android.content.Context;
import ru.phplego.core.db.ActiveRecord;
import ru.phplego.core.AndroidUtils;
import ru.phplego.core.EManager;
import ru.phplego.secretary.App;
import ru.phplego.rec_module.R;
import java.io.File;
import java.util.Date;

/**
* Created by IntelliJ IDEA.
* User: Admin
* Date: 15.01.12
* Time: 0:50
* To change this template use File | Settings | File Templates.
*/

public class ActiveCall extends ActiveRecord {
	public static enum FMT {
		UNKMOWN,

		GP3,

		MP4,

		WAV;

	}
	public static class InsertEvent extends EManager.Event {
	}
	public static class DeleteEvent extends EManager.Event {
	}
	public static class AddToFavouritesEvent extends EManager.Event {
	}
	public static class RemoveFromFavouritesEvent extends EManager.Event {
	}
	public static class PlannedEvent extends EManager.Event {
	}
	// Событие вставки

	// Событие удаления

	// Событие добавления в избранное

	// Событие добавления в избранное
	public String getTableName() {
		return TableCalls.name;
	}

	public ActiveCall() {
		super();
	}

	public long insert() {
		long id = super.insert();
		App.getEManager().riseEvent(new InsertEvent(), null);
		return id;
	}

	public void delete(boolean rise_event) {
		super.delete();
		if(rise_event) App.getEManager().riseEvent(new DeleteEvent(), null);
		if(fileExists()){
				File f = new File(getFilename());
				f.delete();
		}
	}

	public void delete() {
		delete(true);
	}


    private String mContactDisplayName = null;

    public String getContactDisplayName(Context context) {
        if(mContactDisplayName != null) return mContactDisplayName;
        mContactDisplayName = AndroidUtils.getContactNameFromNumber(context, getPhone());
        return mContactDisplayName;
    }

	public String getPhone() {
		return get(TableCalls.phone);
	}

	public boolean isIncoming() {
		return getBool(TableCalls.incoming);
	}

	public boolean isFavourite() {
		return getBool(TableCalls.favourite);
	}

	public void addToFavourites() {
		this.set(TableCalls.favourite, "1");
		this.update();
		App.getEManager().riseEvent(new AddToFavouritesEvent(), null);
	}

	public void removeFromFavourites() {
		this.set(TableCalls.favourite, "0");
		this.update();
		App.getEManager().riseEvent(new RemoveFromFavouritesEvent(), null);
	}

	public String getNote() {
		return get(TableCalls.note);
	}

	public String getFilename() {
		return get(TableCalls.filename);
	}

	public int getDuration() {
		return getInt(TableCalls.duration);
	}

	public void setDuration(long duration) {
		set(TableCalls.duration, duration);
	}

	public String getFilenameSmart() {
		String filename = getFilename();
		if(filename.endsWith("_"))
				filename = filename.substring(0, filename.length()-1);
		return filename;
	}

	public boolean fileExists() {
		File f = new File(getFilename());
		return f.exists();
	}

	public File getFile() {
		return new File(getFilename());
	}

	public void setNote(String s) {
		set(TableCalls.note, s.trim());
	}

	public void setAlerted(boolean value) {
		set(TableCalls.alerted, value ? "1" : "0");
	}

	public long getCreatedTime() {
		return getLong(TableCalls.created);
	}

	public Date getCreatedDate() {
		return new Date(getCreatedTime());
	}

	public boolean isPlanned() {
		return getPlannedTime() > 0;
	}

	public long getPlannedTime() {
		return getLong(TableCalls.planned);
	}

	public Date getPlannedDate() {
		return new Date(getPlannedTime());
	}

	public void setPlannedTime(long time) {
		set(TableCalls.planned, "" + time);
		update();
		App.getEManager().riseEvent(new PlannedEvent(), null);
	}

	public void removeFromPlan() {
		setPlannedTime(0);
		update();
		App.getEManager().riseEvent(new PlannedEvent(), null);
	}

	public FMT getFormat() {
		String filename = getFilename();
		if(filename.endsWith("_")) // Удаляем окончние _ (скрытый от медапроигрывателя) , если оно есть
				filename = filename.substring(0, filename.length()-1);
		if(filename.endsWith(".3gp")) return FMT.GP3;
		if(filename.endsWith(".mp4")) return FMT.MP4;
		if(filename.endsWith(".wav")) return FMT.WAV;
		return FMT.UNKMOWN;
	}

	public String getFileMimeType() {
		switch (this.getFormat()){
				case MP4: return "video/mp4";
				case GP3: return "video/3gpp";
				case WAV: return "audio/wav";
		}
		return "*";
	}

}
