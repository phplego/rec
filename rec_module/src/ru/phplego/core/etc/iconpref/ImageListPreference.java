package ru.phplego.core.etc.iconpref;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import ru.phplego.core.Application;
import ru.phplego.core.Res;
import ru.phplego.rec_module.R;

/**
* The ImageListPreference class responsible for displaying an image for each
* item within the list.
* @author Casper Wakkers
*/

public class ImageListPreference extends ListPreference {
	private int [] resourceIds =null;

	/**
	* Constructor of the ImageListPreference. Initializes the custom images.
	* @param context application context.
	* @param attrs custom xml attributes.
	*/
	public ImageListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
						R.styleable.ImageListPreference);

		String[] imageNames = context.getResources().getStringArray(
						typedArray.getResourceId(typedArray.getIndexCount()-1, -1));

		resourceIds = new int[imageNames.length];

		for (int i=0;i<imageNames.length;i++) {
						String imageName = imageNames[i].substring(
										imageNames[i].indexOf('/') + 1,
										imageNames[i].lastIndexOf('.'));

						resourceIds[i] = context.getResources().getIdentifier(imageName,
										null, context.getPackageName());
		}

		typedArray.recycle();
	}

	/**
	* {@inheritDoc}
	*/
	protected void onPrepareDialogBuilder(Builder builder) {
		int index = findIndexOfValue(getSharedPreferences().getString(getKey(), "1"));

		ListAdapter listAdapter = new ImageArrayAdapter(getContext(),
						R.layout.iconpref_listi_tem, getEntries(), resourceIds, index);

		// Order matters.
		builder.setAdapter(listAdapter, this);
		super.onPrepareDialogBuilder(builder);
	}

}
