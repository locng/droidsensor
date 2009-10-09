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

package org.sevenleaves.droidsensor.bluetooth;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author esmasui@gmail.com
 *
 */
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

		private Map<Method, Method> _methodCache = new ConcurrentHashMap<Method, Method>();

		public InvocationHandlerImpl(Class<?> type, Object underlying) {

			_underlying = underlying;
		}

		private boolean isMethodStatic(Method m) {

			int modifiers = m.getModifiers();
			boolean res = Modifier.isStatic(modifiers);

			return res;
		}

		private Method getMethod(Method method) throws SecurityException,
				NoSuchMethodException {

			if (_methodCache.containsKey(method)) {

				Method cache = _methodCache.get(method);

				return cache;
			}

			String name = method.getName();
			Class<? extends Object> clazz = _underlying.getClass();
			Method underlyingMethod = clazz.getMethod(name, method
					.getParameterTypes());

			if (!underlyingMethod.isAccessible()) {

				underlyingMethod.setAccessible(true);
			}

			_methodCache.put(method, underlyingMethod);

			return underlyingMethod;
		}

		public Object invoke(Object target, Method method, Object[] args)
				throws Throwable {

			Method underlyingMethod = getMethod(method);

			Object receiver = null;

			if (!isMethodStatic(underlyingMethod)) {

				receiver = _underlying;
			}

			Object res = underlyingMethod.invoke(receiver, args);

			return res;
		}
	}

}
