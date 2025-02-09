/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.model.subreport;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.editor.defaults.DefaultManager;
import com.jaspersoft.studio.help.HelpReferenceBuilder;
import com.jaspersoft.studio.messages.Messages;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.JReportsDTO;
import com.jaspersoft.studio.model.MGraphicElement;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.model.util.NodeIconDescriptor;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.descriptor.checkbox.CheckBoxPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.ExprUtil;
import com.jaspersoft.studio.property.descriptor.expression.JRExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.expression.JRSubreportExpressionPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.returnvalue.RVPropertyDescriptor;
import com.jaspersoft.studio.property.descriptor.subreport.parameter.SubreportPropertiesPropertyDescriptor;
import com.jaspersoft.studio.property.descriptors.NamedEnumPropertyDescriptor;
import com.jaspersoft.studio.utils.ModelUtils;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.base.JRBaseSubreport;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.OverflowType;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/*
 * The Class MSubreport.
 */
public class MSubreport extends MGraphicElement {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/** The icon descriptor. */
	private static IIconDescriptor iconDescriptor;

	private static IPropertyDescriptor[] descriptors;

	/**
	 * Gets the icon descriptor.
	 * 
	 * @return the icon descriptor
	 */
	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new NodeIconDescriptor("subreport"); //$NON-NLS-1$
		return iconDescriptor;
	}

	/**
	 * Instantiates a new m subreport.
	 */
	public MSubreport() {
		super();
	}

	/**
	 * Instantiates a new m subreport.
	 * 
	 * @param parent
	 *            the parent
	 * @param jrSubreport
	 *            the jr subreport
	 * @param newIndex
	 *            the new index
	 */
	public MSubreport(ANode parent, JRDesignSubreport jrSubreport, int newIndex) {
		super(parent, newIndex);
		setValue(jrSubreport);
		if (jrSubreport != null)
			(jrSubreport).getEventSupport().addPropertyChangeListener(this);
	}

	@Override
	public IPropertyDescriptor[] getDescriptors() {
		return descriptors;
	}

	@Override
	public void setDescriptors(IPropertyDescriptor[] descriptors1) {
		descriptors = descriptors1;
	}

	/**
	 * Creates the property descriptors.
	 * 
	 * @param desc
	 *            the desc
	 */
	@Override
	public void createPropertyDescriptors(List<IPropertyDescriptor> desc) {
		super.createPropertyDescriptors(desc);

		CheckBoxPropertyDescriptor runToBottomD = new CheckBoxPropertyDescriptor(JRBaseSubreport.PROPERTY_RUN_TO_BOTTOM,
				Messages.MSubreport_run_to_bottom, NullEnum.NULL);
		runToBottomD.setDescription(Messages.MSubreport_run_to_bottom_description);
		desc.add(runToBottomD);

		CheckBoxPropertyDescriptor useCacheD = new CheckBoxPropertyDescriptor(JRBaseSubreport.PROPERTY_USING_CACHE,
				Messages.common_using_cache, NullEnum.INHERITED);
		useCacheD.setDescription(Messages.MSubreport_using_cache_description);
		desc.add(useCacheD);

		JRSubreportExpressionPropertyDescriptor exprD = new JRSubreportExpressionPropertyDescriptor(JRDesignSubreport.PROPERTY_EXPRESSION,
				Messages.common_expression);
		exprD.setDescription(Messages.MSubreport_expression_description);
		desc.add(exprD);
		exprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#subreportExpression")); //$NON-NLS-1$

		JRExpressionPropertyDescriptor paramExprD = new JRExpressionPropertyDescriptor(
				JRDesignSubreport.PROPERTY_PARAMETERS_MAP_EXPRESSION, Messages.common_parameters_map_expression);
		paramExprD.setDescription(Messages.MSubreport_parameters_map_expression_description);
		desc.add(paramExprD);
		paramExprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#parametersMapExpression")); //$NON-NLS-1$

		JRExpressionPropertyDescriptor connExprD = new JRExpressionPropertyDescriptor(
				JRDesignSubreport.PROPERTY_CONNECTION_EXPRESSION, Messages.common_connection_expression);
		connExprD.setDescription(Messages.MSubreport_connection_expression_description);
		desc.add(connExprD);
		connExprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#connectionExpression")); //$NON-NLS-1$

		JRExpressionPropertyDescriptor dsExprD = new JRExpressionPropertyDescriptor(
				JRDesignSubreport.PROPERTY_DATASOURCE_EXPRESSION, Messages.MSubreport_datasource_expression);
		dsExprD.setDescription(Messages.MSubreport_datasource_expression_description);
		desc.add(dsExprD);
		dsExprD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#dataSourceExpression")); //$NON-NLS-1$

		SubreportPropertiesPropertyDescriptor propertiesD = new SubreportPropertiesPropertyDescriptor(
				JRDesignSubreport.PROPERTY_PARAMETERS, Messages.MSubreport_parametersName);
		propertiesD.setDescription(Messages.MSubreport_parameters_description);
		desc.add(propertiesD);
		propertiesD.setHelpRefBuilder(new HelpReferenceBuilder(
				"net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#subreportParameter")); //$NON-NLS-1$
		propertiesD.init(this);

		RVPropertyDescriptor returnValuesD = new RVPropertyDescriptor(JRDesignSubreport.PROPERTY_RETURN_VALUES,
				Messages.MSubreport_returnValuesName);
		returnValuesD.setDescription(Messages.MSubreport_return_values_description);
		desc.add(returnValuesD);
		returnValuesD.setHelpRefBuilder(
				new HelpReferenceBuilder("net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#returnValue")); //$NON-NLS-1$

		overflowTypeD = new NamedEnumPropertyDescriptor<OverflowType>(JRBaseSubreport.PROPERTY_OVERFLOW_TYPE,
				Messages.MSubreport_0, OverflowType.STRETCH, NullEnum.NOTNULL);
		overflowTypeD.setDescription(Messages.MSubreport_1);
		desc.add(overflowTypeD);

		overflowTypeD.setCategory(Messages.MSubreport_subreport_properties_category);
		returnValuesD.setCategory(Messages.MSubreport_subreport_properties_category);
		propertiesD.setCategory(Messages.MSubreport_subreport_properties_category);
		dsExprD.setCategory(Messages.MSubreport_subreport_properties_category);
		connExprD.setCategory(Messages.MSubreport_subreport_properties_category);
		paramExprD.setCategory(Messages.MSubreport_subreport_properties_category);
		exprD.setCategory(Messages.MSubreport_subreport_properties_category);
		useCacheD.setCategory(Messages.MSubreport_subreport_properties_category);
		runToBottomD.setCategory(Messages.MSubreport_subreport_properties_category);

		setHelpPrefix(desc, "net.sf.jasperreports.doc/docs/schema.reference.html?cp=0_1#subreport"); //$NON-NLS-1$
	}

	private static NamedEnumPropertyDescriptor<OverflowType> overflowTypeD;
	private JReportsDTO returnValuesDTO;

	@Override
	public Object getPropertyValue(Object id) {
		JRDesignSubreport jrElement = (JRDesignSubreport) getValue();
		if (id.equals(JRBaseSubreport.PROPERTY_RUN_TO_BOTTOM))
			return jrElement.isRunToBottom();
		if (id.equals(JRBaseSubreport.PROPERTY_USING_CACHE))
			return jrElement.getUsingCache();
		if (id.equals(JRDesignSubreport.PROPERTY_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getExpression());
		if (id.equals(JRDesignSubreport.PROPERTY_PARAMETERS_MAP_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getParametersMapExpression());
		if (id.equals(JRDesignSubreport.PROPERTY_CONNECTION_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getConnectionExpression());
		if (id.equals(JRDesignSubreport.PROPERTY_DATASOURCE_EXPRESSION))
			return ExprUtil.getExpression(jrElement.getDataSourceExpression());
		if (id.equals(JRDesignSubreport.PROPERTY_PARAMETERS))
			return JRCloneUtils.cloneArray(jrElement.getParameters());
		if (id.equals(JRDesignSubreport.PROPERTY_RETURN_VALUES)) {
			if (returnValuesDTO == null) {
				returnValuesDTO = new JReportsDTO();
				returnValuesDTO.setjConfig(getJasperConfiguration());
				returnValuesDTO.setSubreport(jrElement, ModelUtils.getFirstDatasetInHierarchy(this));
			}
			returnValuesDTO.setValue(JRCloneUtils.cloneList(jrElement.getReturnValuesList()));
			return returnValuesDTO;

		}
		if (id.equals(JRBaseSubreport.PROPERTY_OVERFLOW_TYPE))
			return overflowTypeD.getIntValue(jrElement.getOverflowType());

		return super.getPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JRDesignSubreport jrElement = (JRDesignSubreport) getValue();
		if (id.equals(JRBaseSubreport.PROPERTY_RUN_TO_BOTTOM))
			jrElement.setRunToBottom((Boolean) value);
		if (id.equals(JRBaseSubreport.PROPERTY_USING_CACHE))
			jrElement.setUsingCache((Boolean) value);
		else if (id.equals(JRBaseSubreport.PROPERTY_OVERFLOW_TYPE))
			jrElement.setOverflowType(overflowTypeD.getEnumValue(value));
		else if (id.equals(JRBaseSubreport.PROPERTY_RUN_TO_BOTTOM))
			jrElement.setUsingCache((Boolean) value);
		else if (id.equals(JRDesignSubreport.PROPERTY_EXPRESSION))
			jrElement.setExpression(ExprUtil.setValues(jrElement.getExpression(), value));
		else if (id.equals(JRDesignSubreport.PROPERTY_PARAMETERS_MAP_EXPRESSION))
			jrElement.setParametersMapExpression(ExprUtil.setValues(jrElement.getParametersMapExpression(), value));
		else if (id.equals(JRDesignSubreport.PROPERTY_CONNECTION_EXPRESSION)) {
			if (value instanceof String)
				value = value != null && ((String) value).equals("") ? null : value; //$NON-NLS-1$
			jrElement.setConnectionExpression(ExprUtil.setValues(jrElement.getConnectionExpression(), value));
		} else if (id.equals(JRDesignSubreport.PROPERTY_DATASOURCE_EXPRESSION)) {
			if (value instanceof String)
				value = value != null && ((String) value).equals("") ? null : value; //$NON-NLS-1$
			jrElement.setDataSourceExpression(ExprUtil.setValues(jrElement.getDataSourceExpression(), value));
		} else if (id.equals(JRDesignSubreport.PROPERTY_PARAMETERS)) {
			if (value.getClass().isArray()) {
				JRSubreportParameter[] v = (JRSubreportParameter[]) value;
				JRSubreportParameter[] old = jrElement.getParameters();
				for (JRSubreportParameter p : old)
					jrElement.removeParameter(p.getName());
				for (JRSubreportParameter p : v)
					try {
						jrElement.addParameter(p);
					} catch (JRException e) {
						e.printStackTrace();
					}
			}
		} else if (id.equals(JRDesignSubreport.PROPERTY_RETURN_VALUES)) {
			returnValuesDTO = (JReportsDTO) value;
			List<JRSubreportReturnValue> list = (List<JRSubreportReturnValue>) returnValuesDTO.getValue();
			for (JRSubreportReturnValue srv : jrElement.getReturnValues())
				jrElement.removeReturnValue(srv);
			for (JRSubreportReturnValue j : list)
				jrElement.addReturnValue(j);
		}
		super.setPropertyValue(id, value);
	}

	@Override
	public int getDefaultHeight() {
		Object defaultValue = DefaultManager.INSTANCE.getDefaultPropertiesValue(this.getClass(),
				JRDesignElement.PROPERTY_HEIGHT);
		return defaultValue != null ? (Integer) defaultValue : 200;
	}

	@Override
	public int getDefaultWidth() {
		Object defaultValue = DefaultManager.INSTANCE.getDefaultPropertiesValue(this.getClass(),
				JRDesignElement.PROPERTY_WIDTH);
		return defaultValue != null ? (Integer) defaultValue : 200;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jaspersoft.studio.model.MGeneric#createJRElement(net.sf.jasperreports.
	 * engine.design.JasperDesign)
	 */
	@Override
	public JRDesignElement createJRElement(JasperDesign jasperDesign, boolean applayDefault) {
		JRDesignSubreport subreport = new JRDesignSubreport(jasperDesign);

		if (applayDefault) {
			DefaultManager.INSTANCE.applyDefault(this.getClass(), subreport);
		}

		return subreport;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getDisplayText()
	 */
	@Override
	public String getDisplayText() {
		String p = getElementNameProperty();
		return Misc.isNullOrEmpty(p) ? getIconDescriptor().getTitle() : p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getImagePath()
	 */
	@Override
	public ImageDescriptor getImagePath() {
		return getIconDescriptor().getIcon16();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jaspersoft.studio.model.MGeneric#getToolTip()
	 */
	@Override
	public String getToolTip() {
		JRDesignSubreport value = getValue();
		if (value != null) {
			String tip = ""; //$NON-NLS-1$
			if (value.getExpression() != null)
				tip += value.getExpression().getText();
			if (tip.trim().isEmpty())
				return getIconDescriptor().getTitle() + ": " + Messages.common_noExpressionMessage; //$NON-NLS-1$
			else
				return getIconDescriptor().getTitle() + ": " + tip; //$NON-NLS-1$
		}
		return getIconDescriptor().getToolTip();
	}

	@Override
	public void trasnferProperties(JRElement target) {
		super.trasnferProperties(target);

		JRDesignSubreport jrSource = (JRDesignSubreport) getValue();
		if (jrSource != null) {
			JRDesignSubreport jrTarget = (JRDesignSubreport) target;
			jrTarget.setRunToBottom(jrSource.isRunToBottom());
		}
	}

	@Override
	public JRDesignSubreport getValue() {
		return (JRDesignSubreport) super.getValue();
	}
}
