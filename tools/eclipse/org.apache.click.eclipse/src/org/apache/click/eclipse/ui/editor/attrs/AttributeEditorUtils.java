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
package org.apache.click.eclipse.ui.editor.attrs;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.ui.fieldassist.FieldAssistUtils;
import org.apache.click.eclipse.ui.fieldassist.TypeNameContentProposalProvider;
import org.apache.click.eclipse.ui.wizard.NewClassWizard;
import org.apache.click.eclipse.ui.wizard.NewClickPageWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.fieldassist.TextControlCreator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.fieldassist.ContentAssistField;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Element;

/**
 * Provides utility methods for attribute editors.
 *
 * @author Naoki Takezoe
 */
public class AttributeEditorUtils {

	public static Control[] createLinkText(FormToolkit toolkit, Composite parent,
			final IDOMElement element, String label, final String attrName){

		Hyperlink link = toolkit.createHyperlink(parent, label, SWT.NULL);

		Composite composite = FieldAssistUtils.createNullDecoratedPanel(parent, true);
		final Text text = toolkit.createText(composite, "", SWT.BORDER);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if(element != null){
			String initialValue = element.getAttribute(attrName);
			if(initialValue != null){
				text.setText(initialValue);
			}
		}
		return new Control[]{link, text};

	}

	/**
	 * Creates the {@link Text} field editor.
	 *
	 * @param toolkit the <code>FormToolkit</code> instance
	 * @param parent the parent composite
	 * @param element the target element
	 * @param label the field label
	 * @param attrName the target attribute name
	 */
	public static Text createText(FormToolkit toolkit, Composite parent,
			final IDOMElement element, String label, final String attrName){

		toolkit.createLabel(parent, label);

		Composite composite = FieldAssistUtils.createNullDecoratedPanel(parent, true);
		final Text text = toolkit.createText(composite, "", SWT.BORDER);

		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if(element != null){
			String initialValue = element.getAttribute(attrName);
			if(initialValue != null){
				text.setText(initialValue);
			}
		}
		return text;
	}

	/**
	 * Creates the {@Combo} field editor.
	 *
	 * @param toolkit the <code>FormToolkit</code>
	 * @param parent the parent composite
	 * @param element the target element
	 * @param label the field label
	 * @param attrName the target attribute name
	 * @param values proposals of the combobox
	 */
	public static Combo createCombo(FormToolkit toolkit, Composite parent,
			final IDOMElement element, String label, final String attrName, String[] values){

		toolkit.createLabel(parent, label);

		Composite composite = FieldAssistUtils.createNullDecoratedPanel(parent, true);
		final Combo combo = new Combo(composite, SWT.READ_ONLY);

		for(int i = 0; i < values.length; i++){
			combo.add(values[i]);
		}
		if(element != null){
			String initialValue = element.getAttribute(attrName);
			if(initialValue!=null){
				combo.setText(initialValue);
			}
		}

		return combo;
	}

	
	
	/**
	 * Creates the classname field editor.
	 *
	 * @param project the <code>IJavaProject</code> instance
	 * @param toolkit the <code>FormToolkit</code> instance
	 * @param parent the parent composite
	 * @param element the target element
	 * @param attrName the target attribute name
	 * @param superClass the super class
	 * @param textFileName null or the text control for the HTML filename
	 */
	public static Text createClassText(final IJavaProject project,
			FormToolkit toolkit, final Composite parent,
			final IDOMElement element, String label, final String attrName,
			final String superClass, final Text textFileName){

		// packagename of page class
		final String packageName = getPagePackageName(element, superClass);
		
		final Hyperlink link = toolkit.createHyperlink(parent, label, SWT.NULL);
		link.addHyperlinkListener(new HyperlinkAdapter(){
			public void linkActivated(HyperlinkEvent e){
				try {
					Control[] controls = parent.getChildren();
					Text text = null;
					for(int i = 0; i < controls.length; i++){
						if(i > 0 && controls[i-1] == link && controls[i] instanceof Composite){
							Composite composite = (Composite)controls[i];
							text = (Text)((Composite)composite.getChildren()[0]).getChildren()[0];
						}
					}
					if(text != null && !text.getText().equals("")){
						String className = text.getText();
						if(superClass != null && superClass == ClickPlugin.CLICK_PAGE_CLASS){
							if(ClickUtils.isNotEmpty(packageName)){
								className = packageName + "." + className;
							}
						}
						IFile file = (IFile)ClickUtils.getResource(element.getStructuredDocument());
						IJavaProject project = JavaCore.create(file.getProject());
						IType type = project.findType(className);
						if(type != null){
							JavaUI.revealInEditor(JavaUI.openInEditor(type), (IJavaElement)type);
						} else {
							if(superClass != null && superClass == ClickPlugin.CLICK_PAGE_CLASS){
								// Opens the new page creation wizard
								NewClickPageWizard wizard = new NewClickPageWizard();
								wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
								wizard.setInitialClassName(className);
								if(textFileName != null){
									wizard.setInitialPageName(textFileName.getText());
								}
								WizardDialog dialog = new WizardDialog(text.getShell(), wizard);
								dialog.open();
							} else {
								// Opens the new java class creation wizard
								NewClassWizard wizard = new NewClassWizard();
								wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
								if(superClass != null){
									if(superClass == ClickPlugin.CLICK_CONTROL_IF || superClass.endsWith("Service")){
										wizard.addInterface(superClass);
									} else {
										wizard.setSuperClass(superClass);
									}
								}
								wizard.setClassName(className);
								WizardDialog dialog = new WizardDialog(text.getShell(), wizard);
								dialog.open();
							}
						}
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		});


		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(FieldAssistUtils.createGridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		ContentAssistField field = new ContentAssistField(composite, SWT.BORDER,
				new TextControlCreator(), new TextContentAdapter(),
				new TypeNameContentProposalProvider(project, packageName),
				ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
				new char[0]);

		final Text text = (Text) field.getControl();
		field.getLayoutControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		field.getLayoutControl().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		if(element != null){
			String initialValue = element.getAttribute(attrName);
			if(initialValue!=null){
				text.setText(initialValue);
			}
		}

		Button button = toolkit.createButton(composite, ClickPlugin.getString("action.browse"), SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				Shell shell = text.getShell();
				try {
					SelectionDialog dialog = JavaUI.createTypeDialog(
							shell,new ProgressMonitorDialog(shell),
							SearchEngine.createJavaSearchScope(new IJavaElement[]{project}),
							IJavaElementSearchConstants.CONSIDER_CLASSES,false);

					if(dialog.open() == SelectionDialog.OK){
						Object[] result = dialog.getResult();
						String className = ((IType)result[0]).getFullyQualifiedName();
						if(ClickUtils.isNotEmpty(packageName) && className.startsWith(packageName)){
							className = className.substring(packageName.length() + 1);
						}
						text.setText(className);
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		});

		return text;
	}

	private static String getPagePackageName(final IDOMElement element, final String superClass) {
		String packageName = null;
		if(superClass != null && superClass == ClickPlugin.CLICK_PAGE_CLASS){
			packageName = ((Element) element.getParentNode()).getAttribute(ClickPlugin.ATTR_PACKAGE);
		}
		return packageName;
	}

}
