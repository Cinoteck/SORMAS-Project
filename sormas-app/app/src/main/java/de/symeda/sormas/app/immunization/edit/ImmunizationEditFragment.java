/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.edit;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentImmunizationEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;

public class ImmunizationEditFragment extends BaseEditFragment<FragmentImmunizationEditLayoutBinding, Immunization, Immunization> {

	private Immunization record;

	// enum lists

	private List<Item> diseaseList;
	private List<Item> immunizationStatusList;
	private List<Item> meansOfImmunizationList;
	private List<Item> immunizationManagementStatusList;

	private List<Item> initialResponsibleDistricts;
	private List<Item> initialResponsibleCommunities;
	private List<Item> initialRegions;
	private List<Item> allDistricts;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;

	public static ImmunizationEditFragment newInstance(Immunization activityRootData) {
		ImmunizationEditFragment immunizationEditFragment = newInstanceWithFieldCheckers(
			ImmunizationEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		return immunizationEditFragment;
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_immunization_edit_layout;
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_immunization_information);
	}

	@Override
	public Immunization getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getDisease() != null && !diseases.contains(record.getDisease())) {
			diseaseList.add(DataUtils.toItem(record.getDisease()));
		}
		immunizationStatusList = DataUtils.getEnumItems(ImmunizationStatus.class, true);
		meansOfImmunizationList = DataUtils.getEnumItems(MeansOfImmunization.class, true);
		immunizationManagementStatusList = DataUtils.getEnumItems(ImmunizationManagementStatus.class, true);

		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		allDistricts = InfrastructureDaoHelper.loadAllDistricts();
		initialResponsibleDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialResponsibleCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
	}

	@Override
	protected void onLayoutBinding(FragmentImmunizationEditLayoutBinding contentBinding) {
		contentBinding.setData(record);

		contentBinding.setYesNoUnknownClass(YesNoUnknown.class);

		InfrastructureDaoHelper.initializeRegionFields(
			contentBinding.immunizationResponsibleRegion,
			initialRegions,
			record.getResponsibleRegion(),
			contentBinding.immunizationResponsibleDistrict,
			initialResponsibleDistricts,
			record.getResponsibleDistrict(),
			contentBinding.immunizationResponsibleCommunity,
			initialResponsibleCommunities,
			record.getResponsibleCommunity());

		InfrastructureDaoHelper.initializeRegionFieldListeners(
			contentBinding.immunizationResponsibleRegion,
			contentBinding.immunizationResponsibleDistrict,
			record.getResponsibleDistrict(),
			contentBinding.immunizationResponsibleCommunity,
			record.getResponsibleCommunity(),
			null,
			null,
			null,
			null,
			null,
			() -> false);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentImmunizationEditLayoutBinding contentBinding) {

		// Initialize ControlSpinnerFields
		contentBinding.immunizationDisease.initializeSpinner(diseaseList);
		contentBinding.immunizationImmunizationStatus.initializeSpinner(immunizationStatusList);
		contentBinding.immunizationImmunizationManagementStatus.initializeSpinner(immunizationManagementStatusList);
		contentBinding.immunizationMeansOfImmunization.initializeSpinner(meansOfImmunizationList);

		// Initialize ControlDateFields
		contentBinding.immunizationReportDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationStartDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationEndDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationPreviousInfectionDate.initializeDateField(getFragmentManager());
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}

}
