package sfms.web.models;

import sfms.web.schemas.CrewMemberModelSchema;

public class CrewMemberSortingModel extends SortingModel {

	public boolean getFirstNameHasIcon() {
		return getSort().equals(CrewMemberModelSchema.FIRST_NAME);
	}

	public String getFirstNameLinkDirection() {
		return getLinkDirection(CrewMemberModelSchema.FIRST_NAME);
	}

	public boolean getLastNameHasIcon() {
		return getSort().equals(CrewMemberModelSchema.LAST_NAME);
	}

	public String getLastNameLinkDirection() {
		return getLinkDirection(CrewMemberModelSchema.LAST_NAME);
	}

}
