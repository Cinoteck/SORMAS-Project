/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.share;

import java.util.Date;

public class ExternalShareInfoCountAndLatestDate {

	private final Long associatedObjectId;

	private final Long count;

	private final Date latestDate;

	public ExternalShareInfoCountAndLatestDate(Long associatedObjectId, Long count, Date latestDate) {
		this.associatedObjectId = associatedObjectId;
		this.count = count;
		this.latestDate = latestDate;
	}

	public Long getAssociatedObjectId() {
		return associatedObjectId;
	}

	public Long getCount() {
		return count;
	}

	public Date getLatestDate() {
		return latestDate;
	}
}
