package ru.phplego.secretary.view.sortablelist;

/**
* Implement to handle removing items.
* An adapter handling the underlying data
* will most likely handle this interface.
*
* @author Eric Harlow
*/

public interface RemoveListener {
	/**
	* Called when an item is to be removed
	* @param which - indicates which item to remove.
	*/
	public void onRemove(int which);

}