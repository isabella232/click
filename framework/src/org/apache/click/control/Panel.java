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
package org.apache.click.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.click.ActionListener;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.Page;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.Format;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.click.util.SessionMap;

/**
 * Provides a Panel container that has its own {@link #template} and
 * {@link #model}.
 * <p/>
 * Panels are powerful components for creating modular, reusable
 * and customized layout sections within a page.
 *
 * <table style='margin-bottom: 1.25em'>
 * <tr>
 * <td>
 * <fieldset>
 * <legend>Panel</legend>
 * <i>My panel content.</i>
 * </fieldset>
 * </td>
 * </tr>
 * </table>
 *
 * The Panel class uses a template for rendering model data and controls that
 * have been added to the Panel. Furthermore the Panel's parent
 * {@link org.apache.click.Page#model Page model} is also made available to the
 * Panel template.
 * <p/>
 *
 * <h3>Example 1 - A Simple Panel</h3>
 *
 * This example shows how to create a basic Panel and adding it to a Page.
 * <p/>
 * First we create the <tt>/panel/simple-panel.htm</tt> that references the
 * variable <span class="st">$time</span>:
 *
 * <pre class="codeHtml">The time is now <span class="st">$time</span></pre>
 *
 * Then in our page class, <tt>SimplePageDemo</tt>, we create and add the Panel
 * instance:
 *
 * <pre class="prettyprint">
 * public class SimplePageDemo extends Page {
 *
 *     private Panel panel = new Panel("panel", "/panel/simple-panel.htm");
 *
 *     public SimplePanelDemo() {
 *          Date time = new Date();
 *
 *         // Add the $time variable to the panel model
 *         panel.getModel().put("time", time);
 *
 *         addControl(panel);
 *     }
 * } </pre>
 *
 * The SimplePanelDemo template, <tt>/simple-panel-demo.htm</tt>, would
 * reference the panel control:
 *
 * <pre class="codeHtml"><span class="st">$panel</span></pre>
 *
 * The Panel template would then be merged with the Panel model and
 * rendered in the page as:
 *
 * <pre class="codeHtml">Time time is now Sun Mar 15 07:32:51 EST 2009 </pre>
 *
 * <h3>Example 2 - Localization support</h3>
 *
 * In this example, we demonstrate localization support by
 * specifying the Panel content in the <tt>SimplePanelDemo.properties</tt> file.
 * Since the Panel model and Page model are merged at runtime, the Panel template
 * can access the Page messages.
 *
 * <p/>
 * First we create the <tt>SimplePanelDemo.properties</tt> file which specifies
 * two properties: <span style="color:#7F0055">heading</span> and <span style="color:#7F0055">content</span>.
 * <pre class="codeConfig">
 * <span style="color:#7F0055">heading</span>=Welcome
 * <span style="color:#7F0055">content</span>=Welcome to MyCorp&lt;p/&gt;MyCorp is your telecommuting office portal. Its just like being there at the office!</pre>
 *
 * <p/>
 * Then we create the <tt>/panel/simple-panel.htm</tt> that references the
 * localized Page properties. Since a Page properties are made available through
 * the <span class="st">$messages</span> map, the Panel can access the Page
 * properties using the variables <span class="st">$messages.header</span> and
 * <span class="st">$messages.content</span>:
 *
 * <pre class="codeHtml">
 * &lt;fieldset&gt;
 *   &lt;legend class="title"&gt; <span class="st">$messages.heading</span> &lt;/legend&gt;
 *   <span class="st">$messages.content</span>
 * &lt;/fieldset&gt; </pre>
 *
 * In our page class, <tt>SimplePageDemo</tt>, we create and add the Panel
 * instance:
 *
 * <pre class="prettyprint">
 * public class SimplePanelDemo extends Page {
 *
 *     public Panel panel = new Panel("panel", "/panel/simple-panel.htm");
 * } </pre>
 *
 * In the Page above we make use of Click's <tt>autobinding</tt> feature by
 * declaring a <tt>public</tt> Panel field. Autobinding will automatically add
 * the Panel to the Page model.
 *
 * <p/>
 * The SimplePanelDemo template, <tt>/simple-panel-demo.htm</tt>, would
 * reference the panel control:
 *
 * <pre class="codeHtml"> <span class="st">$panel</span> </pre>
 *
 * And the result is:
 *
 * <fieldset style="margin:2em;width:550px;">
 * <legend><b>Welcome</b></legend>
 * Welcome to MyCorp.
 * <p/>
 * MyCorp is your telecommuting office portal. Its just like being there at the
 * office!
 * </fieldset>
 *
 * <h3>Example 3 - Reusing and Nesting Panels</h3>
 *
 * Panels provide a good way to create reusable components, and since Panel is
 * a Container it can hold child controls, even other Panels.
 *
 * <p/>
 * In this example we create a reusable <tt>CustomerPanel</tt> which is added
 * to a Border Panel.
 *
 * <p/>
 * First we create the <tt>/panel/customer-panel.htm</tt> template which references
 * the <span class="st">$form</span> variable:
 *
 * <pre class="codeHtml"> <span class="st">$form</span> </pre>
 *
 * Next up is the <tt>CustomerPanel</tt>:
 *
 * <pre class="prettyprint">
 * public class CustomerPanel extends Panel {
 *
 *     private Form form = new Form("form");
 *
 *     public CustomerPanel(String name) {
 *         super(name);
 *
 *         // We explicitly set the customer panel template
 *         setTemplate("/panel/customer-panel.htm");
 *
 *         form.add(new TextField("name");
 *         form.add(new DateField("dateJoined");
 *         form.add(new DoubleField("holdings");
 *     }
 * } </pre>
 *
 * The Border Panel template, <tt>/panel/border-panel.htm</tt>, will draw a
 * Border around its contents:
 *
 * <pre class="codeHtml">
 * &lt;div&gt; style="border: 1px solid black"&gt;
 * <span class="st">$panel</span>
 * &lt;/div&gt; </pre>
 *
 * Lastly we specify the <tt>NestedDemo</tt> Page, that creates a Border Panel,
 * and adds <tt>CustomerPanel</tt> as a child.
 *
 * <pre class="prettyprint">
 * public class NestedDemo extends Page {
 *
 *     private Panel borderPanel = new Panel("borderPanel", "/panel/border-panel.htm");
 *
 *     private CustomerPanel customerPanel = new CustomerPanel("panel");
 *
 *     public void onInit() {
 *         // Add CustomerPanel to the Border panel
 *         parentPanel.add(childPanel);
 *
 *         // Add border panel to page
 *         addControl(parentPanel);
 *     }
 * } </pre>
 *
 * The Page template, <tt>/nested-demo.htm</tt>, would reference the
 * <span class="st">$borderPanel</span> variable:
 *
 * <pre class="codeHtml"> <span class="st">$borderPanel</span> </pre>
 *
 * <h3>Template Model</h3>
 *
 * To render the panel's template, a model is created ({@link #createTemplateModel()})
 * which is merged with the template.  This model will include the page model
 * values, plus any Panel defined model values, with the Panel's values overriding
 * the Page defined values. In addition a number of predefined values are
 * automatically added to the model. These values include:
 * <ul>
 * <li>attributes - the panel HTML attributes map</li>
 * <li>context - the Servlet context path, e.g. /mycorp</li>
 * <li>format - the page {@link Format} object for formatting the display of objects</li>
 * <li>this - a reference to this panel object</li>
 * <li>messages - the panel messages bundle</li>
 * <li>request - the servlet request</li>
 * <li>response - the servlet request</li>
 * <li>session - the {@link SessionMap} adaptor for the users HttpSession</li>
 * </ul>
 *
 * @author Phil Barnes
 * @author Malcolm Edgar
 */
