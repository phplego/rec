package ru.phplego.secretary.view.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 29.04.12
* Time: 3:56
* To change this template use File | Settings | File Templates.
*/

public class DropDownAnim extends Animation {
	int targetHeight;

	View view;

	boolean down;

	public DropDownAnim(View view, int targetHeight, boolean down) {
		this.view = view;
		this.targetHeight = targetHeight;
		this.down = down;
	}

	public DropDownAnim start(int duration) {
		this.setDuration(duration);
		this.view.startAnimation(this);
		//setInterpolator(new LinearInterpolator());
		return this;
	}

	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int newHeight;
		if (down) {
				newHeight = (int) (targetHeight * interpolatedTime);
		} else {
				newHeight = (int) (targetHeight * (1 - interpolatedTime));
		}
		if(newHeight == 0){
				newHeight = 1;
		};
		view.getLayoutParams().height = newHeight;
		view.requestLayout();
	}

	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	public boolean willChangeBounds() {
		return true;
	}

}
