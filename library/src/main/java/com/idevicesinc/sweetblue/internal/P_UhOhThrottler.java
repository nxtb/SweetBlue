/*
 
  Copyright 2022 Hubbell Incorporated
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
 
  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
 */

package com.idevicesinc.sweetblue.internal;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.P_Bridge_User;
import com.idevicesinc.sweetblue.UhOhListener;
import com.idevicesinc.sweetblue.UhOhListener.UhOhEvent;
import com.idevicesinc.sweetblue.UhOhListener.UhOh;



final class P_UhOhThrottler
{

	private final static String LAST_UHOH_NAMESPACE = "sweetblue_f#$9_=hdSA";
	private final static String TIME_TRACKER_KEY = "lastTimeTrackerValue";
	private final static String LAST_TIME = "lastTimeSaved";

	private final HashMap<UhOh, Double> m_lastTimesCalled = new HashMap<>();
	private UhOhListener m_uhOhListener;
	private final double m_throttle;
	private final IBleManager m_mngr;
	private double m_timeTracker = 0.0;

	
	public P_UhOhThrottler(IBleManager mngr, double throttle)
	{
		m_mngr = mngr;
		m_throttle = throttle;
		if (mngr.getConfigClone().manageLastUhOhOnDisk)
		{
			loadLastUhOhs();
		}
	}

	public final synchronized void setListener(UhOhListener listener)
	{
		m_uhOhListener = listener;
	}
	
	final void uhOh(UhOh reason)
	{
		uhOh(reason, m_throttle);
	}
	
	final synchronized void uhOh(UhOh reason, double throttle)
	{
		m_mngr.getLogger().w(reason+"");
		
		if( throttle > 0.0 )
		{
			Double lastTimeCalled = m_lastTimesCalled.get(reason);
			
			if( lastTimeCalled != null )
			{
				if( m_timeTracker - lastTimeCalled < throttle )
				{
					return;
				}
			}
		}

		if (m_mngr.getConfigClone().manageLastUhOhOnDisk)
		{
			prefs().edit().putString(reason.toString(), String.valueOf(m_timeTracker))
					.putString(TIME_TRACKER_KEY, String.valueOf(m_timeTracker))
					.putString(LAST_TIME, String.valueOf(System.currentTimeMillis())).commit();
		}
		
		if( m_uhOhListener != null ) 
		{
			m_lastTimesCalled.put(reason, m_timeTracker);
			UhOhEvent event = P_Bridge_User.newUhOhEvent(BleManager.get(m_mngr.getApplicationContext()), reason);
			m_mngr.postEvent(m_uhOhListener, event);
		}
	}
	
	final void update(double timeStep)
	{
		m_timeTracker += timeStep;
	}

	final void shutdown()
	{
		if (m_mngr.getConfigClone().manageLastUhOhOnDisk)
		{
			prefs().edit().putString(TIME_TRACKER_KEY, String.valueOf(m_timeTracker))
					.putString(LAST_TIME, String.valueOf(System.currentTimeMillis())).commit();
		}
	}

	private void loadLastUhOhs()
	{
		final SharedPreferences prefs = prefs();

		long lastTime;

		if (prefs.contains(LAST_TIME))
		{
			// If the last time we saved any info is longer than the throttle time, then just clear all saved
			// info.
			lastTime = Long.parseLong(prefs.getString(LAST_TIME, "0"));
			if (lastTime + (m_throttle * 1000) < System.currentTimeMillis())
			{
				prefs.edit().clear().commit();
				return;
			}
		}

		if (prefs.contains(TIME_TRACKER_KEY))
		{
			m_timeTracker = Double.parseDouble(prefs.getString(TIME_TRACKER_KEY, "0.0"));
		}

		for (UhOh uhoh : UhOh.values())
		{
			if (prefs.contains(uhoh.toString()))
			{
				m_lastTimesCalled.put(uhoh, Double.parseDouble(prefs.getString(uhoh.toString(), "0.0")));
			}
		}
	}

	private SharedPreferences prefs()
	{
		final SharedPreferences prefs = m_mngr.getApplicationContext().getSharedPreferences(LAST_UHOH_NAMESPACE, Context.MODE_PRIVATE);

		return prefs;
	}

}