public class Panel extends AbstractContainer {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The panel disabled value. */
    protected boolean disabled;

    /** The "identifier" for this panel (CSS id for rendering). */
    protected String id;

    /** The (localized) label of this panel. */
    protected String label;

    /** A temporary storage for model objects until the Page is set. */
    protected Map model;

    /** The list of sub panels. */
    protected List panels;

    /** The path of the template to render. */
    protected String template;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Panel with the given name.
     *
     * @param name the name of the panel
     */
    public Panel(String name) {
        setName(name);
    }

    /**
     * Create a Panel with the given name and template path.
     *
     * @param name the name of the panel
     * @param template the template path
     */
    public Panel(String name, String template) {
        setName(name);
        setTemplate(template);
    }

    /**
     * Create a Panel with the given name, id attribute and template path.
     *
     * @param name the name of the panel
     * @param id the id HTML attribute value
     * @param template the template path
     */
    public Panel(String name, String id, String template) {
        setName(name);
        setId(id);
        setTemplate(template);
    }

    /**
     * Create a Panel with no name or template defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Panel() {
    }

    // ------------------------------------------------------------- Properties

    /**
     * @see #add(org.apache.click.Control)
     *
     * @deprecated use {@link #add(org.apache.click.Control)} instead
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, if the name
     *     of the control is not defined or the container already contains a
     *     control with the same name
     */
    public Control addControl(Control control) {
        return add(control);
    }

