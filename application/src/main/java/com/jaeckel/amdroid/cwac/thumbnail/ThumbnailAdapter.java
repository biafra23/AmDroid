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

package com.jaeckel.amdroid.cwac.thumbnail;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import com.jaeckel.amdroid.cwac.adapter.AdapterWrapper;
import com.jaeckel.amdroid.cwac.cache.SimpleWebImageCache;

public class ThumbnailAdapter extends AdapterWrapper {
	private static final String TAG="ThumbnailAdapter";
	private int[] imageIds;
	private SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache=null;
	private Activity host=null;
	
	/**
		* Constructor wrapping a supplied ListAdapter
    */
	public ThumbnailAdapter(Activity host,
													ListAdapter wrapped,
													SimpleWebImageCache<ThumbnailBus, ThumbnailMessage> cache,
													int[] imageIds) {
		super(wrapped);
		
		this.host=host;
		this.imageIds=imageIds;
		this.cache=cache;
		
		cache.getBus().register(getBusKey(), onCache);
	}
	
	public void close() {
		cache.getBus().unregister(onCache);
	}

	/**
		* Get a View that displays the data at the specified
		* position in the data set. In this case, if we are at
		* the end of the list and we are still in append mode,
		* we ask for a pending view and return it, plus kick
		* off the background task to append more data to the
		* wrapped adapter.
		* @param position Position of the item whose data we want
		* @param convertView View to recycle, if not null
		* @param parent ViewGroup containing the returned View
    */
	@Override
	public View getView(int position, View convertView,
											ViewGroup parent) {
		View result=super.getView(position, convertView, parent);
		
		processView(result);
		
		return(result);
	}
	
	public void processView(View row) {
		for (int imageId : imageIds) {
			ImageView image=(ImageView)row.findViewById(imageId);
			
			if (image!=null && image.getTag()!=null) {
				ThumbnailMessage msg=cache
																.getBus()
																.createMessage(getBusKey());
																
				msg.setImageView(image);
				msg.setUrl(image.getTag().toString());
				
				try {
					cache.notify(msg.getUrl(), msg);
				}
				catch (Throwable t) {
					Log.e(TAG, "Exception trying to fetch image", t);
				}
			}
		}
	}
	
	private String getBusKey() {
		return(toString());
	}
	
	private ThumbnailBus.Receiver<ThumbnailMessage> onCache=
		new ThumbnailBus.Receiver<ThumbnailMessage>() {
		public void onReceive(final ThumbnailMessage message) {
			final ImageView image=message.getImageView();
			
			host.runOnUiThread(new Runnable() {
				public void run() {
					if (image.getTag()!=null &&
							image.getTag().toString().equals(message.getUrl())) {
						image.setImageDrawable(cache.get(message.getUrl()));
					}
				}
			});
		}
	};
}