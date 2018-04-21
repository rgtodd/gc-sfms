package sfms.web.models;

import sfms.web.schemas.StarModelSchema;

public class StarSortingModel extends SortingModel {

	public boolean getXHasIcon() {
		return getSort().equals(StarModelSchema.X);
	}

	public String getXLinkDirection() {
		return getLinkDirection(StarModelSchema.X);
	}

	public boolean getYHasIcon() {
		return getSort().equals(StarModelSchema.Y);
	}

	public String getYLinkDirection() {
		return getLinkDirection(StarModelSchema.Y);
	}

	public boolean getZHasIcon() {
		return getSort().equals(StarModelSchema.Z);
	}

	public String getZLinkDirection() {
		return getLinkDirection(StarModelSchema.Z);
	}

}
