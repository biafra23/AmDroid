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

import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;

public class IntentBus
	extends AbstractBus<Intent, IntentFilter, IntentBus.IntentMatchStrategy> {
	private static final String TAG="IntentBus";
	
	public IntentBus() {
		super();
		
		setStrategy(new IntentMatchStrategy());
	}
	
	static public class DirectIntentFilter extends IntentFilter {
		public DirectIntentFilter(Intent i)
			throws IntentFilter.MalformedMimeTypeException {
			super(i.getAction());
			
			addDataType(i.getType());
			addDataScheme(i.getData().getScheme());
			addDataAuthority(i.getData().getHost(),
											 String.valueOf(i.getData().getPort()));
			addDataPath(i.getData().getPath(),
									PatternMatcher.PATTERN_LITERAL);
		}
	}
	
	class IntentMatchStrategy
		implements AbstractBus.Strategy<Intent, IntentFilter> {
		public boolean isMatch(Intent message, IntentFilter filter) {
			return(filter.match(null, message, false, TAG)>=0);
		}
	}
}