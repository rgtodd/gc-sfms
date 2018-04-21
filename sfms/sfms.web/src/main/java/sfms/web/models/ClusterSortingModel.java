package sfms.web.models;

import sfms.web.schemas.ClusterModelSchema;

public class ClusterSortingModel extends SortingModel {

	public boolean getMaximumXHasIcon() {
		return getSort().equals(ClusterModelSchema.MAXIMUM_X);
	}

	public String getMaximumXLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MAXIMUM_X);
	}

	public boolean getMaximumYHasIcon() {
		return getSort().equals(ClusterModelSchema.MAXIMUM_Y);
	}

	public String getMaximumYLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MAXIMUM_Y);
	}

	public boolean getMaximumZHasIcon() {
		return getSort().equals(ClusterModelSchema.MAXIMUM_Z);
	}

	public String getMaximumZLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MAXIMUM_Z);
	}

	public boolean getMinimumXHasIcon() {
		return getSort().equals(ClusterModelSchema.MINIMUM_X);
	}

	public String getMinimumXLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MINIMUM_X);
	}

	public boolean getMinimumYHasIcon() {
		return getSort().equals(ClusterModelSchema.MINIMUM_Y);
	}

	public String getMinimumYLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MINIMUM_Y);
	}

	public boolean getMinimumZHasIcon() {
		return getSort().equals(ClusterModelSchema.MINIMUM_Z);
	}

	public String getMinimumZLinkDirection() {
		return getLinkDirection(ClusterModelSchema.MINIMUM_Z);
	}
}
