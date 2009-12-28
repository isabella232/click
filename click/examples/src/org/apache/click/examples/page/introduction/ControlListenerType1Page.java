/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.page.introduction;

import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.Bindable;

/**
 * Provides a control listener example Page using the runtime binding of the
 * control listener.
 * <p/>
 * The advantage of this control listener binding style is
 * that you write less lines of code, the disadvantage is that there is no
 * compile time checking.
 *
 * @author Malcolm Edgar
 */
public class ControlListenerType1Page extends BorderPage {

    /* Set the listener to this object's "onLinkClick" method. */
    @Bindable protected ActionLink myLink = new ActionLink(this, "onLinkClick");

    @Bindable protected String msg;

    // --------------------------------------------------------- Event Handlers

    /**
     * Handle the ActionLink control click event.
     */
    public boolean onLinkClick() {
        msg = "ControlListenerPage#" + hashCode()
            + " object method <tt>onLinkClick()</tt> invoked.";

        return true;
    }

}
