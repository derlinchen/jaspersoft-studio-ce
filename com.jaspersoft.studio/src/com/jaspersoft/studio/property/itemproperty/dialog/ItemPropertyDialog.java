/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.property.itemproperty.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.property.infoList.ElementDescription;
import com.jaspersoft.studio.property.infoList.SelectableComposite;
import com.jaspersoft.studio.property.itemproperty.desc.ADescriptor;
import com.jaspersoft.studio.utils.Misc;
import com.jaspersoft.studio.widgets.framework.IPropertyEditor;
import com.jaspersoft.studio.widgets.framework.PropertyEditorAdapter;
import com.jaspersoft.studio.widgets.framework.WItemProperty;
import com.jaspersoft.studio.widgets.framework.manager.ItemPropertyLayoutData;
import com.jaspersoft.studio.widgets.framework.ui.ItemPropertyDescription;
import com.jaspersoft.studio.widgets.framework.ui.TextPropertyDescription;

import net.sf.jasperreports.components.items.ItemProperty;
import net.sf.jasperreports.components.items.StandardItemProperty;
import net.sf.jasperreports.eclipse.ui.util.PersistentLocationTitleAreaDialog;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;

/**
 * Dialog that allows editing the information associated to a {@link ItemProperty} element.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 * 
 */
public class ItemPropertyDialog extends PersistentLocationTitleAreaDialog {

	private ItemPropertyDescription<?> ipDesc;
	
	private String propertyName;
	
	private String staticValue;
	
	private JRExpression expressionValue;
	
	private WItemProperty itemProperty;
	
	protected ExpressionContext context;
	
	protected boolean isExpressionMode = false;
	
	private ADescriptor descriptor;
	
	private Text propertyNameText;
	
	private Composite dialogArea;
	
	private IPropertyEditor internalEditor = new PropertyEditorAdapter() {

		public void createUpdateProperty(String propertyName, String value, JRExpression valueExpression) {
			expressionValue = valueExpression;
			staticValue = value;
			validateDialog();
		};
		
		@Override
		public JRExpression getPropertyValueExpression(String propertyName) {
			return getExpressionValue();
		}

		@Override
		public String getPropertyValue(String propertyName) {
			return getStaticValue();
		}
	};

