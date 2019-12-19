package ir.smartdevelop.eram.showcaseview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;

public class MainActivity extends AppCompatActivity {

	View view1;
	View view2;
	View view3;
	View view4;
	View view5;
	private GuideView mGuideView;
	private GuideView.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		view1 = findViewById(R.id.view1);
		view2 = findViewById(R.id.view2);
		view3 = findViewById(R.id.view3);
		view4 = findViewById(R.id.view4);
		view5 = findViewById(R.id.view5);

//		builder = new GuideView.Builder(this)
//				.setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
//				.setGravity(Gravity.TOP_START)
//				.setDismissType(DismissType.TARGET_VIEW)
//				.setTargetView(view1)
//				.setGuideListener(new GuideListener() {
//					@Override
//					public void onDismiss(View view) {
//						switch (view.getId()) {
//							case R.id.view1:
//								builder.setTargetView(view2).build();
//								break;
//							case R.id.view2:
//								builder.setTargetView(view3).build();
//								break;
//							case R.id.view3:
//								builder.setTargetView(view4).build();
//								break;
//							case R.id.view4:
//								builder.setTargetView(view5).build();
//								break;
//							case R.id.view5:
//								return;
//						}
//						mGuideView = builder.build();
//						mGuideView.show();
//					}
//				});
//
//		mGuideView = builder.build();
//		mGuideView.show();

//		updatingForDynamicLocationViews();
		view3.setOnClickListener(new View.OnClickListener() {
			int i = 0;

			@Override
			public void onClick(View v) {
				builder = new GuideView.Builder(MainActivity.this)
						.setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
						.setGravity(Gravity.values()[i])
						.setTargetView(view3)
				;

				mGuideView = builder.build();
				mGuideView.show();
				i++;
				if (i >= Gravity.values().length) {
					i = 0;
				}
			}
		});
	}

	private void updatingForDynamicLocationViews() {
		view4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				mGuideView.updateGuideViewLocation();
			}
		});
	}

}