    /**
     * Add the control to the panel and return the specified control.
     * <p/>
     * In addition to the requirements specified by
     * {@link Container#add(org.apache.click.Control)}, note
     * the following:
     * <ul>
     *  <li>
     *   The control's name must be set when adding to a panel.
     *  </li>
     *  <li>
     *   The control will be added to the Panel model using the controls name as
     *   the key and can be accessed through {@link #getModel()}. This allows
     *   one to reference the control in the Panels template.
     *  </li>
     *  <li>
     *   If the specified control is an <tt>instanceof</tt> a Panel, it will
     *   also be added to a list of panels and can be accessed through
     *   {@link #getPanels()}.
     *  </li>
     * </ul>
     * @see org.apache.click.control.Container#add(org.apache.click.Control)
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null or the container
     *     already contains a control with the same name
     */
    public Control add(Control control) {
        super.add(control);

        String controlName = control.getName();
        if (controlName != null) {
            // If controls name is set, add control to the model
            addModel(controlName, control);
        }

        if (control instanceof Panel) {
            getPanels().add(control);
        }

        return control;
    }

    /**
     * @see #remove(org.apache.click.Control)
     *
     * @deprecated use {@link #remove(org.apache.click.Control)} instead
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     * @throws IllegalArgumentException if the control is null or if the name of
     *     the control is not defined
     */
    public boolean removeControl(Control control) {
        return remove(control);
    }

    /**
     * Remove the control from the panel and returning true if the control was
     * found in the container and removed, or false if the control was not
     * found.
     * <p/>
     * In addition to the requirements specified by
     * {@link Container#remove(org.apache.click.Control)}, the controls name
     * must also be set.
     *
     * @see org.apache.click.control.Container#remove(org.apache.click.Control)
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     * @throws IllegalArgumentException if the control is null
     */
    public boolean remove(Control control) {
        boolean contains = super.remove(control);

        String controlName = control.getName();
        if (controlName != null) {
            // If control has name, remove it from the model
            getModel().remove(controlName);
        }

        if (control instanceof Panel) {
            getPanels().remove(control);
        }

        return contains;
    }

    /**
     * Return true if the panel is disabled.
     *
     * @return true if the panel is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Set whether the panel is disabled.
     *
     * @param disabled the disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the panel id value. If no id attribute is defined then this method
     * will return the panel name. If no name is defined this method will return
     * <tt>null</tt>.
     *
     * @see org.apache.click.Control#getId()
     *
     * @return the panel HTML id attribute value
     */
    public String getId() {
        if (id != null) {
            return id;

        } else {
            String id = getName();

            if (id == null) {
                // If panel name is null, return null
                return null;
            }

            if (id.indexOf('/') != -1) {
                id = id.replace('/', '_');
            }
            if (id.indexOf(' ') != -1) {
                id = id.replace(' ', '_');
            }
            if (id.indexOf('<') != -1) {
                id = id.replace('<', '_');
            }
            if (id.indexOf('>') != -1) {
                id = id.replace('>', '_');
            }

            return id;
        }
    }

    /**
     * Set the id for this panel.  This is the identifier that will be assigned
     * to the 'id' tag for this panel's model.
     *
     * @param id the id attribute for this panel
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return the panel display label.
     * <p/>
     * If the label value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".label"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value still cannot be found then the Panel name will be converted
     * into a label using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     * Typically the label property is used as a header for a particular panel.
     * For example:
     *
     * <pre class="codeHtml">
     *  &lt;div id="$panel.id"&gt;
     *      &lt;h1&gt;$panel.label&lt;/h1&gt;
     *      ## content here
     *  &lt;/div&gt; </pre>
     *
     * @return the internationalized label associated with this control
     */
    public String getLabel() {
        if (label == null) {
            label = getMessage(getName() + ".label");
        }
        if (label == null) {
            label = ClickUtils.toLabel(getName());
        }
        return label;
    }

