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

package org.polarsys.eplmp.core.product;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * A class that stores the result of a conversion process.
 *
 * @author Morgan Guimard
 * @version 2.5, 11/05/17
 * @since   V2.5
 */
@Entity
@IdClass(PartIterationKey.class)
public class Conversion implements Serializable {

    @Id
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private PartIteration partIteration;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private boolean pending;

    private boolean succeed;

    public Conversion() {
    }

    public Conversion(PartIteration partIteration) {
        this(new Date(), null, true, false, partIteration);
    }

    public Conversion(Date startDate, Date endDate, boolean pending, boolean succeed, PartIteration partIteration) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.pending = pending;
        this.succeed = succeed;
        this.partIteration = partIteration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public PartIteration getPartIteration() {
        return partIteration;
    }

    public void setPartIteration(PartIteration partIteration) {
        this.partIteration = partIteration;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
