/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.jaeckel.amdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.jaeckel.amdroid.cwac.endless.EndlessAdapter;
import java.util.ArrayList;

public class EndlessAdapterExceptionDemo extends ListActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.endless_main);
		
		ArrayList<Integer> items=new ArrayList<Integer>();
		
		for (int i=0;i<25;i++) { items.add(i); }
		
		setListAdapter(new DemoAdapter(items));
	}
	
	class DemoAdapter extends EndlessAdapter {
		DemoAdapter(ArrayList<Integer> list) {
			super(EndlessAdapterExceptionDemo.this,
						new SpecialAdapter(list), R.layout.pending);
		}
		
		@Override
		protected boolean cacheInBackground() throws Exception {
			SystemClock.sleep(5000);				// pretend to do work

      //ends at 74
			if (getWrappedAdapter().getCount()<75) {
				return(true);
			}
			
			throw new Exception("Gadzooks!");
		}
		
		@Override
		protected void appendCachedData() {
			if (getWrappedAdapter().getCount()<75) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Integer> a=(ArrayAdapter<Integer>)getWrappedAdapter();
				
				for (int i=0;i<25;i++) { a.add(a.getCount()); }
			}
		}
	}
	
	class SpecialAdapter extends ArrayAdapter<Integer> {
		SpecialAdapter(ArrayList<Integer> items) {
			super(EndlessAdapterExceptionDemo.this, R.layout.row,
							android.R.id.text1, items);
		}
		
		@Override
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=super.getView(position, convertView, parent);
			
			// further customize your rows here
			
			return(row);
		}
	}
}
