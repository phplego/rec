package ru.phplego.core.db;

/**
* Created with IntelliJ IDEA by Oleg Dubrov
* User: Oleg
* Date: 06.06.12
* Time: 2:47
*/
public class ActiveField implements Comparable <ActiveField> {
	private int mPosition;

	private String mName;

	private String mType;

	private String mExtra;

	private boolean mIsIndex;

	private int mVersion;

	public ActiveField(int position, String name, String type, String extra, boolean isIndex, int version) {
		mPosition = position;
		mName = name;
		mType = type;
		mExtra = extra;
		mIsIndex = isIndex;
		mVersion = version;
	}

	public ActiveField(int position, String type, String extra, boolean isIndex, int version) {
		this(position, "", type, extra, isIndex, version);
	}

	public ActiveField(int position, String type, boolean isIndex, int version) {
		this(position, "", type, null, isIndex, version);
	}

	public String getName() {
		return mName;
	}

	void setName(String name) {
		mName = name;
	}

	public String getType() {
		return mType;
	}

	public String getExtra() {
		return mExtra;
	}

	public boolean isIndex() {
		return mIsIndex;
	}

	public int getVersion() {
		return mVersion;
	}

	public String toString() {
		return getName();
	}

	public int compareTo(ActiveField activeField) {
		// Для сортировки по позиции полей в таблице
		return this.mPosition - activeField.mPosition;
	}

}
