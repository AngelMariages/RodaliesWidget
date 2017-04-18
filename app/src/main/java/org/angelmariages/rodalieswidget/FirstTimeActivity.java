package org.angelmariages.rodalieswidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FirstTimeActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private LinearLayout dotsLayout;
	private Button btnSkip;
	private Button btnNext;
	private int[] layouts;
	private TextView[] dots;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Making notification bar transparent
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		setContentView(R.layout.first_time_activity);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
		btnSkip = (Button) findViewById(R.id.btn_skip);
		btnNext = (Button) findViewById(R.id.btn_next);

		changeStatusBarColor();

		// layouts of all welcome sliders
		// add few more layouts if you want
		layouts = new int[]{
				R.layout.tutorial_slide_1,
				R.layout.tutorial_slide_1,
				R.layout.tutorial_slide_1};

		// adding bottom dots
		addBottomDots(0);

		viewPager.setAdapter(new MyViewPagerAdapter());
		viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//launchHomeScreen();
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// checking for last page
				// if last page home screen will be launched
				int current = getItem(+1);
				if (current < layouts.length) {
					// move to next screen
					viewPager.setCurrentItem(current);
				} else {
					startActivity(new Intent(FirstTimeActivity.this, SettingsActivity.class));
					//launchHomeScreen();
				}
			}
		});
	}

	/**
	 * View pager adapter
	 */
	public class MyViewPagerAdapter extends PagerAdapter {
		private LayoutInflater layoutInflater;

		public MyViewPagerAdapter() {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = layoutInflater.inflate(layouts[position], container, false);
			container.addView(view);

			return view;
		}

		@Override
		public int getCount() {
			return layouts.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}


		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}
	}

	//  viewpager change listener
	ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			addBottomDots(position);

			// changing the next button text 'NEXT' / 'GOT IT'
			if (position == layouts.length - 1) {
				// last page. make button text to GOT IT
				btnNext.setText("start");
				btnSkip.setVisibility(View.GONE);
			} else {
				// still pages are left
				btnNext.setText("next");
				btnSkip.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void addBottomDots(int currentPage) {
		dots = new TextView[layouts.length];

		int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
		int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

		dotsLayout.removeAllViews();
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new TextView(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT));
			} else {
				dots[i].setText(Html.fromHtml("&#8226;"));
			}
			dots[i].setTextSize(35);
			dots[i].setTextColor(colorsInactive[currentPage]);
			dotsLayout.addView(dots[i]);
		}

		if (dots.length > 0)
			dots[currentPage].setTextColor(colorsActive[currentPage]);
	}

	private int getItem(int i) {
		return viewPager.getCurrentItem() + i;
	}

	private void changeStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}
}
