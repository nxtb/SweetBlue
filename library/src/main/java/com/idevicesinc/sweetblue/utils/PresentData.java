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

package com.idevicesinc.sweetblue.utils;

/**
 * Simple dummy implementation of {@link FutureData} that just returns whatever is passed into the constructor.
 */
public final class PresentData implements FutureData
{
	private final byte[] m_data;

	/**
	 * The data sent to this constructor will simply be returned by {@link #getData()}.
	 */
	public PresentData(final byte[] data)
	{
		m_data = data;
	}

	/**
	 * Returns the data sent into the constructor {@link #PresentData(byte[])}
	 */
	@Override public byte[] getData()
	{
		return m_data;
	}
}
