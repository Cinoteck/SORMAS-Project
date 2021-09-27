package de.symeda.sormas.backend.infrastructure;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;

import java.util.List;

public abstract class AbstractInfrastructureEjb<A extends InfrastructureAdo, D extends EntityDto, S extends AbstractInfrastructureAdoService<A, C>, C extends BaseCriteria>
	extends AbstractBaseEjb<A, D, S> {

	protected FeatureConfigurationFacadeEjb featureConfiguration;

	protected AbstractInfrastructureEjb() {
		super();
	}

	protected AbstractInfrastructureEjb(S service, FeatureConfigurationFacadeEjb featureConfiguration) {
		super(service);
		this.featureConfiguration = featureConfiguration;
	}

	protected D save(D dtoToSave, boolean allowMerge, String duplicateErrorMessage) {
		if (dtoToSave == null) {
			return null;
		}
		A existingEntity = service.getByUuid(dtoToSave.getUuid());

		if (existingEntity == null) {
			List<A> duplicates = findDuplicates(dtoToSave);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					return mergeAndSave(dtoToSave, duplicates);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(duplicateErrorMessage));
				}
			}
		}
		return persist(dtoToSave, existingEntity);
	}

	@Override
	public void archive(String uuid) {
		// todo this should be really in the parent but right now there the setter for archived is not available there
		checkInfraDataLocked();
		A ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}
	}

	public void dearchive(String uuid) {
		checkInfraDataLocked();
		doDearchive(uuid);
	}

	protected void dearchiveUnchecked(String uuid) {
		doDearchive(uuid);
	}

	private void doDearchive(String uuid) {
		A ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
	}

	protected void checkInfraDataLocked() {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
	}

}
