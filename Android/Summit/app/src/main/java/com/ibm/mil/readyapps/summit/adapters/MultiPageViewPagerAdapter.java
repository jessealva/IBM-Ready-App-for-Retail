/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.readyapps.summit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.ibm.mil.readyapps.summit.fragments.ItemsPageFragment;
import com.ibm.mil.readyapps.summit.models.ItemMetaData;

import java.util.LinkedList;
import java.util.List;

/**
 * A subclass of the {@link android.support.v4.app.FragmentStatePagerAdapter} that allows a
 * standard {@link android.support.v4.view.ViewPager} to display two pages at once.
 * <p/>
 * Use this adapter with a standard {@link android.support.v4.view.ViewPager}.
 *
 * @author Jonathan Ballands
 */
public class MultiPageViewPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * A list of fragments generated by this adapter.
     */
    private List<Fragment> mPagerFragments;

    /**
     * The current page being displayed by the pager.
     */
    private int currentPage;

    /**
     * Constructs a new {@link com.ibm.mil.readyapps.summit.adapters.InfiniteViewPagerAdapter}.
     *
     * @param fm             The fragment manager for the app.
     * @param pagerResources A linked list of resources that this adapter should load into fragments.
     *                       A new linked list called {@link #mPagerFragments mPagerFragments} will
     *                       be generated.
     */
    public MultiPageViewPagerAdapter(FragmentManager fm, List<ItemMetaData> pagerResources) {
        super(fm);

        this.currentPage = -1;
        this.mPagerFragments = new LinkedList<>();

        // Put the pages in
        for (int i = 0; i < pagerResources.size(); i++) {
            String json = new Gson().toJson(pagerResources.get(i));
            mPagerFragments.add(ItemsPageFragment.newInstance(json, true));
        }

        this.currentPage = 0;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.5f;
    }

    @Override
    public Fragment getItem(int position) {
        if (this.mPagerFragments == null || position > this.mPagerFragments.size() || position < 0) {
            return null;
        }

        return this.mPagerFragments.get(position);
    }

    @Override
    public int getCount() {
        if (this.mPagerFragments == null) {
            return 0;
        }
        return this.mPagerFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * A getter method that returns the {@link #currentPage currentPage}.
     *
     * @return The current page this adapter thinks its associated pager is on.
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    /**
     * A setter method that sets the {@link #currentPage currentPage}.
     *
     * @param position The position you want this adapter to think its associated pager is on.
     */
    public void setCurrentPage(int position) {
        this.currentPage = position;
    }

}
