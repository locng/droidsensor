package org.sevenleaves.droidsensor;

import org.sevenleaves.droidsensor.IDroidSensorCallbackListener;

interface IDroidSensorService {

	boolean isStarted();
	
	void stopService();
	
	void addListener(IDroidSensorCallbackListener listener);
	
	void removeListener(IDroidSensorCallbackListener listener);
}
