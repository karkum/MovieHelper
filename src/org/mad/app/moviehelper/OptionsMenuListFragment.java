package org.mad.app.moviehelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * List shown in the sliding menu
 * @author karthik
 *
 */
public class OptionsMenuListFragment extends ListFragment {

	private Context cnxt;
	
	public OptionsMenuListFragment(Context c) {
		cnxt = c;
	}
	public OptionsMenuListFragment() {
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}
	
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Intent i;
		switch (position) {
		case 0:
			i = new Intent(cnxt, RandomMovieActivity.class);
			startActivity(i);
			break;
		case 1:
			i = new Intent(cnxt, FindByGenreActivity.class);
			startActivity(i);
			break;
		case 4:
			i = new Intent(cnxt, SearchMovieActivity.class);
			startActivity(i);
			break;
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		adapter.add(new SampleItem("Find a random movie", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Find movie by genre", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("In Theaters", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Coming Soon", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Search Movie", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("Favorites", android.R.drawable.ic_menu_search));
		setListAdapter(adapter);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
}