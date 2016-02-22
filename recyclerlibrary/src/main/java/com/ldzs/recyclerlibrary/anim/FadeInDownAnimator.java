package com.ldzs.recyclerlibrary.anim;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

public class FadeInDownAnimator extends BaseItemAnimator {
    private int mDelayTime;

    public FadeInDownAnimator() {
    }

    public FadeInDownAnimator(int delayTime) {
        this.mDelayTime = delayTime;
    }

    public FadeInDownAnimator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder, int index) {
        ViewCompat.animate(holder.itemView)
                .translationY(-holder.itemView.getHeight())
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setStartDelay(index * mDelayTime)
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationY(holder.itemView, -holder.itemView.getHeight());
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder, int index) {
        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setStartDelay(index * mDelayTime)
                .setListener(new DefaultAddVpaListener(holder))
                .start();
    }
}