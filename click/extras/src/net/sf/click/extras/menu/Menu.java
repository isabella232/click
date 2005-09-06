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
package net.sf.click.extras.menu;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.click.Context;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides hierarchical Menu component. Application menus can be defined
 * using a <tt>/WEB-INF/menu.xml</tt> configuration file.

 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Menu implements Serializable {

    private static final long serialVersionUID = 5820272228903777866L;

    private static final Object loadLock = new Object();

    /**
     * The menu configuration filename: &nbsp; "<tt>/WEB-INF/menu.xml</tt>"
     */
    protected static final String CONFIG_FILE = "/WEB-INF/menu.xml";

    /** The cached root Menu as defined in <tt>menu.xml</tt> */
    protected static Menu rootMenu;

    // ----------------------------------------------------- Instance Variables

    /** The list of submenu items. */
    protected List children = new ArrayList();

    /** The menu display label. */
    protected String label;

    /** The menu path. */
    protected String path;

    /** The list of valid role names. */
    protected List roles = new ArrayList();

    /** The menu is selected flag. */
    protected boolean selected;

    /** The tooltip title attribute. */
    protected String title = "";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Menu instance.
     */
    public Menu() {
    }

    /**
     * Create a Menu from the given menu-item XML Element.
     *
     * @param menuElement the menu-item XML Element.
     */
    public Menu(Element menuElement) {
        if (menuElement == null) {
            throw new IllegalArgumentException("Null menuElement parameter");
        }

        setLabel(menuElement.getAttributeValue("label"));
        setPath(menuElement.getAttributeValue("path"));
        setTitle(menuElement.getAttributeValue("title", ""));

        String rolesValue = menuElement.getAttributeValue("roles");
        if (!StringUtils.isBlank(rolesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(rolesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                getRoles().add(tokenizer.nextToken().trim());
            }
        }

        List childElements = menuElement.getChildren("menu-item");
        for (int i = 0, size = childElements.size(); i < size; i++) {
            Element childElement = (Element) childElements.get(i);
            getChildren().add(new Menu(childElement));
        }
    }

    /**
     * Create a new Menu from the given menu. Provides a deep copy constructor.
     *
     * @param menu the menu to copy
     */
    public Menu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Null menu parameter");
        }
        setLabel(menu.getLabel());
        setPath(menu.getPath());
        setTitle(menu.getTitle());
        setRoles(menu.getRoles());

        for (int i = 0; i < menu.getChildren().size(); i++) {
            Menu menuChild = (Menu) menu.getChildren().get(i);
            getChildren().add(new Menu(menuChild));
        }
    }

    // ------------------------------------------------------ Public Attributes

    public List getChildren() {
        return children;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List getRoles() {
        return roles;
    }

    public void setRoles(List roles) {
        this.roles = roles;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return a copy of the Appliations root Menu as defined in the
     * configuration file "<tt>/WEB-INF/menu.xml</tt>".
     *
     * @param context the request context
     * @return a copy of the application's root Menu
     */
    public static synchronized Menu getRootMenu(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }

        synchronized(loadLock) {
            if (rootMenu == null) {
                Menu menu = new Menu();

                InputStream inputStream =
                    context.getServletContext().getResourceAsStream(CONFIG_FILE);

                if (inputStream == null) {
                    String msg = "could not find configuration file:" + CONFIG_FILE;
                    throw new RuntimeException(msg);
                }

                SAXBuilder saxBuilder = new SAXBuilder();

                try {
                    Document document = saxBuilder.build(inputStream);

                    Element rootElm = document.getRootElement();

                    List list = rootElm.getChildren("menu");

                    for (int i = 0; i < list.size(); i++) {
                        Element menuElm = (Element) list.get(i);
                        menu.getChildren().add(new Menu(menuElm));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                rootMenu = menu;
            }
        }

        Menu menu = new Menu(rootMenu);

        menu.select(context);

        return menu;
    }

    /**
     * Set the selected status of the menu and its children depending upon
     * the current context's path. If the path equals the menus path the
     * menu will be selected.
     *
     * @param context the request context
     */
    public void select(Context context) {
        if (getPath() != null) {
            selected = getPath().equals(context.getResourcePath());
        } else {
            selected = false;
        }

        for (int i = 0; i < getChildren().size(); i++) {
            Menu menu = (Menu) getChildren().get(i);
            menu.select(context);
        }
    }

}
