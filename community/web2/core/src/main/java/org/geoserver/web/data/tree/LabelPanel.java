/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.tree;

import javax.swing.tree.TreeNode;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public class LabelPanel extends CatalogTreePanel {

    Label label;

    public LabelPanel(String id, DataTreeTable tree, MarkupContainer parent,
            CatalogNode node, int level) {
        super(id, tree, parent, node, level);

        label = new org.apache.wicket.markup.html.basic.Label("label",
                new Model(node));
        add(label);
    }
    
    @Override
    protected ResourceReference getNodeIcon(DataTreeTable tree, TreeNode node) {
        return null;
    }

}
