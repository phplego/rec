package ru.phplego.secretary.view.listeners;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
	private int mVisibleThreshold =5;

	private int mCurrentPage =0;

	private int mPreviousTotal =0;

	private boolean mLoading =true;

	public EndlessScrollListener() {
	}

	public EndlessScrollListener(int visibleThreshold) {
		this.mVisibleThreshold = visibleThreshold;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mLoading) {
				if (totalItemCount > mPreviousTotal) {
						mLoading = false;
						mPreviousTotal = totalItemCount;
						mCurrentPage++;
				}
		}
		if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + mVisibleThreshold)) {
				// I load the next page of gigs using a background task,
				// but you can call any function here.
				onScrollToEnd(mCurrentPage + 1);
				mLoading = true;
		}
	}

	protected abstract void onScrollToEnd(int page);

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

}
