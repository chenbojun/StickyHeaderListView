package com.sunfusheng.StickyHeaderListView.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunfusheng.StickyHeaderListView.AboutActivity;
import com.sunfusheng.StickyHeaderListView.R;
import com.sunfusheng.StickyHeaderListView.adapter.TravelingAdapter;
import com.sunfusheng.StickyHeaderListView.model.ChannelEntity;
import com.sunfusheng.StickyHeaderListView.model.FilterData;
import com.sunfusheng.StickyHeaderListView.model.FilterEntity;
import com.sunfusheng.StickyHeaderListView.model.FilterTwoEntity;
import com.sunfusheng.StickyHeaderListView.model.OperationEntity;
import com.sunfusheng.StickyHeaderListView.model.TravelingEntity;
import com.sunfusheng.StickyHeaderListView.util.ColorUtil;
import com.sunfusheng.StickyHeaderListView.util.DensityUtil;
import com.sunfusheng.StickyHeaderListView.util.ModelUtil;
import com.sunfusheng.StickyHeaderListView.util.StatusBarUtil;
import com.sunfusheng.StickyHeaderListView.view.FilterView;
import com.sunfusheng.StickyHeaderListView.view.HeaderBannerView;
import com.sunfusheng.StickyHeaderListView.view.HeaderChannelView;
import com.sunfusheng.StickyHeaderListView.view.HeaderDividerView;
import com.sunfusheng.StickyHeaderListView.view.HeaderFilterView;
import com.sunfusheng.StickyHeaderListView.view.HeaderOperationView;
import com.sunfusheng.StickyHeaderListView.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bojunchen on 2017/11/14.
 */

public class MainFragment extends Fragment implements SmoothListView.ISmoothListViewListener {

