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

package com.jaeckel.amdroid.bus;

import android.os.Bundle;

public class SimpleBus
	extends AbstractBus<Bundle, String, SimpleBus.IntentMatchStrategy> {
	public static final String KEY="com.jaeckel.amdroid.bus.SimpleBus.KEY";
	private static final String TAG="SimpleBus";
	
	public SimpleBus() {
		super();
		
		setStrategy(new IntentMatchStrategy());
	}
	
	public Bundle createMessage(String key) {
		Bundle message=new Bundle();
		
		message.putString(KEY, key);
		
		return(message);
	}
	
	class IntentMatchStrategy
		implements AbstractBus.Strategy<Bundle, String> {
		public boolean isMatch(Bundle message, String filter) {
			return(filter!=null && message!=null &&
						 filter.equals(message.getString(KEY)));
		}
	}
}