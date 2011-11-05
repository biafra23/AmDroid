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

package com.jaeckel.amenoid.cwac.bus;

import com.jaeckel.amenoid.cwac.task.AsyncTaskEx;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

abstract public class AbstractBus<M,F,S extends AbstractBus.Strategy> {
	private S strategy=null;
	private CopyOnWriteArrayList<Registration> regs=
										new CopyOnWriteArrayList<Registration>();
	
	protected void setStrategy(S strategy) {
		this.strategy=strategy;
	}
	
	public void send(M... messages) {
		sendActual(messages);
	}
	
	public void sendAsync(M... messages) {
		new SendMessageTask().execute(messages);
	}
	
	public void register(F filter, Receiver receiver) {
		register(filter, receiver, null);
	}
	
	public void register(F filter, Receiver receiver,
											 String tag) {
		regs.add(new Registration(filter, receiver, tag));
	}
	
	public void unregister(Receiver receiver) {
		unregister(receiver, null);
	}
	
	public void unregister(Receiver receiver,
												 BlockingQueue<SoftReference<M>> q) {
		for (Registration r : regs) {
			if (r.receiver==receiver) {
				synchronized(r) {
					if (q==null) {
						regs.remove(r);
					}
					else {
						r.setQueue(q);
					}
				}
			}
		}
	}
	
	public void unregisterByTag(String tag,
															BlockingQueue<SoftReference<M>> q) {
		if (tag==null) return;
		
		for (Registration r : regs) {
			if (tag.equals(r.tag)) {
				synchronized(r) {
					if (q==null) {
						regs.remove(r);
					}
					else {
						r.setQueue(q);
					}
				}
			}
		}
	}
	
	private void sendActual(M... messages) {
		for (M message : messages) {
			for (Registration r : regs) {
				synchronized(r) {
					r.tryToSend(message);
				}
			}
		}
	}
	
	public interface Receiver<M> {
		void onReceive(M message);
	}
	
	public interface Strategy<M,F> {
		boolean isMatch(M message, F filter);
	}
	
	private class Registration<M, F> {
		F filter=null;
		BlockingQueue<SoftReference<M>> q=null;
		Receiver receiver=null;
		String tag=null;
							
		Registration(F filter, Receiver receiver,
								 String tag) {
			this.filter=filter;
			this.receiver=receiver;
			this.tag=tag;
		}
		
		void setQueue(BlockingQueue q) {
			this.q=q;
			this.receiver=null;
		}
		
		void clearQueue() {
			this.q=null;
		}
		
		void tryToSend(M message) {
			if (strategy.isMatch(message, filter)) {
				if (receiver==null) {
					q.add(new SoftReference<M>(message));
				}
				else {
					receiver.onReceive(message);
				}
			}
		}
		
		void drainQueueAndSetReceiver(Receiver receiver) {
			this.receiver=receiver;
			
			ArrayList<SoftReference<M>> drain=
											new ArrayList<SoftReference<M>>();
			
			q.drainTo(drain);
			
			for (SoftReference<M> ref : drain) {
				M message=ref.get();
				
				if (message!=null) {
					receiver.onReceive(message);
				}
			}
		}
	}
	
	private class SendMessageTask extends AsyncTaskEx<M, Void, Void> {
		@Override
		protected Void doInBackground(M... messages) {
			sendActual(messages);
			
			return(null);
		}
	}
}