	@BindView(R.id.listView)
	SmoothListView smoothListView;
	@BindView(R.id.real_filterView)
	FilterView realFilterView;
	@BindView(R.id.rl_bar)
	RelativeLayout rlBar;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.view_title_bg)
	View viewTitleBg;
	@BindView(R.id.view_action_more_bg)
	View viewActionMoreBg;
	@BindView(R.id.fl_action_more)
	FrameLayout flActionMore;

	private Context mContext;
	private Activity mActivity;
	private int mScreenHeight; // 屏幕高度

	private List<String> bannerList = new ArrayList<>(); // 广告数据
	private List<ChannelEntity> channelList = new ArrayList<>(); // 频道数据
	private List<OperationEntity> operationList = new ArrayList<>(); // 运营数据
	private List<TravelingEntity> travelingList = new ArrayList<>(); // ListView数据

	private HeaderBannerView headerBannerView; // 广告视图
	private HeaderChannelView headerChannelView; // 频道视图
	private HeaderOperationView headerOperationView; // 运营视图
	private HeaderDividerView headerDividerView; // 分割线占位图
	private HeaderFilterView headerFilterView; // 分类筛选视图
	private FilterData filterData; // 筛选数据
	private TravelingAdapter mAdapter;

	private int titleViewHeight = 65; // 标题栏的高度

	private View itemHeaderBannerView; // 从ListView获取的广告子View
	private int bannerViewHeight = 180; // 广告视图的高度
	private int bannerViewTopMargin; // 广告视图距离顶部的距离

	private View itemHeaderFilterView; // 从ListView获取的筛选子View
	private int filterViewPosition = 4; // 筛选视图的位置
	private int filterViewTopMargin; // 筛选视图距离顶部的距离
	private boolean isScrollIdle = true; // ListView是否在滑动
	private boolean isStickyTop = false; // 是否吸附在顶部
	private boolean isSmooth = false; // 没有吸附的前提下，是否在滑动
	private int filterPosition = -1; // 点击FilterView的位置：分类(0)、排序(1)、筛选(2)

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null);
		ButterKnife.bind(this, rootView);
		StatusBarUtil.setStatusBarTranslucent(getActivity(), false);
		initData();
		initView();
		initListener();

		return rootView;
	}

	private void initData() {
		mContext = getContext();
		mActivity = getActivity();
		mScreenHeight = DensityUtil.getWindowHeight(getActivity());

		// 筛选数据
		filterData = new FilterData();
		filterData.setCategory(ModelUtil.getCategoryData());
		filterData.setSorts(ModelUtil.getSortData());
		filterData.setFilters(ModelUtil.getFilterData());
		// 广告数据
		bannerList = ModelUtil.getBannerData();
		// 频道数据
		channelList = ModelUtil.getChannelData();
		// 运营数据
		operationList = ModelUtil.getOperationData();
		// ListView数据
		travelingList = ModelUtil.getTravelingData();
	}

	private void initView() {
		// 设置广告数据
		headerBannerView = new HeaderBannerView(getActivity());
		headerBannerView.fillView(bannerList, smoothListView);

		// 设置频道数据
		headerChannelView = new HeaderChannelView(getActivity());
		headerChannelView.fillView(channelList, smoothListView);

		// 设置运营数据
		headerOperationView = new HeaderOperationView(getActivity());
		headerOperationView.fillView(operationList, smoothListView);

		// 设置分割线
		headerDividerView = new HeaderDividerView(getActivity());
		headerDividerView.fillView("", smoothListView);

		// 设置假FilterView数据
		headerFilterView = new HeaderFilterView(getActivity());
		headerFilterView.fillView(new Object(), smoothListView);

		// 设置真FilterView数据
		realFilterView.setFilterData(mActivity, filterData);
		realFilterView.setVisibility(View.GONE);

		// 设置ListView数据
		mAdapter = new TravelingAdapter(getContext(), travelingList);
		smoothListView.setAdapter(mAdapter);

		filterViewPosition = smoothListView.getHeaderViewsCount() - 1;
	}

	private void initListener() {
		// 关于
		flActionMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mActivity, AboutActivity.class));
			}
		});

		// (假的ListView头部展示的)筛选视图点击
		headerFilterView.getFilterView().setOnFilterClickListener(new FilterView.OnFilterClickListener() {
			@Override
			public void onFilterClick(int position) {
				filterPosition = position;
				isSmooth = true;
				smoothListView.smoothScrollToPositionFromTop(filterViewPosition, DensityUtil.dip2px(mContext, titleViewHeight));
			}
		});

		// (真正的)筛选视图点击
		realFilterView.setOnFilterClickListener(new FilterView.OnFilterClickListener() {
			@Override
			public void onFilterClick(int position) {
				filterPosition = position;
				realFilterView.show(position);
				smoothListView.smoothScrollToPositionFromTop(filterViewPosition, DensityUtil.dip2px(mContext, titleViewHeight));
			}
		});

		// 分类Item点击
		realFilterView.setOnItemCategoryClickListener(new FilterView.OnItemCategoryClickListener() {
			@Override
			public void onItemCategoryClick(FilterTwoEntity leftEntity, FilterEntity rightEntity) {
				fillAdapter(ModelUtil.getCategoryTravelingData(leftEntity, rightEntity));
			}
		});

		// 排序Item点击
		realFilterView.setOnItemSortClickListener(new FilterView.OnItemSortClickListener() {
			@Override
			public void onItemSortClick(FilterEntity entity) {
				fillAdapter(ModelUtil.getSortTravelingData(entity));
			}
		});

		// 筛选Item点击
		realFilterView.setOnItemFilterClickListener(new FilterView.OnItemFilterClickListener() {
			@Override
			public void onItemFilterClick(FilterEntity entity) {
				fillAdapter(ModelUtil.getFilterTravelingData(entity));
			}
		});

		smoothListView.setRefreshEnable(true);
		smoothListView.setLoadMoreEnable(true);
		smoothListView.setSmoothListViewListener(this);
		smoothListView.setOnScrollListener(new SmoothListView.OnSmoothScrollListener() {
			@Override
			public void onSmoothScrolling(View view) {
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				isScrollIdle = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (isScrollIdle && bannerViewTopMargin < 0) return;

				// 获取广告头部View、自身的高度、距离顶部的高度
				if (itemHeaderBannerView == null) {
					itemHeaderBannerView = smoothListView.getChildAt(1);
				}
				if (itemHeaderBannerView != null) {
					bannerViewTopMargin = DensityUtil.px2dip(mContext, itemHeaderBannerView.getTop());
					bannerViewHeight = DensityUtil.px2dip(mContext, itemHeaderBannerView.getHeight());
				}

				// 获取筛选View、距离顶部的高度
				if (itemHeaderFilterView == null) {
					itemHeaderFilterView = smoothListView.getChildAt(filterViewPosition - firstVisibleItem);
				}
				if (itemHeaderFilterView != null) {
					filterViewTopMargin = DensityUtil.px2dip(mContext, itemHeaderFilterView.getTop());
				}

				// 处理筛选是否吸附在顶部
				if (filterViewTopMargin <= titleViewHeight || firstVisibleItem > filterViewPosition) {
					isStickyTop = true; // 吸附在顶部
					realFilterView.setVisibility(View.VISIBLE);
				} else {
					isStickyTop = false; // 没有吸附在顶部
					realFilterView.setVisibility(View.GONE);
				}

				if (isSmooth && isStickyTop) {
					isSmooth = false;
					realFilterView.show(filterPosition);
				}

				// 处理标题栏颜色渐变
				handleTitleBarColorEvaluate();
			}
		});
	}

	// 填充数据
	private void fillAdapter(List<TravelingEntity> list) {
		if (list == null || list.size() == 0) {
			int height = mScreenHeight - DensityUtil.dip2px(mContext, 95); // 95 = 标题栏高度 ＋ FilterView的高度
			mAdapter.setData(ModelUtil.getNoDataEntity(height));
		} else {
			mAdapter.setData(list);
		}
	}

	// 处理标题栏颜色渐变
	private void handleTitleBarColorEvaluate() {
		float fraction;
		if (bannerViewTopMargin > 0) {
			fraction = 1f - bannerViewTopMargin * 1f / 60;
			if (fraction < 0f) fraction = 0f;
			rlBar.setAlpha(fraction);
			return;
		}

		float space = Math.abs(bannerViewTopMargin) * 1f;
		fraction = space / (bannerViewHeight - titleViewHeight);
		if (fraction < 0f) fraction = 0f;
		if (fraction > 1f) fraction = 1f;
		rlBar.setAlpha(1f);

		if (fraction >= 1f || isStickyTop) {
			isStickyTop = true;
			viewTitleBg.setAlpha(0f);
			viewActionMoreBg.setAlpha(0f);
			rlBar.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
		} else {
			viewTitleBg.setAlpha(1f - fraction);
			viewActionMoreBg.setAlpha(1f - fraction);
			rlBar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.colorPrimary));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		headerBannerView.enqueueBannerLoopMessage();
	}

	@Override
	public void onStop() {
		super.onStop();
		headerBannerView.removeBannerLoopMessage();
	}


	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				smoothListView.stopRefresh();
				smoothListView.setRefreshTime("刚刚");
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				smoothListView.stopLoadMore();
			}
		}, 2000);
	}

	public boolean onBackPressed() {
		if (realFilterView.isShowing()) {
			realFilterView.resetAllStatus();
			return true;
		} else {
			return false;
		}
	}
}
