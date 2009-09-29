package org.sevenleaves.droidsensor;




import it.gerdavax.android.bluetooth.RemoteBluetoothDevice;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;

public class LocalBluetoothDeviceAdapter {

	private LocalBluetoothDevice _target;

	public LocalBluetoothDeviceAdapter(LocalBluetoothDevice target) {
		
		_target = target;
	}

	public LocalBluetoothDevice getTarget() {

		return _target;
	}

	public void setTarget(LocalBluetoothDevice target) {
		
		_target = target;
	}

	public void close(Context context) {
		
		_target.close(context);
	}

	public String getAddress() throws Exception {
		
		return _target.getAddress();
	}

	public String getCompany() throws Exception {
		
		return _target.getCompany();
	}

	public String getManufacturer() throws Exception {
		
		return _target.getManufacturer();
	}

	public String getName() {
		
		return _target.getName();
	}

	public RemoteBluetoothDevice getRemoteBluetoothDevice(String address) {
		
		return _target.getRemoteBluetoothDevice(address);
	}

	public int getRemoteClass(String address) throws Exception {
		
		return _target.getRemoteClass(address);
	}

	public String getRemoteName(String address) throws Exception {
		
		return _target.getRemoteName(address);
	}

	public boolean isEnabled() throws Exception {
		
		return _target.isEnabled();
	}

	public boolean isScanning() throws Exception {
		
		return _target.isScanning();
	}

	public void scan() throws Exception {
		
		_target.scan();
	}

	public boolean setEnabled(boolean enabled) throws Exception {
		
		return _target.setEnabled(enabled);
	}

	public void setListener(LocalBluetoothDeviceListener listener) {
		
		_target.setListener(listener);
	}

	public void stopScanning() throws Exception {
		
		_target.stopScanning();
	}
	
	@SuppressWarnings("unchecked")
	protected final <T> T exposeDeclaredField(Object target, String name){
		
		try {
			
			Field field = target.getClass().getDeclaredField(name);
			
			if(!field.isAccessible()){
				
				field.setAccessible(true);
			}
			
			Object val = field.get(target);
			
			return (T) val;
			
		} catch (SecurityException e) {

			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	protected final <T> void replaceDeclaredField(Object target, String name, T value){
		
		try {
			
			Field field = target.getClass().getDeclaredField(name);
			
			if(!field.isAccessible()){
				
				field.setAccessible(true);
			}
			
			field.set(target, value);
		} catch (SecurityException e) {

			throw new RuntimeException(e);
		} catch (NoSuchFieldException e) {
			
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	protected LocalBluetoothDevice exposeLocalBluetoothDevice(){
		
		//private static LocalBluetoothDevice _localDevice;
		return exposeDeclaredField(_target, "_localDevice");
	}

	protected Object exposeBluetoothService(){
		
		//private static Object bluetoothService;
		return exposeDeclaredField(_target, "bluetoothService");
	}
	
	protected Class<?> exposeBluetoothServiceClass(){
		
		//private static Class bluetoothServiceClass;
		return exposeDeclaredField(_target, "bluetoothServiceClass");
	}
	
	protected LocalBluetoothDeviceListener exposeLocalBluetoothDeviceListener(){
		
		//private LocalBluetoothDeviceListener listener;
		return exposeDeclaredField(_target, "listener");
	}

	protected List<String> exposeDevices(){
		
		//private ArrayList<String> devices = new ArrayList<String>();
		LocalBluetoothDevice device = exposeLocalBluetoothDevice();
		return exposeDeclaredField(device, "devices");
	}

	protected void replaceDevices(List<String> devices){
	
		LocalBluetoothDevice device = exposeLocalBluetoothDevice();
		//private ArrayList<String> devices = new ArrayList<String>();
		replaceDeclaredField(device, "devices", devices);
	}

	protected Object exposeBluetoothBroadcastReceiver(){
		
		//private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
		Object field = exposeDeclaredField(_target, "bluetoothBroadcastReceiver");
		return field;
	}		
	
	protected Hashtable<String,RemoteBluetoothDevice> exposeRemoteDevices(){

		//private Hashtable<String, RemoteBluetoothDeviceImpl> remoteDevices = new Hashtable<String, RemoteBluetoothDeviceImpl>();
		LocalBluetoothDevice device = exposeLocalBluetoothDevice();
		return exposeDeclaredField(device, "remoteDevices");
	}

	protected void replaceRemoteDevices(Hashtable<String,RemoteBluetoothDevice> devices){
	
		LocalBluetoothDevice device = exposeLocalBluetoothDevice();
		//private Hashtable<String, RemoteBluetoothDeviceImpl> remoteDevices = new Hashtable<String, RemoteBluetoothDeviceImpl>();
		replaceDeclaredField(device, "remoteDevices", devices);
	}

	

}
