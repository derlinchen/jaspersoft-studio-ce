/*******************************************************************************
 * Copyright (C) 2010 - 2012 Jaspersoft Corporation. All rights reserved.
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
package com.jaspersoft.studio.editor.style.editpolicy;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import com.jaspersoft.studio.editor.style.StyleTreeEditPartFactory;
import com.jaspersoft.studio.model.ANode;
/*
 * The Class JDContainerEditPolicy.
 * 
 * @author Chicu Veaceslav
 */
public class JDStyleContainerEditPolicy extends ContainerEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.ContainerEditPolicy#getOrphanChildrenCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	public Command getOrphanChildrenCommand(GroupRequest request) {
		List<?> parts = request.getEditParts();
		CompoundCommand result = new CompoundCommand("orphans"); //$NON-NLS-1$
		for (int i = 0; i < parts.size(); i++) {
			result.add(StyleTreeEditPartFactory.getOrphanCommand((ANode) getHost().getModel(),
					(ANode) ((EditPart) parts.get(i)).getModel()));

		}
		return result.unwrap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// Command createCommand = OutlineTreeEditPartFactory.getCreateCommand((ANode) getHost().getModel(), (ANode) request
		// .getNewObject(), request.getLocation(), -1);
		// return createCommand;
		return null;
	}

}