	public ItemPropertyDialog(Shell parentShell, ItemProperty handledProperty, ADescriptor descriptor, ExpressionContext context) {
		super(parentShell);
		setSaveSettings(false);
		setDefaultSize(450, 350);
		this.context = context;
		if (handledProperty != null){
			staticValue = handledProperty.getValue();
			expressionValue = handledProperty.getValueExpression();
			propertyName = handledProperty.getName();
		} else {
			staticValue = "";
			expressionValue = null;
			propertyName = "";
		}
		this.isExpressionMode = expressionValue != null;
		this.descriptor = descriptor;	
		ItemPropertyDescription<?> ipDesc = descriptor.getDescription(propertyName);
		if (ipDesc == null)
			this.ipDesc = new TextPropertyDescription<String>(propertyName, "", false);
		else
			this.ipDesc = ipDesc.clone();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.ItemPropertyElementDialog_shellTitle);
	}
	
	protected WItemProperty createProperty(Composite parent, ItemPropertyDescription<?> idDesc, IPropertyEditor editor){
		return new WItemProperty(parent, SWT.NONE, ipDesc, editor){
			@Override
			public boolean isExpressionMode() {
				return isExpressionMode;
			}
		};
	}
	
	public JRExpression getExpressionValue(){
		return expressionValue;
	};
	
	public String getStaticValue(){
		return staticValue;
	}
	
	public StandardItemProperty getValue(){
		return new StandardItemProperty(propertyName, getStaticValue(), getExpressionValue());
	}
	
	@Override
	public boolean close() {
		descriptor.setOldItemProperty(null);
		if (isExpressionMode){
			staticValue = null;
			//if the user deosn't set an expression create it anyway
			if (expressionValue == null){
				expressionValue = new JRDesignExpression();
			}
		} else {
			expressionValue = null;
		}
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.ItemPropertyDialog_EditItemProperty);
		setMessage("Define the value of the new property");
		dialogArea = new Composite(parent, SWT.NONE);
		dialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		dialogArea.setLayout(layout);
		
		Label lblPropertyName = new Label(dialogArea, SWT.NONE);
		lblPropertyName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblPropertyName.setText(Messages.ItemPropertyDialog_PropertyName);
		
		propertyNameText = new Text(dialogArea, SWT.BORDER);
		propertyNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		propertyNameText.setText(propertyName);

		Button useExpressionCheckbox = new Button(dialogArea, SWT.CHECK);
		useExpressionCheckbox.setText(Messages.ItemPropertyElementDialog_2);
		useExpressionCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		useExpressionCheckbox.setSelection(isExpressionMode);
		useExpressionCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isExpressionMode = ((Button)e.widget).getSelection();
				itemProperty.updateWidget();
				validateDialog();
			}
		});
		
		Label lblPropertyValue = new Label(dialogArea, SWT.NONE);
		lblPropertyValue.setText(Messages.ItemPropertyDialog_PropertyValue);
		lblPropertyValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		itemProperty = createProperty(dialogArea, ipDesc, internalEditor);
		itemProperty.setLayoutData(new GridData(GridData.FILL_BOTH));
		ItemPropertyLayoutData contentLayout = new ItemPropertyLayoutData();
		contentLayout.expressionFillVertical = true;
		itemProperty.setContentLayoutData(contentLayout);
		itemProperty.setExpressionContext(context);

		List<ElementDescription> hints = getPropertiesInformation();
		if (!hints.isEmpty()){
			final SelectableComposite infoPanel = new SelectableComposite(dialogArea);
			infoPanel.setItems(hints);
			GridData infoGD = new GridData(SWT.FILL, SWT.FILL, true, true);
			infoGD.heightHint = 200;
			infoGD.verticalIndent = 5;
			infoPanel.setLayoutData(infoGD);
			infoPanel.SetDoubleClickListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String newname = infoPanel.getSelectedElement().getName();
					ItemPropertyDescription<?> ipDescNew = descriptor.getDescription(newname);
					if (ipDescNew != null) {
						propertyName = newname;
						staticValue = ipDescNew.getDefaultValueString();
						expressionValue = null;
						ItemPropertyDescription<?> ipDesc = descriptor.getDescription(propertyName);
						rebuildWidget(ipDesc);
					}
				}
			});
		}
		
		itemProperty.updateWidget();
		addListeners();
		return dialogArea;
	}
	
	protected void rebuildWidget(ItemPropertyDescription<?> ipDesc){
		itemProperty.dispose();
		if (ipDesc == null) {
			ItemPropertyDialog.this.ipDesc = new TextPropertyDescription<String>(propertyName, "", false);
		} else {
			ItemPropertyDialog.this.ipDesc = ipDesc.clone();
		}
		itemProperty = createProperty(dialogArea, ItemPropertyDialog.this.ipDesc, internalEditor);
		itemProperty.setLayoutData(new GridData(GridData.FILL_BOTH));
		ItemPropertyLayoutData contentLayout = new ItemPropertyLayoutData();
		contentLayout.expressionFillVertical = true;
		itemProperty.setContentLayoutData(contentLayout);
		itemProperty.setExpressionContext(context);
		dialogArea.layout();
		itemProperty.updateWidget();
	}

	private void addListeners() {
		propertyNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Point p = propertyNameText.getSelection();
				propertyName = propertyNameText.getText();

				ItemPropertyDescription<?> ipDesc = descriptor.getDescription(propertyName);
				rebuildWidget(ipDesc);

				propertyNameText.setSelection(p);
				validateDialog();
			}
		});
	}
	
	/**
	 * Need to do the validation after the contents are created, 
	 * otherwise the button will not be correctly disabled
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control cmp = super.createContents(parent);
		validateDialog();
		return cmp;
	}

	private List<ElementDescription> getPropertiesInformation() {
		List<ElementDescription> descriptions = new ArrayList<ElementDescription>();
		for (ItemPropertyDescription<?> ipd : descriptor.getItemPropertyDescriptors())
			descriptions.add(new ElementDescription(ipd.getName(), ipd.getDescription(), false));
		return descriptions;
	}

	protected void validateDialog() {
		Button ok = getButton(IDialogConstants.OK_ID);
		String str = null;
		try {
			descriptor.validateItem(getValue());
		} catch (Exception e) {
			str = e.getMessage();
		}
		if (Misc.isNullOrEmpty(str)){
			setErrorMessage(null);
			if (ok != null){
				ok.setEnabled(true);
			}
		} else {
			setErrorMessage(str);
			if (ok != null){
				ok.setEnabled(false);
			}
		}
		
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.PRIMARY_MODAL);
		setBlockOnOpen(true);
	}
}
