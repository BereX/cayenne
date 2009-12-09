/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.di;

import java.util.List;
import java.util.Map;

/**
 * A facade to the Cayenne DI container. To create an injector use {@link DIBootstrap}
 * static methods.
 * 
 * @since 3.1
 */
public interface Injector {

    /**
     * Returns a service instance bound in the container for a specific type. Throws
     * {@link DIException} if the type is not bound, or an instance can not be created.
     */
    <T> T getInstance(Class<T> type) throws DIException;

    <T> Provider<T> getProvider(Class<T> type) throws DIException;

    <T> Map<String, ?> getMapConfiguration(Class<T> type);

    <T> List<?> getListConfiguration(Class<T> type);

    /**
     * Performs field injection on a given object, ignoring constructor injection. Since
     * Cayenne DI injector returns fully injected objects, this method is rarely used
     * directly. One possible use is in unit tests to test a specific object that requires
     * field injection.
     */
    void injectMembers(Object object);
}