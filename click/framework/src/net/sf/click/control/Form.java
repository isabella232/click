/*
 * Copyright 2004-2005 Malcolm A. Edgar
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
package net.sf.click.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a Form control: &nbsp; &lt;form method='POST'&gt;.
 *
 * <table class='htmlHeader'>
 * <tr>
 * <td align='left'><label>Username</label><span class="red">*</span></td>
 * <td align='left'><input type='text' name='username' value='' size='20'   maxlength='20' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Password</label><span class="red">*</span></td>
 * <td align='left'><input type='password' name='password' value='' size='20'   maxlength='20' /></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'>
 * <input type='submit' name='ok' value='  OK  '/><input type='submit' name='cancel' value=' Cancel '/></td>
 * </tr>
 * </table>
 *
 * When a Form is processed it will process its {@link Field} controls
 * in the order they were added to the form, and then it will process the
 * {@link Button} controls in the added order. Once all the Fields have been
 * processed the form will invoke its action listener if defined.
 * <p/>
 * The example below illustrates a Form being used in a login Page.
 *
 * <div class="code">public class Login extends Page {
 *
 *     Form form;
 *     TextField usernameField;
 *     PasswordField passwordField;
 *
 *     public void onInit() {
 *         form = new Form("<span class='blue'>form</span>", getContext());
 *         addControl(form);
 *
 *         usernameField = new TextField("Username");
 *         usernameField.setMaxLength(20);
 *         usernameField.setMinLength(5);
 *         usernameField.setRequired(true);
 *         form.add(usernameField);
 *
 *         passwordField = new PasswordField("Password");
 *         passwordField.setMaxLength(20);
 *         passwordField.setMinLength(5);
 *         passwordField.setRequired(true);
 *         form.add(passwordField);
 *
 *         Submit okButton = new Submit("  OK  ");
 *         okButton.setListener(this, "onOkClick");
 *         form.add(okButton);
 *
 *         Submit cancelButton = new Submit(" Cancel ");
 *         cancelButton.setListener(this, "onCancelClick");
 *         form.add(cancelButton);
 *     }
 *
 *     public boolean onOkClick() {
 *         if (form.isValid()) {
 *             String username = usernameField.getValue();
 *             String password = passwordField.getValue();
 *
 *             User user = UserDatabase.getUser(username);
 *
 *             if (user != null && user.getPassword().equals(password)) {
 *                 getContext().setSessionAttribute("user", user);
 *                 setRedirect("home.htm");
 *             }
 *             else {
 *                  form.setError(getMessage("authentication-error")); 
 *             }
 *         }
 *         return true;
 *     }
 *
 *     public boolean onCancelClick() {
 *         setRedirect("index.htm");
 *         return false;
 *     }
 * }
 * </div>
 *
 * The forms corresponding template code is below. Note the form automatically
 * renders itself when Velocity invokes its {@link #toString()} method.
 *
 * <div class="code">
 * <span class="blue">$form</span>
 * </div>
 *
 * If a Form has been posted and processed, if it has an {@link #error} defined or
 * any of its Fields hava validation errors they will be automatically
 * rendered, and the {@link #isValid()} method will return false.
 * 
 * <a name="manual-layout"><h4>Manual Layout</h4></a>
 * 
 * You can also manually layout the Form in the page template specifying
 * the fields using the named field notation:
 *
 * <div class="code">
 * $form.{@link #fields}.usernameField
 * </div>
 *
 * Whenever including your own Form markup in a page template or Velocity macro,
 * always specify the form {@link #method} and include a hidden field which
 * specifies the {@link #name} of the Form. For example:
 *
 * <div class="code">
 * &lt;form method="<span class="green">POST</span>" name="<span class="green">form</span>" action="<span class="blue">$request.requestURI</span>&gt;
 *   &lt;input type="<span class="green">hidden</span>" name="<span class="green">form_name</span>" value="<span class="green">form</span>"/&gt; 
 *   Username: $form.fields.usernameField &lt;br/&gt;
 *   Password: $form.fields.passwordField &lt;br/&gt;
 *   $form.fields.okSubmit
 * &lt;form&gt;
 * </div>
 * 
 * <h4>Velocity Macros</h4>
 * 
 * Velocity 
 * <a target="topic" href="../../../../../velocity/user-guide.html#Velocimacros">Macros</a>
 * are a great way to encapsulate custom forms.
 * 
 * <div class="code">
 * 
 * #** Custom Form Macro Code **#
 * #macro( <span class="blue">writeForm</span>[$form] )
 *  &lt;form method='POST' name='$form.name' action='$request.requestURI'&gt;
 *   &lt;input name='form_name' type='hidden' value='$form.name)'/&gt;
 *   ..
 *  &lt;/form&gt;
 * 
 * #end
 * 
 * &lt;-- HTML Code --&gt;
 * #<span class="blue">writeForm</span>($form)
 * 
 * </div>
 * 
 * To create a generic form layout you can use the Form {@link #fieldList} and 
 * {@link #buttonList} properties.
 *
 * <div class="code">
 * &lt;form method="POST" name="form" action="<span class="blue">$request.requestURI</span>"&gt;
 * &lt;input type="hidden" name="form_name" value="form"/&gt;
 * &lt;table width="100%"&gt;
 * <span class="red">#foreach (</span><span class="blue">$field</span> <span class="red">in </span><span class="blue">$form.fieldList</span><span class="red">)</span>
 *   <span class="red">#if( !</span><span class="blue">$field.isValid()</span> <span class="red">)</span>
 *   &lt;tr&gt;
 *     &lt;td colspan="2"&gt; <span class="blue">$field.error</span> &lt;/td&gt;
 *   &lt;/tr&gt;
 *   <span class="red">#end</span>
 *   &lt;tr&gt;
 *     &lt;td&gt; <span class="blue">$field.label</span> &lt;/td&gt; &lt;td&gt; $<span class="blue">field</span> &lt;/td&gt;
 *   &lt;/tr&gt;
 * <span class="red">#end</span>
 *  &lt;tr&gt;
 *    &lt;td colspan="2"&gt;
 *    <span class="red">#foreach (</span><span class="blue">$button</span> <span class="red">in </span><span class="blue">$form.buttonList</span><span class="red">)</span>
 *      <span class="blue">$button</span> &amp;nbsp;
 *    <span class="red">#end</span>
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 * &lt;/form&gt;
 * </div>
 *
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.3">FORM</a>
 *
 * @see Field
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class Form implements Control {

    // ------------------------------------------------------- Static Variables

    /**
     * The form name parameter for multiple forms: &nbsp; <tt>"form_name"</tt>
     */
    public static final String FORM_NAME = "form_name";

    /** The errors and labels on top form layout constant. */
    public static final int TOP = 10;

    /** The errors in middle form layout constant. */
    public static final int MIDDLE = 11;

    /** The errors on bottom form layout constant. */
    public static final int BOTTOM = 12;

    /** The labels of left form layout contant. */
    public static final int LEFT = 13;

    /** The errors-header resource property */
    protected static String errorsHeader = "";

    /** The errors-footer resource property. */
    protected static String errorsFooter = "";

    /** The errors-prefix resource property. */
    protected static String errorsPrefix = "";

    /** The errors-suffix resource property. */
    protected static String errorsSuffix = "";

    /** The label-required-prefix resource property. */
    protected static String labelRequiredPrefix = "";

    /** The label-required-suffix resource property. */
    protected static String labelRequiredSuffix = "";

    static {
        ResourceBundle bundle =
            ResourceBundle.getBundle(Field.PACKAGE_MESSAGES);

        errorsHeader = bundle.getString("errors-header");
        errorsFooter = bundle.getString("errors-footer");
        errorsPrefix = bundle.getString("errors-prefix");
        errorsSuffix = bundle.getString("errors-suffix");
        labelRequiredPrefix = bundle.getString("label-required-prefix");
        labelRequiredSuffix = bundle.getString("label-required-suffix");
    }

    // ----------------------------------------------------- Instance Variables

    /** The form attributes map. */
    protected Map attributes;

    /** The ordered list of button values. */
    protected final List buttonList = new ArrayList(5);

    /** The form context. */
    protected Context context;

    /** The form level error message. */
    protected String error;

    /**
     * The form errors position <tt>[TOP, MIDDLE, BOTTOM]</tt> default value:
     * &nbsp; <tt>MIDDLE</tt>
     */
    protected int errorsPosition = MIDDLE;

    /** The ordered list of field values, excluding buttons */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The JavaScript enabled flag, defaults value is true. */
    protected boolean jsEnabled = true;

    /** The label align, default value is "<tt>left</tt>" */
    protected String labelAlign = "left";

    /**
     * The form labels position <tt>[LEFT, TOP]</tt> default value: &nbsp; <tt>LEFT</tt>
     */
    protected int labelsPosition = LEFT;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /**
     * The form method <tt>["POST, "GET"]</tt>, default value: &nbsp;
     * <tt>POST</tt>
     */
    protected String method = "POST";

    /** The form name. */
    protected String name;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a form with the given name and context.
     *
     * @param name the name of the form
     * @param context the form command context
     * @throws IllegalArgumentException if the form name or command is null.
     */
    public Form(String name, Context context) {
        setName(name);
        setContext(context);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the field to the form, and set the fields form property. The field
     * will be added to the {@link #fields} Map using its name.
     * <p/>
     * Button instances will also be added to the {@link #buttonList} while
     * all others field types will also be added to the {@link #fieldList}.
     *
     * @param field the field to add to the form.
     * @throws IllegalArgumentException if the form already contains a field
     * or button with the same name as the added field
     */
    public void add(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field parameter cannot be null");
        }
        if (fields.containsKey(field.getName()) && !(field instanceof Label)) {
            throw new IllegalArgumentException
                ("Form already contains field named: " + field.getName());
        }

        if (field instanceof Button) {
            buttonList.add(field);
        } else {
            fieldList.add(field);
        }
        fields.put(field.getName(), field);
        field.setForm(this);
        field.setContext(getContext());
    }

    /**
     * Remove the given field from the form.
     *
     * @param field the field to remove from the form
     */
    public void remove(Field field) {
        if (field != null && fields.containsKey(field.getName())) {
            field.setForm(null);
            fields.remove(field.getName());
            if (field instanceof Button) {
                buttonList.remove(field);
            } else {
                fieldList.remove(field);
            }
        }
    }

    /**
     * Return the link HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of link HTML attribute
     * @return the link HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the form HTML attribute with the given attribute name and value..
     *
     * @param name the name of the form HTML attribute
     * @param value the value of the form HTML attribute
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (attributes == null) {
            attributes = new HashMap(5);
        }

        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }


    /**
     * Return the form attributes Map.
     *
     * @return the form attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the form has attributes or false otherwise.
     *
     * @return true if the form has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the ordered list of {@link Button}s.
     *
     * @return the ordered list of {@link Button}s.
     */
    public List getButtonList() {
        return buttonList;
    }

    /**
     * @see Control#getContext()
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;
    }

    /**
     * Return the form level error message.
     *
     * @return the form level error message
     */
    public String getError() {
        return error;
    }

    /**
     * Set the form level validation error message. If the error message is not
     * null the form is invalid.
     *
     * @param error the validation error message
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Return the form errors position <tt>[TOP, MIDDLE, BOTTOM]</tt>.
     *
     * @return form errors position
     */
    public int getErrorsPosition() {
        return errorsPosition;
    }

    /**
     * Set the form errors position <tt>[TOP, MIDDLE, BOTTOM]</tt>.
     *
     * @param position the form errors position
     */
    public void setErrorsPosition(int position) {
        if (position != TOP && position != MIDDLE && position != BOTTOM) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        errorsPosition = position;
    }

    /**
     * Return the named field if contained in the form, or null if not found.
     *
     * @param name the name of the field
     * @return the named field if contained in the form
     */
    public Field getField(String name) {
        return (Field) fields.get(name);
    }

    /**
     * Return the List of form fields, ordered in addition order to the form.
     *
     * @return the ordered List of form fields
     */
    public List getFieldList() {
        return fieldList;
    }

    /**
     * Return the Map of form fields, keyed on field name.
     *
     * @return the Map of form fields, keyed on field name
     */
    public Map getFields() {
        return fields;
    }

    /**
     * Return the field value for the named field, or null if the field is not
     * found.
     *
     * @param name the name of the field
     * @return the field value for the named field
     */
    public String getFieldValue(String name) {
        Field field = getField(name);
        if (field != null) {
            return field.getValue();
        } else {
            return null;
        }
    }

    /**
     * Return the HTML head import statement for the CSS stylesheet
     * click/form.css. If JavaScript is enabled a import statement for
     * click/form.js will also be included.
     *
     * @see #jsEnabled
     *
     * @return the HTML head import statements for the form stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String contextPath = context.getRequest().getContextPath();

        if (jsEnabled) {

            return "<link rel='stylesheet' type='text/css' href='"
                   + contextPath + "/click/form.css' title='style'>\n"
                   + "<script type='text/javascript' "
                   + "src='" + contextPath + "/click/form.js'></script>";

        } else {

            return "<link rel='stylesheet' type='text/css' href='"
                   + contextPath + "/click/form.css' title='style'>";

        }
    }

    /**
     * Return true if JavaScript is enabled.
     *
     * @return true if JavaScript is enabled
     */
    public boolean getJSEnabled() {
        return jsEnabled;
    }

    /**
     * Set whether JavaScript is enabled.
     *
     * @param enabled sets whether JavaScript is enabled
     */
    public void setJSEnabled(boolean enabled) {
        jsEnabled = enabled;
    }

    /**
     * Return the field label HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>".
     *
     * @return the field label HTML horizontal alignment
     */
    public String getLabelAlign() {
        return labelAlign;
    }

    /**
     * Set the field label HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>". Note the given align is not
     * validated.
     *
     * @param align the field label HTML horizontal alignment
     */
    public void setLabelAlign(String align) {
        labelAlign = align;
    }

    /**
     * Return the form labels position <tt>[LEFT, TOP]</tt>.
     *
     * @return form labels position
     */
    public int getLabelsPosition() {
        return labelsPosition;
    }

    /**
     * Set the form labels position <tt>[LEFT, TOP]</tt>.
     *
     * @param position the form labels position
     */
    public void setLabelsPosition(int position) {
        if (position != LEFT && position != TOP) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        labelsPosition = position;
    }

    /**
     * The callback listener will only be called during processing if the field
     * value is valid. If the field has validation errors the listener will not
     * be called.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     */
    public void setListener(Object target, String methodName) {
        listener = target;
        listenerMethod = methodName;
    }
 
    /**
     * Return the form method <tt>["POST" | "GET"]</tt>
     *
     * @return the form method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the form method <tt>["POST" | "GET"]</tt>
     *
     * @param value the form method
     */
    public void setMethod(String value) {
        method = value;
    }

    /**
     * Return the name of the form.
     *
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the form.
     *
     * @see net.sf.click.Control#setName(String)
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
    }

    /**
     * Return true if the fields are valid and there is no form level error,
     * otherwise return false.
     *
     * @return true if the fields are valid and there is no form level error
     */
    public boolean isValid() {
        if (getError() != null) {
            return false;
        }
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);
            if (!field.isValid()) {
                return false;
            }
        }
        return true;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the Form when the Context request method is the same as the Forms,
     * by default "POST" method.
     * <p/>
     * The Forms processing order is:<ol>
     * <li>All {@link Field} controls in the order they were added</li>
     * <li>All {@link Button} controls in the order they were added</li>
     * <li>Invoke the Forms listener if defined</li>
     * </ol>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        HttpServletRequest request = getContext().getRequest();

        if (request.getMethod().equalsIgnoreCase(getMethod())) {
            // If a form name is defined, but does not match this form exit.
            String formName = request.getParameter(FORM_NAME);
            if (formName != null && !formName.equals(name)) {
                return true;
            }

            boolean continueProcessing = true;
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                continueProcessing = field.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                continueProcessing = button.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);
            }
        }

        return true;
    }

    /**
     * Return the HTML string representation of the form.
     * <p/>
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @see Object#toString()
     */
    public String toString() {
        final boolean process =
            context.getRequest().getMethod().equalsIgnoreCase(getMethod());

        // Estimate the size of the string buffer
        int bufferSize =
            400 + (fieldList.size() * 350) + (buttonList.size() * 50);

        StringBuffer buffer = new StringBuffer(bufferSize);

        buffer.append("<form method='");
        buffer.append(getMethod());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' action='");
        buffer.append(getContext().getRequest().getRequestURI());
        buffer.append("'");
        if (hasAttributes()) {
            ClickUtils.renderAttributes(getAttributes(), buffer);
        }
        buffer.append(">\n");

        buffer.append("<input name='form_name' type='hidden' value='");
        buffer.append(getName());
        buffer.append("'>\n");

        int hiddenCount = 0;
        Field fieldWithError = null;

        // Render fields, errors and buttons
        switch (getErrorsPosition()) {
        case TOP:
            fieldWithError = renderErrors(buffer, process);
            hiddenCount = renderFields(buffer);
            renderButtons(buffer);
            break;
        case MIDDLE:
            hiddenCount = renderFields(buffer);
            fieldWithError = renderErrors(buffer, process);
            renderButtons(buffer);
            break;
        case BOTTOM:
            hiddenCount = renderFields(buffer);
            renderButtons(buffer);
            fieldWithError = renderErrors(buffer, process);
            break;
        default:
            throw new IllegalArgumentException("Invalid errorsPositon");
        }

        // Render hidden fields
        if (hiddenCount > 0) {
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (field.isHidden()) {
                    buffer.append(field);
                    buffer.append("\n");
                    hiddenCount--;
                    if (hiddenCount == 0) {
                        break;
                    }
                }
            }
        }

        buffer.append("</form>\n");

        // Set field focus
        if (fieldWithError != null) {
            buffer.append("<script type='text/javascript'><!--\n");
            buffer.append("document.forms['");
            buffer.append(getName());
            buffer.append("'].elements['");
            buffer.append(fieldWithError.getName());
            buffer.append("'].focus();\n");
            buffer.append("//--></script>\n");

        } else {
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (field.getFocus() && !field.isHidden() && !field.isDisabled()) {
                    buffer.append("<script type='text/javascript'><!--\n");
                    buffer.append("document.forms['");
                    buffer.append(getName());
                    buffer.append("'].elements['");
                    buffer.append(field.getName());
                    buffer.append("'].focus();\n");
                    buffer.append("//--></script>\n");
                    break;
                }
            }
        }

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the non hidden Form Fields to the string buffer and return a
     * count of hidden fields.
     *
     * @param buffer the StringBuffer to render to
     * @return the number of hidden Fields
     */
    protected int renderFields(StringBuffer buffer) {
        int hiddenCount = 0;

        buffer.append("<table class='fields'>\n");

        for (int i = 0, size = fieldList.size(); i < size; i++) {

            Field field = (Field) fieldList.get(i);

            if (!field.isHidden()) {

                if (field instanceof Label) {
                    buffer.append("<tr><td colspan='2' align='");
                    buffer.append(getLabelAlign());
                    buffer.append("'");
                    if (field.hasAttributes()) {
                        ClickUtils.renderAttributes
                            (field.getAttributes(), buffer);
                    }
                    buffer.append(">");
                    buffer.append(field);
                    buffer.append("</td></tr>\n");

                } else {
                    buffer.append("<tr>\n");

                    // Write out label
                    if (getLabelsPosition() == LEFT) {
                        buffer.append("<td align='");
                        buffer.append(getLabelAlign());
                        buffer.append("'>");
                    } else {
                        buffer.append("<td valign='top'>");
                    }

                    if (field.isRequired()) {
                        buffer.append(labelRequiredPrefix);
                    }
                    buffer.append("<label");
                    buffer.append(field.getDisabled());
                    buffer.append(">");
                    buffer.append(field.getLabel());
                    buffer.append("</label>");
                    if (field.isRequired()){
                        buffer.append(labelRequiredSuffix);
                    }

                    if (getLabelsPosition() == LEFT) {
                        buffer.append("</td>\n");
                        buffer.append("<td align='left'>");
                    } else {
                        buffer.append("<br>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                    buffer.append("</tr>\n");
                }

            } else {
                hiddenCount++;
            }
        }
        buffer.append("</table>\n");

        return hiddenCount;
    }

    /**
     * Render the form errors to the given buffer is form processed and
     * return the first field with an error if processed.
     *
     * @param buffer the string buffer to render the errors to
     * @param processed the flag indicating whether has been processed
     * @return the first field with an error if the form is being processed
     */
    protected Field renderErrors(StringBuffer buffer, boolean processed) {

        Field fieldWithError = null;
        if (processed && !isValid()) {

            String headerTest = errorsHeader.toLowerCase() +
                                errorsPrefix.toLowerCase();
            boolean useErrorsHeader =
                (((headerTest.indexOf("<ul") > -1) ||
                  (headerTest.indexOf("<ol") > -1)) &&
                  (headerTest.indexOf("<li") > -1));

            if (useErrorsHeader) {
                buffer.append(errorsHeader);
                buffer.append("\n");
            } else {
                buffer.append("<table class='errors'>\n");
            }

            if (getError() != null) {
                if (useErrorsHeader) {
                    buffer.append(errorsPrefix);
                } else {
                    buffer.append("<tr><td>");
                }
                buffer.append("<span class='error'>");
                buffer.append(getError());
                buffer.append("</span>\n");
                if (useErrorsHeader) {
                    buffer.append(errorsSuffix);
                    buffer.append("\n");
                } else {
                    buffer.append("</td></tr>\n");
                }
            }

            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (!field.isValid() && !field.isHidden()) {
                    if (fieldWithError == null && !field.isDisabled()) {
                        fieldWithError = field;
                    }
                    if (useErrorsHeader) {
                        buffer.append(errorsPrefix);
                    } else {
                        buffer.append("<tr><td>");
                    }
                    if (jsEnabled) {
                        buffer.append("<a class='error'");
                        buffer.append(" href='javascript:document.");
                        buffer.append(getName());
                        buffer.append(".");
                        buffer.append(field.getName());
                        buffer.append(".focus();'>");
                        buffer.append(field.getError());
                        buffer.append("</a>");
                    } else {
                        buffer.append("<span class='error'>");
                        buffer.append(field.getError());
                        buffer.append("</span>");
                    }
                    if (useErrorsHeader) {
                        buffer.append(errorsSuffix);
                        buffer.append("\n");
                    } else {
                        buffer.append("</td></tr>\n");
                    }
                }
            }

            if (useErrorsHeader) {
                buffer.append(errorsFooter);
                buffer.append("\n");
            } else {
                buffer.append("</table>\n");
            }
        }

        return fieldWithError;
    }

    /**
     * Render the Form Buttons to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderButtons(StringBuffer buffer) {
        if (!buttonList.isEmpty()) {
            buffer.append("<table class='buttons'>\n");
            buffer.append("<tr><td>");
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                buffer.append(button);
            }
            buffer.append("</td></tr>\n");
            buffer.append("</table>\n");
        }
    }
}