    /**
     * Set the Panel display caption.
     *
     * @param label the display label of the Panel
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * This method does nothing, since Panel does not support listener callback.
     *
     * @see Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * This method does nothing, since Panel does not support action listener
     * callback.
     *
     * @see AbstractControl#setActionListener(org.apache.click.ActionListener)
     *
     * @param listener the control's action listener
     */
    public void setActionListener(ActionListener listener) {
    }

    /**
     * Add the named object value to the Panels model map.
     *
     * @param name the key name of the object to add
     * @param value the object to add
     * @throws IllegalArgumentException if the name or value parameters are
     * null, or if there is already a named value in the model
     */
    public void addModel(String name, Object value) {
        if (name == null) {
            String msg = "Cannot add null parameter name to "
                         + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "Cannot add null " + name + " parameter "
                         + "to " + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (getModel().containsKey(name)) {
            String msg = getClass().getName() + " model already contains "
                         + "value named " + name;
            throw new IllegalArgumentException(msg);
        } else {
            getModel().put(name, value);
        }
    }

    /**
     * Return the panels model map. The model is used populate the
     * Template Context with is merged with the panel template before rendering.
     *
     * @return the Page's model map
     */
    public Map getModel() {
        if (model == null) {
             model = new HashMap();
        }
        return model;
    }

    /**
     * Return the list of sub panels associated with this panel. Do not
     * add sub panels using this method, use {@link #add(Control)} instead.
     *
     * @return the list of sub-panels, if any
     */
    public List getPanels() {
        if (panels == null) {
            panels = new ArrayList();
        }
        return panels;
    }

    /**
     * Return the path of the template to render.
     *
     * @return the path of the template to render
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the path of the template to render.
     *
     * @param template the path of the template to render
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the HTML string representation of the Panel. The panel will be
     * rendered by merging the {@link #template} with the template
     * model. The template model is created using {@link #createTemplateModel()}.
     * <p/>
     * If a Panel template is not defined, a template based on the classes
     * name will be loaded. For more details please see {@link Context#renderTemplate(Class, Map)}.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        Context context = getContext();

        if (getTemplate() != null) {
            buffer.append(context.renderTemplate(getTemplate(), createTemplateModel()));

        } else {
            buffer.append(context.renderTemplate(getClass(), createTemplateModel()));
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Create a model to merge with the template. The model will
     * include the pages model values, plus any Panel defined model values, and
     * a number of automatically added model values. Note panel model values
     * will override any page defined model values.
     * <p/>
     * The following values automatically added to the Model:
     * <ul>
     * <li>attributes - the panel HTML attributes map</li>
     * <li>context - the Servlet context path, e.g. /mycorp</li>
     * <li>format - the page {@link Format} object for formatting the display of objects</li>
     * <li>this - a reference to this panel</li>
     * <li>messages - the panel messages bundle</li>
     * <li>request - the servlet request</li>
     * <li>response - the servlet request</li>
     * <li>session - the {@link SessionMap} adaptor for the users HttpSession</li>
     * </ul>
     *
     * @return a new model to merge with the template.
     */
    protected Map createTemplateModel() {

        Context context = getContext();

        final HttpServletRequest request = context.getRequest();

        final Page page = ClickUtils.getParentPage(this);

        final Map renderModel = new HashMap(page.getModel());

        renderModel.putAll(getModel());

        if (hasAttributes()) {
            renderModel.put("attributes", getAttributes());
        } else {
            renderModel.put("attributes", Collections.EMPTY_MAP);
        }

        renderModel.put("this", this);

        renderModel.put("context", request.getContextPath());

        Format format = page.getFormat();
        if (format != null) {
            renderModel.put("format", format);
        }

        Map templateMessages = new HashMap(getMessages());
        templateMessages.putAll(page.getMessages());
        renderModel.put("messages", templateMessages);

        renderModel.put("request", request);

        renderModel.put("response", context.getResponse());

        renderModel.put("session", new SessionMap(request.getSession(false)));

        return renderModel;
    }

}
