package com.ldzs.recyclerlibrary.anim;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

public class SlideInLeftAnimator extends BaseItemAnimator {
    private int mDelayTime;

    public SlideInLeftAnimator() {

    }

    public SlideInLeftAnimator(int elayTime) {
        this.mDelayTime = elayTime;
    }

    public SlideInLeftAnimator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder, int index) {
        ViewCompat.animate(holder.itemView)
                .translationX(-holder.itemView.getRootView().getWidth())
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setStartDelay(index * mDelayTime)
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getRootView().getWidth());
    }

    @Override
    protected void animateAddImpl(final RecyclerView.ViewHolder holder, int index) {
        ViewCompat.animate(holder.itemView)
                .translationX(0)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setStartDelay(index * mDelayTime)
                .setListener(new DefaultAddVpaListener(holder))
                .start();
    }
}