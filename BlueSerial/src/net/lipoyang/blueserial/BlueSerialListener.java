/*
 * Copyright (C) 2014 Bizan Nishimura (@lipoyang)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lipoyang.blueserial;

import java.util.EventListener;

public interface BlueSerialListener extends EventListener
{
    // on connecting to the Bluetooth device
    public void onConneting();
    
    // on connected to the Bluetooth device
    public void onConneted(String devideName);
    
    // on disconnected from the Bluetooth device
    public void onDisconneted();
}
