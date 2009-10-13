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

package org.sevenleaves.droidsensor;

public class HardReference<T> {

    private T _held;

    /**
     * use create() instead.
     */
    private HardReference() {
    }

    public void put(final T object) {
    	
        _held = object;
    }

    public T get() {
    	
        return _held;
    }

    public boolean hasValue() {
    	
        return _held != null;
    }

    public static <T> HardReference<T> create() {
    	
        return new HardReference<T>();
    }

    public static <T> HardReference<T> create(T t) {
    	
        HardReference<T> h = new HardReference<T>();
        h.put(t);
        return h;
    }

}