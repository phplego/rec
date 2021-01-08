package ru.phplego.secretary.view.sortablelist;

import android.view.View;
import android.widget.ListView;

/**
* Implement to handle an item being dragged.
*
* @author Eric Harlow
*/

public interface DragListener {
	/**
	* Called when a drag starts.
	* @param itemView - the view of the item to be dragged i.e. the drag view
	*/
	public void onStartDrag(View itemView, int from);

	/**
	* Called when a drag is to be performed.
	* @param x - horizontal coordinate of MotionEvent.
	* @param y - verital coordinate of MotionEvent.
	* @param listView - the listView
	*/
	public void onDrag(int x, int y, ListView listView, int from, int to);

	/**
	* Called when a drag stops.
	* Any changes in onStartDrag need to be undone here
	* so that the view can be used in the list again.
	* @param itemView - the view of the item to be dragged i.e. the drag view
	*/
	public void onStopDrag(View itemView);

}
