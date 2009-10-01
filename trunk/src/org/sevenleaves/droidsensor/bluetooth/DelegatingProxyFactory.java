package org.sevenleaves.droidsensor.bluetooth;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class DelegatingProxyFactory {

	private static final DelegatingProxyFactory SINGLETON = new DelegatingProxyFactory();

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<T> type, Object underlying) {

		return (T) SINGLETON.createProxyInternal(type, underlying);
	}

	@SuppressWarnings("unchecked")
	private <T> Object createProxyInternal(Class<T> type, Object underlying) {

		InvocationHandler handler = new InvocationHandlerImpl(type, underlying);

		T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(),
				new Class[] { type }, handler);

		return proxy;
	}

	private static final class InvocationHandlerImpl implements
			InvocationHandler {

		private Object _underlying;

		public InvocationHandlerImpl(Class<?> type, Object underlying) {

			_underlying = underlying;
		}

		private boolean isMethodStatic(Method m){
			
			int modifiers = m.getModifiers();
			boolean res = Modifier.isStatic(modifiers);
			
			return res;
		}
		
		public Object invoke(Object target, Method method, Object[] args)
				throws Throwable {

			String name = method.getName();
			Class<? extends Object> clazz = _underlying.getClass();
			Method underlyingMethod = clazz.getMethod(name, method
					.getParameterTypes());
			
			if(!underlyingMethod.isAccessible()){
				
				underlyingMethod.setAccessible(true);
			}
			
			Object receiver = null;

			if(!isMethodStatic(underlyingMethod)){
				
				receiver = _underlying;
			}
			
			Object res = underlyingMethod.invoke(receiver, args);

			return res;
		}
	}

}
