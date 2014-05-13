/*******************************************************************************
 * Copyright (C) 2010 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, 
 * the following license terms apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jaspersoft Studio Team - initial API and implementation
 ******************************************************************************/
package com.jaspersoft.studio.components.crosstab.part;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.components.crosstab.figure.EmptyCellFigure;
import com.jaspersoft.studio.components.crosstab.model.MCrosstab;
import com.jaspersoft.studio.components.crosstab.model.title.MTitle;
import com.jaspersoft.studio.editor.gef.figures.ReportPageFigure;
import com.jaspersoft.studio.editor.gef.parts.FigureEditPart;
import com.jaspersoft.studio.editor.gef.parts.editPolicy.PageLayoutEditPolicy;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.MPage;

/*
 * BandEditPart creates the figure for the band. The figure is actually just the bottom border of the band. This allows
 * to drag this border to resize the band. The PageEditPart sets a specific contraint for the BandEditPart elements in
 * order to make them move only vertically. The BandMoveEditPolicy is responsable for the feedback when the band is
 * dragged.
 * 
 * @author Chicu Veaceslav, Giulio Toffoli
 * 
 */
public class CrosstabTitleEditPart extends ACrosstabCellEditPart {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PageLayoutEditPolicy() {

			protected Command getCreateCommand(ANode parent, Object obj, Rectangle constraint, int index) {
				if (parent instanceof MPage)
					parent = getModel();
				return super.getCreateCommand(parent, obj, constraint, index);
			}
		});
	}

	@Override
	public MTitle getModel() {
		return (MTitle) super.getModel();
	}

	@Override
	protected void setupFigure(IFigure rect) {
		updateContainerSize();
		IGraphicElement model = (IGraphicElement) getModel();
		rect.setToolTip(new Label(((ANode) model).getToolTip()));

		Rectangle bounds = ((IGraphicElement) model).getBounds();
		int x = bounds.x + ReportPageFigure.PAGE_BORDER.left;
		int y = bounds.y + ReportPageFigure.PAGE_BORDER.top;

		rect.setLocation(new Point(x, y));

		((EmptyCellFigure) rect).setJRElement(null, getDrawVisitor(), new Dimension(bounds.width, bounds.height));
		rect.setSize(bounds.width, bounds.height);
		updateRulers();

		if (getSelected() == 1)
			updateRulers();
		else {
			List<?> selected = getViewer().getSelectedEditParts();
			if (selected.isEmpty())
				updateRulers();
			else
				for (Object obj : selected) {
					if (obj instanceof FigureEditPart) {
						FigureEditPart figEditPart = (FigureEditPart) obj;
						if (figEditPart.getModel().getParent() == getModel())
							figEditPart.updateRulers();
					}
				}
		}
	}

	@Override
	protected MCrosstab getCrosstab() {
		return getModel().getCrosstab();
	}

}
