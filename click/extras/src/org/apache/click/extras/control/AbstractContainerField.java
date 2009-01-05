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
package org.apache.click.extras.control;

import java.util.List;
import java.util.Map;

import org.apache.click.ActionListener;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.control.AbstractContainer;
import org.apache.click.control.Container;
import org.apache.click.control.Field;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an abstract convenience class that implements Container and extend Field.
 * <p/>
 * AbstractContainerField delegates Contain specific actions to an internal
 * Container instance. You can access the Container instance through
 * {@link #getContainer()}.
 * <p/>
 * If you need to bind a request parameter to this fields value, please see
 * {@link #bindRequestValue()}.
 * <p/>
 * Here is an example of a Border Control that can wrap a Button and render
 * a <tt>div</tt> border around it.
 * <pre class="prettyprint">
 * public class ButtonBorder extends AbstractContainerField {
 *     public ButtonBorder(String name) {
 *         super(name);
 *     }
 *
 *     public String getTag() {
 *         return "div";
 *     }
 *
 *     public Control addControl(Button button) {
 *         return getContainer().addControl(button);
 *     }
 * } </pre>
 *
 * @author Bob Schellink
 */
public abstract class AbstractContainerField extends Field implements Container {

    // ----------------------------------------------------- Instance Variables

    /** Internal container instance. */
    protected AbstractContainer container = new InnerContainerField();

    // ---------------------------------------------------------- Constructorrs

    /**
     * Create an AbstractContainerField with no name defined.
     */
    public AbstractContainerField() {
    }

    /**
     * Create an AbstractContainerField with the given name.
     *
     * @param name the ContainerField name
     */
    public AbstractContainerField(String name) {
        super(name);
    }

    /**
     * Construct an AbstractContainerField with the given name and label.
     *
     * @param name the name of the Field
     * @param label the label of the Field
     */
    public AbstractContainerField(String name, String label) {
        super(name, label);
    }

    // ------------------------------------------------------ Public methods

    /**
     * @see org.apache.click.control.Container#add(org.apache.click.Control).
     *
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     */
    public Control add(Control control) {
        return insert(control, getControls().size());
    }

    /**
     * @see org.apache.click.control.Container#insert(org.apache.click.Control, int).
     *
     * @param control the control to add to the container and return
     * @param index the index at which the control is to be inserted
     * @return the control that was added to the container
     */
    public Control insert(Control control, int index) {
        return container.insert(control, index);
    }

    /**
     * @see org.apache.click.control.Container#remove(org.apache.click.Control)
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     */
    public boolean remove(Control control) {
        return container.remove(control);
    }

    /**
     * Return the internal container instance.
     *
     * @return the internal container instance
     */
    public Container getContainer() {
        return container;
    }

    /**
     * @see org.apache.click.control.Container#getControls()
     *
     * @return the sequential list of controls held by the container
     */
    public List getControls() {
        return container.getControls();
    }

    /**
     * @see org.apache.click.control.Container#getControl(java.lang.String)
     *
     * @param controlName the name of the control to get from the container
     * @return the named control from the container if found or null otherwise
     */
    public Control getControl(String controlName) {
        return container.getControl(controlName);
    }

    /**
     * @see org.apache.click.control.Container#contains(org.apache.click.Control)
     *
     * @param control the control whose presence in this container is to be tested
     * @return true if the container contains the specified control
     */
    public boolean contains(Control control) {
        return container.contains(control);
    }

    /**
     * Returns true if this container has existing controls, false otherwise.
     *
     * @see AbstractContainer#hasControls()
     *
     * @return true if the container has existing controls, false otherwise.
     */
    public boolean hasControls() {
        return container.hasControls();
    }

    /**
     * Set the parent of the Field.
     *
     * @see org.apache.click.Control#setParent(Object)
     *
     * @param parent the parent of the Control
     * @throws IllegalArgumentException if the given parent instance is
     * referencing <tt>this</tt> object: <tt>if (parent == this)</tt>
     */
    public void setParent(Object parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Cannot set parent to itself");
        }
        this.parent = parent;
    }

    /**
     * Return the HTML head import statements for contained controls.
     *
     * @see org.apache.click.Control#getHtmlImports()
     *
     * @return the HTML includes statements for the contained control stylesheet
     * and JavaScript files
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                String htmlImports = control.getHtmlImports();
                if (htmlImports != null) {
                    buffer.append(htmlImports);
                }
            }
        }

        return buffer.toString();
    }

    /**
     * This method does nothing by default.
     * <p/>
     * Subclasses should override this method to binds the submitted request
     * value to the Field's value. For example:
     * <p/>
     * <pre class="prettyprint">
     * public CoolField extends AbstractContainerField {
     *
     *     public CoolField(String name) {
     *         super(name);
     *     }
     *
     *     public void bindRequestValue() {
     *         setValue(getRequestValue());
     *     }
     *
     *     // Below is the actual getRequestValue implementation as defined
     *     // in Field. This is done solely to show how to retrieve the
     *     // request parameter based on the fields name.
     *     protected String getRequestValue() {
     *         String value = getContext().getRequestParameter(getName());
     *         if (value != null) {
     *             return value.trim();
     *         } else {
     *             return "";
     *         }
     *     }
     * }
     * </pre>
     *
     * Note you can use method {@link #getRequestValue()} to retrieve the
     * fields value if the request parameter is the fields name.
     */
    public void bindRequestValue() {
    }

    /**
     * @see org.apache.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        boolean continueProcessing = super.onProcess();
        if (!container.onProcess()) {
            continueProcessing = false;
        }
        return continueProcessing;
    }

    /**
     * @see org.apache.click.Control#onDestroy()
     */
    public void onDestroy() {
        container.onDestroy();
    }

    /**
     * @see org.apache.click.Control#onInit()
     */
    public void onInit() {
        container.onInit();
    }

    /**
     * @see org.apache.click.Control#onRender()
     */
    public void onRender() {
        container.onRender();
    }

    /**
     * By default render the container and all its child controls to the
     * specified buffer.
     * <p/>
     * If {@link org.apache.click.control.AbstractControl#getTag()} returns null,
     * this method will render only its child controls.
     * <p/>
     * @see org.apache.click.control.AbstractControl#render(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        //If tag is set, render it
        if (getTag() != null) {
            renderTagBegin(getTag(), buffer);
            buffer.closeTag();
            if (hasControls()) {
                buffer.append("\n");
            }
            renderContent(buffer);
            renderTagEnd(getTag(), buffer);
            buffer.append("\n");

        } else {

            //render only content because no tag is specified
            if (hasControls()) {
                renderContent(buffer);
            }
        }
    }

    /**
     * Returns the HTML representation of this control.
     * <p/>
     * This method delegates the rendering to the method
     * {@link #render(org.apache.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    //-------------------------------------------- protected methods

    /**
     * @see org.apache.click.control.AbstractControl#renderTagEnd(java.lang.String, org.apache.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd(tagName);
    }

    /**
     * Render this container content to the specified buffer.
     *
     * @see org.apache.click.control.AbstractContainer#renderContent(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

    /**
     * Render this container children to the specified buffer.
     *
     * @see org.apache.click.control.AbstractContainer#renderChildren(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = (Control) getControls().get(i);

                int before = buffer.length();
                control.render(buffer);
                int after = buffer.length();
                if (before != after) {
                    buffer.append("\n");
                }
            }
        }
    }

    /**
     * Return the map of controls where each map's key / value pair will consist
     * of the control name and instance.
     *
     * @see org.apache.click.control.AbstractContainer#getControlMap()
     *
     * @return the map of controls
     */
    protected Map getControlMap() {
        return container.getControlMap();
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    protected int getControlSizeEst() {
        return container.getControlSizeEst();
    }

    // -------------------------------------------------------- Inner Class

    /**
     * Inner class providing the container implementation for
     * AbstractContainerField.
     * <p/>
     * Note this class delegates certain methods to AbstractContainerField, so
     * that the Container implementation can manipulate state of the
     * AbstractContainerField instance.
     */
    class InnerContainerField extends AbstractContainer {

        // -------------------------------------------------------- Constants

        private static final long serialVersionUID = 1L;

        // -------------------------------------------------------- Public Methods

        /**
         * Return the AbstractContainerField html tag.
         *
         * @return the AbstractContainerField html tag
         */
        public String getTag() {
            return AbstractContainerField.this.getTag();
        }

        /**
         * Sets the AbstractContainerField parent.
         *
         * @param parent the parent of the AbstractContainerField
         */
        public void setParent(Object parent) {
            AbstractContainerField.this.setParent(parent);
        }

        /**
         * Sets the AbstractContainerField name.
         *
         * @param name the name of the AbstractContainerField
         */
        public void setName(String name) {
            AbstractContainerField.this.setName(name);
        }

        /**
         * Sets the action listener of the AbstractContainerField.
         *
         * @param actionListener the action listener object to invoke
         */
        public void setActionListener(ActionListener actionListener) {
            AbstractContainerField.this.setActionListener(actionListener);
        }

        /**
         * Sets the listener of the AbstractContainerField.
         *
         * @param listener the listener object with the named method to invoke
         * @param method the name of the method to invoke
         */
        public void setListener(Object listener, String method) {
            AbstractContainerField.this.setListener(listener, method);
        }

        /**
         * Return the parent of the AbstractContainerField.
         *
         * @return the parent of the AbstractContainerField
         */
        public Object getParent() {
            return AbstractContainerField.this.getParent();
        }

        /**
         * Return the name of the AbstractContainerField.
         *
         * @return the name of the AbstractContainerField
         */
        public String getName() {
            return AbstractContainerField.this.getName();
        }

        /**
         * Return the messages of the AbstractContainerField.
         *
         * @return the message of the AbstractContainerField
         */
        public Map getMessages() {
            return AbstractContainerField.this.getMessages();
        }

        /**
         * Return the id of the AbstractContainerField.
         *
         * @return the id of the AbstractContainerField
         */
        public String getId() {
            return AbstractContainerField.this.getId();
        }

        /**
         * Return the html imports of the AbstractContainerField.
         *
         * @return the html imports of the AbstractContainerField
         */
        public String getHtmlImports() {
            return AbstractContainerField.this.getHtmlImports();
        }

        /**
         * Return the Context of the AbstractContainerField.
         *
         * @return the Context of the AbstractContainerField
         */
        public Context getContext() {
            return AbstractContainerField.this.getContext();
        }
    }
}
