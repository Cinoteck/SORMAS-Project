package de.symeda.sormas.backend;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

import javax.validation.Valid;

// todo should we use BaseAdoService?
public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, DTO extends EntityDto, SRV extends AdoServiceWithUserFilter<ADO>> {

	protected SRV service;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(SRV service) {
		this.service = service;
	}

	// todo cannot be filled right now as we are missing ArchivableAbstractDomainObject
	// with this abstract class e.g., ImmunizationFacadeEjb could be wired up to this as well
	public abstract void archive(String uuid);

	public abstract void dearchive(String uuid);

	public DTO save(@Valid DTO dto) {
		return save(dto, false);
	}

	public abstract DTO save(@Valid DTO dto, boolean allowMerge);
}
