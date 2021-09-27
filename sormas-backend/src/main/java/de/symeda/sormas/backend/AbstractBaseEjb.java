package de.symeda.sormas.backend;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

	public DTO save(@Valid DTO dtoToSave) {
		return save(dtoToSave, false);
	}

	public abstract DTO save(@Valid DTO dtoToSave, boolean allowMerge);

	// todo private
	protected DTO persist(DTO dto, ADO entityToPersist) {
		fillOrBuildEntity(dto, entityToPersist, true);
		service.ensurePersisted(entityToPersist);
		return toDto(entityToPersist);
	}

	protected abstract void fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	public abstract DTO toDto(ADO ado);
}
