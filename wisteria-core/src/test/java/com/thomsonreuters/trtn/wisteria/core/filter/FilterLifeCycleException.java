/*
 *  Licensed to the Apache Software Foundation (ASF) under one
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
 *
 */
package com.thomsonreuters.trtn.wisteria.core.filter;


/**
 * A {@link RuntimeException} which is thrown when {@link Filter#init()}
 * or {@link Filter#onPostAdd(IoFilterChain, String, org.apache.Filter.core.filterchain.IoFilter.NextFilter)}
 * failed.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class FilterLifeCycleException extends RuntimeException {
    private static final long serialVersionUID = -5542098881633506449L;

    public FilterLifeCycleException() {
    }

    public FilterLifeCycleException(String message) {
        super(message);
    }

    public FilterLifeCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterLifeCycleException(Throwable cause) {
        super(cause);
    }
}
