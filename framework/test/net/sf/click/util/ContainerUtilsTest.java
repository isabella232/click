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
package net.sf.click.util;

import java.util.List;
import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Button;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Label;
import net.sf.click.control.TextField;

/**
 * Tests for ContainerUtils.
 */
public class ContainerUtilsTest extends TestCase {

    /**
     * Check that all input fields are retrieved from container.
     */
    public void testGetInputFields() {
        MockContext.initContext();
        // set up the form
        Form form = new Form("sample");

        // HiddenField should be included
        HiddenField hiddenField = new HiddenField("hidden", boolean.class);
        form.add(hiddenField);

        // TextField should be included
        TextField idField = new TextField("id");
        form.add(idField);

        // FieldSet should NOT be included
        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        // Label should NOT be included
        Label label = new Label("name");
        form.add(label);

        // Button should NOT be included
        Button button = new Button("button");
        form.add(button);
        
        List fields = ContainerUtils.getInputFields(form);
        
        // Total should be 3 consisting of the fields "hidden", "id" and the
        // Forms internal HiddenFields "form_name".
        assertEquals(3, fields.size());
    }

    /**
     * Check that domain class with duplicate getter methods resolves to the
     * no-argument version.
     *
     * CLK-461
     */
    public void testCopyToErrorMessages() {
        MockContext context = MockContext.initContext();
        
        // Setup request parameters
        String price = "10.99";
        context.getMockRequest().setParameter("part.price", price);
        context.getMockRequest().setParameter("form_name", "form");

        // Setup form
        Form form = new Form("form");

        // Setup price field
        TextField priceField = new TextField("part.price");
        form.add(priceField);

        // Process form to bind request parameter to field
        form.onProcess();

        Car car = new Car();
        form.copyTo(car);

        assertEquals(price, priceField.getValue());
    }

    /**
     * Test Car class.
     */
    public static class Car {

        /** Part variable. */
        private Part part;

        /**
         * Getter for part
         * @return part
         */
        public Part getPart() {
            return part;
        }

        /**
         * Duplicate Getter for part with int argument
         * @param arg
         * @return part
         */
        public Part getPart(int arg) {
            return part;
        }

        /**
         * Setter for part
         * @param part
         */
        public void setPart(Part part) {
            this.part = part;
        }
    }

    /**
     * Test Part class.
     */
    public static class Part {

        /** Price variable. */
        private double price;

        /**
         * Getter for price.
         * @return price
         */
        public double getPrice() {
            return price;
        }

        /**
         * Setter for price.
         * @param price
         */
        public void setPrice(double price) {
            this.price = price;
        }
    }
}
