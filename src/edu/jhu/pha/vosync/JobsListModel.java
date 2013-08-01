/*******************************************************************************
 * Copyright 2013 Johns Hopkins University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package edu.jhu.pha.vosync;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class JobsListModel extends LinkedHashMap<NodePath, TransferJob> implements ListModel<TransferJob> {

	private static final long serialVersionUID = -7565960193119391058L;

	protected EventListenerList listenerList = new EventListenerList();
	
	@Override
	public int getSize() {
		return this.size();
	}

	@Override
	public TransferJob getElementAt(int index) {
		return (new ArrayList<TransferJob>(this.values())).get(index);		
	}
	
	public void addJob(TransferJob job) {
		synchronized(this) {
			this.put(job.getPath(), job);
			Object[] listeners = listenerList.getListenerList();
			ListDataEvent e = null;

			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == ListDataListener.class) {
					if (e == null) {
						e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, this.size()-1, this.size()-1);
					}
					((ListDataListener)listeners[i+1]).contentsChanged(e);
				}
			}
			this.notifyAll();
		}
	}
	
	public TransferJob popJob() {
		TransferJob job = null;
		synchronized(this) {
			NodePath curJobPath = keySet().iterator().next();
			job = remove(curJobPath);
		}

		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, 0);
				}
				((ListDataListener)listeners[i+1]).contentsChanged(e);
			}
		}
		return job;
	}
	
	public void refresh() {
		synchronized(this) {
			Object[] listeners = listenerList.getListenerList();
			ListDataEvent e = null;
	
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == ListDataListener.class) {
					if (e == null) {
						e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.size()-1);
					}
					((ListDataListener)listeners[i+1]).contentsChanged(e);
				}
			}
		}
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

}
