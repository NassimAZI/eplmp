/*******************************************************************************
  * Copyright (c) 2017-2019 DocDoku.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    DocDoku - initial API and implementation
  *******************************************************************************/
package org.polarsys.eplmp.server.events;

import org.polarsys.eplmp.core.common.Workspace;

/**
 * @author Florent Garin
 */
public class WorkspaceEvent {

    private Workspace observedWorkspace;

    public WorkspaceEvent(Workspace modifiedWorkspace) {
        this.observedWorkspace = modifiedWorkspace;
    }

    public Workspace getObservedWorkspace() {
        return observedWorkspace;
    }

    public void setObservedWorkspace(Workspace observedWorkspace) {
        this.observedWorkspace = observedWorkspace;
    }
}
