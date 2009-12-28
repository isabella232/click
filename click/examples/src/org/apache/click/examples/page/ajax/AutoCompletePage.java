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
package org.apache.click.examples.page.ajax;

import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.PostCodeService;
import org.apache.click.extras.control.AutoCompleteTextField;
import org.apache.click.util.Bindable;
import org.springframework.stereotype.Component;

/**
 * Provides AJAX AutoCompleteTextField example page.
 *
 * @author Malcolm Edgar
 */
@Component
public class AutoCompletePage extends BorderPage {

    @Bindable protected Form form = new Form();

    @Resource(name="postCodeService")
    private PostCodeService postCodeService;

    // ------------------------------------------------------------ Constructor

    public AutoCompletePage() {
        FieldSet fieldSet = new FieldSet("Enter a Suburb Location");
        fieldSet.setStyle("background-color", "");
        form.add(fieldSet);

        AutoCompleteTextField locationField = new AutoCompleteTextField("location") {

            public List getAutoCompleteList(String criteria) {
                return postCodeService.getPostCodeLocations(criteria);
            }
        };
        locationField.setSize(40);

        fieldSet.add(locationField);
    }

}
