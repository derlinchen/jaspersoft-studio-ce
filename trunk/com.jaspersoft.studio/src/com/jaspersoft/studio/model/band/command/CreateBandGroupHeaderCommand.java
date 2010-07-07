/*
 * Jaspersoft Open Studio - Eclipse-based JasperReports Designer.
 * Copyright (C) 2005 - 2010 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Open Studio.
 *
 * Jaspersoft Open Studio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Open Studio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Jaspersoft Open Studio. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.studio.model.band.command;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignSection;

import org.eclipse.gef.commands.Command;

import com.jaspersoft.studio.model.band.MBandGroupHeader;

// TODO: Auto-generated Javadoc
/**
 * creates a band in a Group.
 * 
 * @author Chicu Veaceslav
 */
public class CreateBandGroupHeaderCommand extends Command {
	
	/** The jr band. */
	private JRDesignBand jrBand;
	
	/** The jr design section. */
	private JRDesignSection jrDesignSection;
	
	/** The index. */
	private int index = -1;

	/**
	 * Instantiates a new creates the band group header command.
	 * 
	 * @param destNode
	 *          the dest node
	 */
	public CreateBandGroupHeaderCommand(MBandGroupHeader destNode) {
		super();
		this.jrDesignSection = (JRDesignSection) ((JRDesignGroup) destNode.getJrGroup()).getGroupHeaderSection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if (jrBand == null) {
			jrBand = MBandGroupHeader.createJRBand();
		}
		if (jrBand != null) {
			if (index < 0 || index > jrDesignSection.getBandsList().size())
				jrDesignSection.addBand(jrBand);
			else
				jrDesignSection.addBand(index, jrBand);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		index = jrDesignSection.getBandsList().indexOf(jrBand);
		jrDesignSection.removeBand(jrBand);
	}

}
