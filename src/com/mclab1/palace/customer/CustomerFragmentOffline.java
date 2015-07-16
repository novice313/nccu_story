package com.mclab1.palace.customer;

import java.util.ArrayList;

import edu.mclab1.nccu_story.R;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class CustomerFragmentOffline extends Fragment {
	private SampleAdapter mAdapter;
	private StaggeredGridView mGridView;
	public static Boolean flag=false;
	public static final String tag = "CustomerFragmentOffline";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.customer_fragment_offline, container,
				false);
	}

	private void gen_test_data() {
		flag=false;
		System.out.println("flag"+flag);
		ArrayList<CustomerItem> testDStrings = new ArrayList<CustomerItem>();
		testDStrings.add(new CustomerItem());
		testDStrings.add(new CustomerItem());
		testDStrings.add(new CustomerItem());
		testDStrings.add(new CustomerItem());
		testDStrings.add(new CustomerItem());
		mAdapter.addAll(testDStrings);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				mAdapter);
		swingBottomInAnimationAdapter.setAbsListView(mGridView);

		swingBottomInAnimationAdapter.setInitialDelayMillis(1);

		mGridView.setAdapter(swingBottomInAnimationAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.i(tag, "Clicket" + String.valueOf(arg2));
				CustomerItem customerItem = mAdapter.getItem(arg2);
				Intent intent = new Intent(getActivity(),
						CustomerDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", customerItem.title);
				bundle.putString("content", customerItem.content);
				bundle.putString("photo", customerItem.image_path);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);

			}
		});
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mGridView = (StaggeredGridView) getView().findViewById(R.id.grid_view);
 
		if (mAdapter == null) {
			mAdapter = new SampleAdapter(getActivity(), R.id.txt_line1);
		}

		gen_test_data();

	}
}
