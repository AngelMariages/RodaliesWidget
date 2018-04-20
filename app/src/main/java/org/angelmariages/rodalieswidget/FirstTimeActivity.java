/*
 * MIT License
 *
 * Copyright (c) 2018 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.angelmariages.rodalieswidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.angelmariages.rodalieswidget.utils.U;

public class FirstTimeActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private LinearLayout dotsLayout;
	private Button btnSkip;
	private Button btnNext;
	private int[] layouts;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		boolean fromNotification = intent != null && intent.getAction() != null && intent.getAction().equals("notification");

		if (!fromNotification && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tutorial_viewed", false)) {
			startSettings();
		}

		// Making notification bar transparent
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		setContentView(R.layout.first_time_activity);

		changeStatusBarColor();

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
		btnSkip = (Button) findViewById(R.id.skipButton);
		btnNext = (Button) findViewById(R.id.nextButton);

		layouts = new int[]{
				R.layout.tutorial_slide_1,
				R.layout.tutorial_slide_2,
				R.layout.tutorial_slide_3};

		addBottomDots(0);

		viewPager.setAdapter(new MyViewPagerAdapter());
		viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseReference tutorialReference = U.getFirebaseDatabase().getReference("tutorial");
				tutorialReference.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						DataSnapshot skip = dataSnapshot.child("skip");

						if (skip.exists()) {
							int value = Integer.parseInt(String.valueOf(skip.getValue()));
							skip.getRef().setValue(value + 1);
						} else {
							skip.getRef().setValue(1);
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						U.log("FirebaseError (skip): " + databaseError.getMessage());
					}
				});

				startSettings();
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current = getItem(+1);
				if (current < layouts.length) {
					viewPager.setCurrentItem(current);
				} else {
					startSettings();

					DatabaseReference tutorialReference = U.getFirebaseDatabase().getReference("tutorial");
					tutorialReference.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							DataSnapshot skip = dataSnapshot.child("start");

							if (skip.exists()) {
								int value = Integer.parseInt(String.valueOf(skip.getValue()));
								skip.getRef().setValue(value + 1);
							} else {
								skip.getRef().setValue(1);
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							U.log("FirebaseError (skip): " + databaseError.getMessage());
						}
					});
				}
			}
		});
	}

	private void startSettings() {
		startActivity(new Intent(FirstTimeActivity.this, MainActivity.class));

		PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("tutorial_viewed", true).apply();

		finish();
	}

	private class MyViewPagerAdapter extends PagerAdapter {
		private LayoutInflater layoutInflater;

		MyViewPagerAdapter() { }

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
		public void onPageSelected(final int position) {
			addBottomDots(position);

			if (position == layouts.length - 1) {
				btnNext.setText(getString(R.string.tutorial_start_button));
				btnSkip.setVisibility(View.INVISIBLE);
			} else {
				btnNext.setText(getString(R.string.tutorial_next_button));
				btnSkip.setVisibility(View.VISIBLE);
			}

			DatabaseReference tutorialReference = U.getFirebaseDatabase().getReference("tutorial");
			tutorialReference.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					DataSnapshot skip = dataSnapshot.child("tutorial_" + position);

					if (skip.exists()) {
						int value = Integer.parseInt(String.valueOf(skip.getValue()));
						skip.getRef().setValue(value + 1);
					} else {
						skip.getRef().setValue(1);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					U.log("FirebaseError (skip): " + databaseError.getMessage());
				}
			});
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) { }

		@Override
		public void onPageScrollStateChanged(int arg0) { }
	};

	private void addBottomDots(int currentPage) {
		TextView[] dots = new TextView[layouts.length];

		dotsLayout.removeAllViews();
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new TextView(this);
			dots[i].setText("\u2022");
			dots[i].setTextSize(36);
			dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
			dotsLayout.addView(dots[i]);
		}

		if (dots.length > 0)
			dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
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
