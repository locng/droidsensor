/*
 * Copyright (C) 2009, DroidSensor - http://code.google.com/p/droidsensor/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sevenleaves.droidsensor.handlers;

/**
 * デバイスが無効になろうとしている状態での遷移を処理するハンドラー.
 * 
 * @author esmasui@gmail.com
 * 
 */
public class StateTurningOffHandler extends AbstractBluetoothStateHandler {

	public BluetoothState getResponsibility() {

		return BluetoothState.STATE_TURNING_OFF;
	}

}