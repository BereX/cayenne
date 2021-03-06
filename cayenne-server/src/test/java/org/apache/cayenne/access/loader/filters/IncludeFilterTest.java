/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cayenne.access.loader.filters;

import org.junit.Test;

import static org.apache.cayenne.access.loader.filters.FilterFactory.pattern;
import static org.junit.Assert.*;

public class IncludeFilterTest {

    @Test
    public void testIsInclude() throws Exception {
        IncludeFilter filter = new IncludeFilter(pattern("^v_.*$"));
        assertTrue(filter.isInclude("v_new_view"));
        assertFalse(filter.isInclude("new_view"));
        assertFalse(filter.isInclude("view"));
        assertFalse(filter.isInclude("girl"));
    }
}