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

package com.idevicesinc.sweetblue.defaults;

import com.idevicesinc.sweetblue.DeviceReconnectFilter;


/**
 * Convenience class to use if you do not want SweetBlue to do ANY reconnection attempts (either on initial connection, or
 * when a connection is lost).
 */
public class NoReconnectFilter implements DeviceReconnectFilter
{
    @Override
    public ConnectFailPlease onConnectFailed(ConnectFailEvent event)
    {
        return ConnectFailPlease.doNotRetry();
    }

    @Override
    public ConnectionLostPlease onConnectionLost(ConnectionLostEvent event)
    {
        return ConnectionLostPlease.stopRetrying();
    }
}
