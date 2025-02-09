/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.group;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.editor.expression.ExpressionContext;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.APropertyNode;
import com.jaspersoft.studio.model.DefaultValue;
import com.jaspersoft.studio.model.ICopyable;
import com.jaspersoft.studio.model.IDragable;
import com.jaspersoft.studio.model.dataset.MDataset;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.IntegerPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.JSSValidatedTextPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.PixelPropertyDescriptor;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.base.JRBaseGroup;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.FooterPositionEnum;

/*
 * The Class MGroup.
 * 
 * @author Chicu Veaceslav
 */
public class MGroup extends APropertyNode implements ICopyable, IDragable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private static IPropertyDescriptor[] descriptors;

	private static NamedEnumPropertyDescriptor<FooterPositionEnum> positionD;

	private static GroupNameValidator validator;

	private static final Integer CONST_MIN_HEIGHT = new Integer(0);

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("group"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m group.
	 */
	public MGroup() {
		super();
	}

	/**
	 * Instantiates a new m group.
	 * 
	 * @param parent
	 *            the parent
	 * @param jfRield
	 *            the jf rield
	 * @param newIndex
	 *            the new index
	 */
	public MGroup(ANode parent, JRDesignGroup jfRield, int newIndex) {
		super(parent, newIndex);
		setValue(jfRield);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getDisplayText()
	 */
	public String getDisplayText() {
		return ((JRDesignGroup) getValue()).getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getImagePath()
	 */
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.INode#getToolTip()
	 */
	@Override
	public String getToolTip() {
		return getIconDescriptor().getToolTip();
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	@Override
	protected void postDescriptors(IPropertyDescriptor[] descriptors) {
		super.postDescriptors(descriptors);
		// Set into the validator the actual reference
		updateValidator();
	}

	/**
	 * Update the reference into the static validator when the actual group is
	 * edited
	 */
	public void updateValidator() {
		if (validator == null) {
			validator = new GroupNameValidator();
		}
		validator.setTargetNode(this);
	}

	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc
	 *            the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		updateValidator();
		JSSTextPropertyDescriptor nameD = new JSSValidatedTextPropertyDescriptor(JRDesignGroup.PROPERTY_NAME,
				Messages.common_name, validator);
		nameD.setDescription(Messages.MGroup_name_description);
		desc.add(nameD);

		JRExpressionPropertyDescriptor expressionD = new JRExpressionPropertyDescriptor(
				JRDesignGroup.PROPERTY_EXPRESSION, Messages.common_expression);
		expressionD.setDescription(Messages.MGroup_expression_description);
		desc.add(expressionD);

		PixelPropertyDescriptor minhD = new PixelPropertyDescriptor(JRBaseGroup.PROPERTY_MIN_HEIGHT_TO_START_NEW_PAGE,
				Messages.MGroup_minHeightTitle);
		minhD.setDescription(Messages.MGroup_minHeightDescription);
		desc.add(minhD);

		IntegerPropertyDescriptor minStartTopD = new IntegerPropertyDescriptor(
				JRBaseGroup.PROPERTY_MIN_DETAILS_TO_START_FROM_TOP, Messages.MGroup_1);
		minStartTopD.setDescription(Messages.MGroup_2);
		desc.add(minStartTopD);

		CheckBoxPropertyDescriptor stNewColD = new CheckBoxPropertyDescriptor(JRBaseGroup.PROPERTY_START_NEW_COLUMN,
				Messages.MGroup_newColTitle);
		stNewColD.setDescription(Messages.MGroup_newColDescription);
		desc.add(stNewColD);

		CheckBoxPropertyDescriptor stNewPageD = new CheckBoxPropertyDescriptor(JRBaseGroup.PROPERTY_START_NEW_PAGE,
				Messages.MGroup_newPageTitle);
		stNewPageD.setDescription(Messages.MGroup_newPageDescription);
		desc.add(stNewPageD);

		CheckBoxPropertyDescriptor stPrevOrfanFooterD = new CheckBoxPropertyDescriptor(
				JRBaseGroup.PROPERTY_PREVENT_ORPHAN_FOOTER, Messages.MGroup_0);
		stPrevOrfanFooterD.setDescription(Messages.MGroup_0);
		desc.add(stPrevOrfanFooterD);

		CheckBoxPropertyDescriptor rPageNumD = new CheckBoxPropertyDescriptor(JRBaseGroup.PROPERTY_RESET_PAGE_NUMBER,
				Messages.MGroup_pageNumberTitle);
		rPageNumD.setDescription(Messages.MGroup_pageNumberDescription);
		desc.add(rPageNumD);

		CheckBoxPropertyDescriptor rHeadEPD = new CheckBoxPropertyDescriptor(
				JRBaseGroup.PROPERTY_REPRINT_HEADER_ON_EACH_PAGE, Messages.MGroup_reprintTitle);
		rHeadEPD.setDescription(Messages.MGroup_reprintPosition);
		desc.add(rHeadEPD);

		CheckBoxPropertyDescriptor keepToD = new CheckBoxPropertyDescriptor(JRBaseGroup.PROPERTY_KEEP_TOGETHER,
				Messages.MGroup_keepTitle);
		keepToD.setDescription(Messages.MGroup_keepDescription);
		desc.add(keepToD);

		positionD = new NamedEnumPropertyDescriptor<>(JRBaseGroup.PROPERTY_FOOTER_POSITION,
				Messages.MGroup_footerPosTitle, FooterPositionEnum.COLLATE_AT_BOTTOM, NullEnum.NOTNULL);
		positionD.setDescription(Messages.MGroup_footerPosDescription);
		desc.add(positionD);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#group"); //$NON-NLS-1$
	}

	@Override
	protected Map<String, DefaultValue> createDefaultsMap() {
		Map<String, DefaultValue> defaultsMap = super.createDefaultsMap();

		defaultsMap.put(JRDesignGroup.PROPERTY_MIN_HEIGHT_TO_START_NEW_PAGE, new DefaultValue(CONST_MIN_HEIGHT, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_MIN_DETAILS_TO_START_FROM_TOP, new DefaultValue(0, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_KEEP_TOGETHER, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_REPRINT_HEADER_ON_EACH_PAGE, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_RESET_PAGE_NUMBER, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_START_NEW_COLUMN, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_START_NEW_PAGE, new DefaultValue(Boolean.FALSE, false));
		defaultsMap.put(JRDesignGroup.PROPERTY_PREVENT_ORPHAN_FOOTER, new DefaultValue(Boolean.FALSE, false));

		int footerPositionValue = NamedEnumPropertyDescriptor.getIntValue(FooterPositionEnum.NORMAL, NullEnum.NOTNULL,
				FooterPositionEnum.NORMAL);
		defaultsMap.put(JRDesignGroup.PROPERTY_FOOTER_POSITION, new DefaultValue(footerPositionValue, false));

		return defaultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.
	 * lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		JRDesignGroup jrGroup = (JRDesignGroup) getValue();
		if (id.equals(JRDesignGroup.PROPERTY_NAME))
			return jrGroup.getName();
		if (id.equals(JRDesignGroup.PROPERTY_EXPRESSION))
			return ExprUtil.getExpression(jrGroup.getExpression());
		if (id.equals(JRBaseGroup.PROPERTY_MIN_HEIGHT_TO_START_NEW_PAGE))
			return jrGroup.getMinHeightToStartNewPage();
		if (id.equals(JRBaseGroup.PROPERTY_MIN_DETAILS_TO_START_FROM_TOP))
			return jrGroup.getMinDetailsToStartFromTop();

		if (id.equals(JRBaseGroup.PROPERTY_START_NEW_COLUMN))
			return jrGroup.isStartNewColumn();
		if (id.equals(JRBaseGroup.PROPERTY_START_NEW_PAGE))
			return jrGroup.isStartNewPage();
		if (id.equals(JRBaseGroup.PROPERTY_RESET_PAGE_NUMBER))
			return jrGroup.isResetPageNumber();
		if (id.equals(JRBaseGroup.PROPERTY_REPRINT_HEADER_ON_EACH_PAGE))
			return jrGroup.isReprintHeaderOnEachPage();
		if (id.equals(JRBaseGroup.PROPERTY_KEEP_TOGETHER))
			return jrGroup.isKeepTogether();
		if (id.equals(JRBaseGroup.PROPERTY_PREVENT_ORPHAN_FOOTER))
			return jrGroup.isPreventOrphanFooter();
		if (id.equals(JRBaseGroup.PROPERTY_FOOTER_POSITION))
			return positionD.getIntValue(jrGroup.getFooterPositionValue());

		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (JRDesignGroup.PROPERTY_NAME.equals(evt.getPropertyName())) {
			// Temporary fix for the Community Bug #2991
			// Should be done on JR-side. Let's keep the cache map of groups in
			// sync.
			JRDesignGroup jrGroup = (JRDesignGroup) getValue();
			JasperDesign design = getJasperDesign();
			if (design != null) {
				design.getGroupsMap().remove(evt.getOldValue());
				design.getGroupsMap().put(jrGroup.getName(), jrGroup);
				// JRDesignDataset dataset = ModelUtils.getDataset(this);
				Map<String, JRVariable> variablesMap = getVariablesMap();
				JRVariable groupVar = variablesMap.get(evt.getOldValue() + "_COUNT"); //$NON-NLS-1$
				if (groupVar != null) {
					// This should launch the propertyChange event on the
					// variable so the map is updated also for it
					((JRDesignVariable) groupVar).setName(jrGroup.getName() + "_COUNT"); //$NON-NLS-1$
				}
			}
		}
		super.propertyChange(evt);
	}
	
	/**
	 * Return the variables map of the correct parent object, the jrdesign if the group is 
	 * on the main dataset or the map of the dataset if it is on a subdataset
	 * 
	 * @return a not null variables map
	 */
	protected Map<String, JRVariable> getVariablesMap(){
		if (getParent() != null && getParent().getParent() instanceof MDataset) {
			return ((MDataset)getParent().getParent()).getValue().getVariablesMap();
		}
		return getJasperDesign().getVariablesMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.
	 * lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		JRDesignGroup jrGroup = (JRDesignGroup) getValue();
		if (id.equals(JRDesignGroup.PROPERTY_NAME)) {
			jrGroup.setName((String) value);
		} else if (id.equals(JRDesignGroup.PROPERTY_EXPRESSION))
			jrGroup.setExpression(ExprUtil.setValues(jrGroup.getExpression(), value, null));
		else if (id.equals(JRBaseGroup.PROPERTY_MIN_HEIGHT_TO_START_NEW_PAGE)) {
			int minH = 0;
			if (value != null)
				minH = Math.max(0, (Integer) value);
			jrGroup.setMinHeightToStartNewPage(minH);
		} else if (id.equals(JRBaseGroup.PROPERTY_MIN_DETAILS_TO_START_FROM_TOP)) {
			int minH = 0;
			if (value != null)
				minH = Math.max(0, (Integer) value);
			jrGroup.setMinDetailsToStartFromTop(minH);
		} else if (id.equals(JRDesignGroup.PROPERTY_START_NEW_COLUMN))
			jrGroup.setStartNewColumn((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_START_NEW_PAGE))
			jrGroup.setStartNewPage((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_RESET_PAGE_NUMBER))
			jrGroup.setResetPageNumber((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_REPRINT_HEADER_ON_EACH_PAGE))
			jrGroup.setReprintHeaderOnEachPage((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_PREVENT_ORPHAN_FOOTER))
			jrGroup.setPreventOrphanFooter((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_KEEP_TOGETHER))
			jrGroup.setKeepTogether((Boolean) value);
		else if (id.equals(JRDesignGroup.PROPERTY_FOOTER_POSITION))
			jrGroup.setFooterPosition(positionD.getEnumValue(value));
	}

	/**
	 * Creates the jr group.
	 * 
	 * @param jrDataset
	 *            the jr dataset
	 * @return the jR design group
	 */
	public static JRDesignGroup createJRGroup(JRDesignDataset jrDataset) {
		JRDesignGroup jrDesignGroup = new JRDesignGroup();
		jrDesignGroup.setName(ModelUtils.getDefaultName(jrDataset.getGroupsMap(), "Group")); //$NON-NLS-1$
		return jrDesignGroup;
	}

	public ICopyable.RESULT isCopyable2(Object parent) {
		if (parent instanceof MGroups)
			return ICopyable.RESULT.COPYABLE;
		return ICopyable.RESULT.CHECK_PARENT;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ExpressionContext.class.equals(adapter)) {
			if (getParent() != null) {
				// Ideally parent should be an MGroups node
				ExpressionContext expContext = (ExpressionContext) getParent().getAdapter(ExpressionContext.class);
				if (expContext != null) {
					return expContext;
				}
			}
		}
		return super.getAdapter(adapter);
	}

	@Override
	public boolean isCuttable(ISelection currentSelection) {
		return true;
	}
}
