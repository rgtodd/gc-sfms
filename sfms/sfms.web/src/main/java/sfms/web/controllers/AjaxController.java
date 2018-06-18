package sfms.web.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.IOUtils;

import sfms.common.Constants;
import sfms.rest.api.FilterCriteria;
import sfms.rest.api.RestDetail;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.StarField;
import sfms.storage.Storage;
import sfms.storage.StorageManagerUtility;
import sfms.storage.StorageManagerUtility.ObjectFactory;
import sfms.web.SfmsController;
import sfms.web.mock.MockSpaceData;
import sfms.web.models.MapItemTypes;
import sfms.web.models.ajax.GetMapItemsResponse;
import sfms.web.models.ajax.GetSectorResponse;
import sfms.web.models.ajax.GetSectorsResponse;
import sfms.web.models.ajax.MapItemModel;
import sfms.web.models.ajax.MapItemPropertyGroupModel;
import sfms.web.models.ajax.MapItemPropertyModel;
import sfms.web.models.ajax.MapItemSetModel;
import sfms.web.models.ajax.SectorModel;

@RestController
@RequestMapping({ "/ajax" })
public class AjaxController extends SfmsController {

	private final Logger logger = Logger.getLogger(AjaxController.class.getName());

	@Autowired
	private MockSpaceData m_mockSpaceData;

	@GetMapping({ "/getSectors" })
	public GetSectorsResponse getSectors() {

		String uri = UriComponentsBuilder.newInstance().path("sector")
				.queryParam(RestParameters.PAGE_SIZE, "999999")
				.build().toUriString();

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Sector>> restResponse = restTemplate.exchange(getRestUrl(uri), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Sector>>() {
				});
		SearchResult<Sector> sectors = restResponse.getBody();

		List<SectorModel> sectorModels = new ArrayList<SectorModel>();
		for (Sector sector : sectors.getEntities()) {
			SectorModel sectorModel = new SectorModel();
			sectorModel.setKey(sector.getKey());
			sectorModel.setSx(asInteger(sector.getSectorX()));
			sectorModel.setSy(asInteger(sector.getSectorY()));
			sectorModel.setSz(asInteger(sector.getSectorZ()));
			sectorModel.setMinimumX(asInteger(sector.getMinimumX()));
			sectorModel.setMinimumY(asInteger(sector.getMinimumY()));
			sectorModel.setMinimumZ(asInteger(sector.getMinimumZ()));
			sectorModel.setMaximumX(asInteger(sector.getMaximumX()));
			sectorModel.setMaximumY(asInteger(sector.getMaximumY()));
			sectorModel.setMaximumZ(asInteger(sector.getMaximumZ()));
			sectorModels.add(sectorModel);
		}

		GetSectorsResponse response = new GetSectorsResponse();
		response.setSectors(sectorModels);

		return response;
	}

	private Integer asInteger(Long value) {
		if (value == null) {
			return null;
		}

		return (int) (long) value;
	}

	@GetMapping({ "/getSectorByLocation" })
	public GetSectorResponse getSectorByLocation(
			@RequestParam("x") Double x,
			@RequestParam("y") Double y,
			@RequestParam("z") Double z) {

		SectorModel sector = m_mockSpaceData.getSectors().stream()
				.filter(s -> s.getMinimumX() <= x && x < s.getMaximumX() && s.getMinimumY() <= y && y < s.getMaximumY()
						&& s.getMinimumZ() <= z && z < s.getMaximumZ())
				.findFirst().get();

		GetSectorResponse response = new GetSectorResponse();
		response.setSector(sector);

		return response;
	}

	@GetMapping({ "/getSectorByKey" })
	public GetSectorResponse getSectorByKey(@RequestParam("key") String key) {

		SectorModel sector = m_mockSpaceData.getSectors().stream().filter(s -> s.getKey().equals(key)).findFirst()
				.get();

		GetSectorResponse response = new GetSectorResponse();
		response.setSector(sector);

		return response;
	}

	@GetMapping({ "/getMapItem" })
	public MapItemModel getMapItem(
			@RequestParam("mapItemType") Integer mapItemType,
			@RequestParam("mapItemKey") String mapItemKey) {

		switch (mapItemType) {
		case MapItemTypes.STAR:

			RestTemplate restTemplate = createRestTempate();
			ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + mapItemKey), HttpMethod.GET,
					createHttpEntity(), new ParameterizedTypeReference<Star>() {
					});
			Star star = restResponse.getBody();

			List<MapItemPropertyGroupModel> propertyGroups = new ArrayList<MapItemPropertyGroupModel>();

			addPropertyGroup(propertyGroups, "Summary");
			addProperty(propertyGroups, "Key", "Key of star record in database.", null, star.getKey());
			addProperty(propertyGroups, "Proper Name", "A common name for the star.", null, star.getProperName());
			addProperty(propertyGroups, "Constellation", "Standard constellation associated with star.", null,
					star.getConstellation());
			addProperty(propertyGroups, "Coordinates", "Equatorial coordinates of star.", null,
					formatCoordinates(star.getX(), star.getY(), star.getZ()));
			addProperty(propertyGroups, "Right Ascension", "Right ascension of star for epoch and equinox 2000.0.",
					null, star.getRightAscension());
			addProperty(propertyGroups, "Declination", "Declination of star for epoch and equinox 2000.0.", null,
					star.getDeclination());
			addProperty(propertyGroups, "Distance", "Distance from Earth in parsecs.", null, star.getDistance());

			addPropertyGroup(propertyGroups, "Position");
			addProperty(propertyGroups, "Velocity Components",
					"Velocity of equatorial components of star, in parsecs/year.", null,
					formatCoordinates(star.getVX(), star.getVY(), star.getVZ()));
			addProperty(propertyGroups, "Right Ascension &theta;", "Right ascension of star in radians.", null,
					star.getRightAcensionRadians());
			addProperty(propertyGroups, "Declination &theta;", "Declination of star in radians.", null,
					star.getDeclinationRadians());
			addProperty(propertyGroups, "Proper Motion RA",
					"Right ascension of proper motion of star in milliarcseconds per year.", null,
					star.getProperMotionRightAscension());
			addProperty(propertyGroups, "Proper Motion Dec",
					"Declination of proper motion of star in milliarcseconds per year.", null,
					star.getProperMotionDeclination());
			addProperty(propertyGroups, "Proper Motion RA &theta;",
					"Right ascension of proper motion of star in radians.", null,
					star.getProperMotionRightAscensionRadians());
			addProperty(propertyGroups, "Proper Motion Dec &theta;", "Declination of proper motion of star in radians.",
					null, star.getProperMotionDeclinationRadians());
			addProperty(propertyGroups, "Radial Velocity", "Radial velocity of star in km/sec.", null,
					star.getRadialVelocity());

			addPropertyGroup(propertyGroups, "ID");
			addProperty(propertyGroups, "Hipparcos ID", "ID of star in the Hipparcos catalog.",
					"https://www.cosmos.esa.int/web/hipparcos/catalogues", star.getHipparcosId());
			addProperty(propertyGroups, "Henry Draper ID", "ID of star in the Henry Draper catalog.", null,
					star.getHenryDraperId());
			addProperty(propertyGroups, "Harvard Revised ID",
					"ID of star in the Harvard Revised Catalog.  This is the same as its number in the Yale Bright Star Catalog.",
					null, star.getHarvardRevisedId());
			addProperty(propertyGroups, "Gliese ID",
					"ID of star in the third edition of the Gliese Catalog of Nearby Stars.", null, star.getGlieseId());
			addProperty(propertyGroups, "Bayer / Flamsteed Designation",
					"The Bayer / Flamsteed designation, primarily from the Fifth Edition of the Yale Bright Star Catalog. This is a combination of the two designations. The Flamsteed number, if present, is given first; then a three-letter abbreviation for the Bayer Greek letter; the Bayer superscript number, if present; and finally, the three-letter constellation abbreviation. ",
					null, star.getBayerFlamsteedId());
			addProperty(propertyGroups, "Bayer Designation", "Bayer designation af star.", null, star.getBayerId());
			addProperty(propertyGroups, "Flamsteed Number", "Flamsteed number of star.", null, star.getFlamsteed());
			addProperty(propertyGroups, "Cluster Key", "Key of cluster containing star.", null, star.getClusterKey());
			addProperty(propertyGroups, "Sector Key", "Key of sector containing star.", null, star.getSectorKey());

			addPropertyGroup(propertyGroups, "Appearance");
			addProperty(propertyGroups, "Magnitude", "The star's apparent visual magnitude.", null,
					star.getMagnitude());
			addProperty(propertyGroups, "Absolute Magnitude",
					"The star's absolute visual magnitude (i.e. its apparent magntidue from a distance of 10 parsecs.)",
					null, star.getAbsoluteMagnitude());
			addProperty(propertyGroups, "Spectrum", "The star's spectral type.", null, star.getSpectrum());
			addProperty(propertyGroups, "Color Index",
					"The star's color index (i.e. blue magnitude - visual magnitude.)", null, star.getColorIndex()); // ColorIndex-ci
			addProperty(propertyGroups, "Luminosity", "Lominosity of star as a multiple of Solar luminosity.", null,
					star.getLuminosity());
			addProperty(propertyGroups, "Variable Star Designation", "Standard variable star designation.", null,
					star.getVariableStarDesignation());
			addProperty(propertyGroups, "Minimum Magnitude", "Minimum magnitude of variable star.", null,
					star.getVariableMinimum());
			addProperty(propertyGroups, "Maximum Magnitude", "Maximum magnitude of variable star.", null,
					star.getVariableMaximum());

			addPropertyGroup(propertyGroups, "Multiple Star System");
			addProperty(propertyGroups, "Companion Star ID", "In a multiple star system, ID of companion star.", null,
					star.getCompanionStarId());
			addProperty(propertyGroups, "Primary Star ID", "In a multiple star system, ID of primary star.", null,
					star.getPrimaryStarId());
			addProperty(propertyGroups, "Multiple Star ID", "ID of multiple star system.", null,
					star.getMultipleStarId());

			MapItemModel result = new MapItemModel();
			result.setMapItemKey(mapItemKey);
			result.setMapItemType(mapItemType);
			result.setMapItemName("Star " + star.getCatalogId());
			result.setSectorKey(star.getSectorKey());
			result.setPropertyGroups(propertyGroups);

			return result;
		case MapItemTypes.SHIP:
			// TODO: Extract properties for ship.

			return null;
		default:

			return null;
		}
	}

	// @GetMapping({ "/getMapItemsBySector" })
	// public GetMapItemsResponse getMapItemsBySector(
	// @RequestParam("sectorKey") String sectorKey,
	// @RequestParam("mapItemType") Integer mapItemType) {
	//
	// return getMapItemsBySectorRest(sectorKey, mapItemType);
	// }

	@GetMapping({ "/getMapItemsByRank/get" })
	public GetMapItemsResponse getMapItemsByRank(@RequestParam("rank") Long rank) {

		GetMapItemsResponse response = getMapItemsByRankRest(rank);

		return response;
	}

	@GetMapping({ "/getMapItemsByRank" })
	public void getMapItemsByRankCached(@RequestParam("rank") Long rank, HttpServletResponse response)
			throws Exception {

		ObjectFactory objectFactory = new ObjectFactory() {
			@Override
			public byte[] createObject() throws Exception {
				GetMapItemsResponse serviceResponse = getMapItemsByRankRest(rank);

				ObjectMapper mapper = new ObjectMapper();
				ObjectWriter writer = mapper.writerFor(GetMapItemsResponse.class);
				byte[] buffer = writer.writeValueAsBytes(serviceResponse);

				return buffer;
			}
		};

		String objectName = "getMapItemsByRank-" + String.valueOf(rank);

		try (ReadableByteChannel readChannel = StorageManagerUtility.getCachedObject(Storage.getManager(), objectName,
				Constants.CONTENT_TYPE_JSON, objectFactory);
				InputStream inputStream = Channels.newInputStream(readChannel)) {
			response.setContentType(Constants.CONTENT_TYPE_JSON);
			try (OutputStream outputStream = response.getOutputStream()) {
				IOUtils.copy(inputStream, outputStream);
			}
		}
	}

	private GetMapItemsResponse getMapItemsByRankRest(Long rank) {

		Long minimumX = -1000 + rank * 200;
		Long maximumX = minimumX + 200;

		Map<String, MapItemSetModel> mapItemSetByKey = new HashMap<String, MapItemSetModel>();

		SortCriteria sortCriteria = SortCriteria.newBuilder()
				.ascending(StarField.X.getName())
				.build();

		FilterCriteria filterCriteria = FilterCriteria.newBuilder()
				.add(StarField.X.getName(), FilterCriteria.GE, minimumX.toString())
				.add(StarField.X.getName(), FilterCriteria.LT, maximumX.toString()).build();

		String uri = UriComponentsBuilder.newInstance().path("star")
				.queryParam(RestParameters.FILTER, filterCriteria.toString())
				.queryParam(RestParameters.SORT, sortCriteria.toString())
				.queryParam(RestParameters.DETAIL, RestDetail.MINIMAL)
				.queryParam(RestParameters.PAGE_SIZE, "999999")
				.build().toUriString();

		logger.info("Calling " + uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Star>> restResponse = restTemplate.exchange(getRestUrl(uri), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Star>>() {
				});
		SearchResult<Star> searchResults = restResponse.getBody();

		for (Star star : searchResults.getEntities()) {

			String sectorKey = star.getSectorKey();

			MapItemSetModel mapItemSet = mapItemSetByKey.get(sectorKey);

			if (mapItemSet == null) {
				mapItemSet = new MapItemSetModel();
				mapItemSet.setSectorKey(sectorKey);
				mapItemSet.setMapItemType(MapItemTypes.STAR);
				mapItemSet.setMapItemKeys(new ArrayList<String>());
				mapItemSet.setMapItemPoints(new ArrayList<Double>());

				mapItemSetByKey.put(sectorKey, mapItemSet);
			}

			mapItemSet.getMapItemKeys().add(star.getKey());
			mapItemSet.getMapItemPoints().add(star.getX());
			mapItemSet.getMapItemPoints().add(star.getY());
			mapItemSet.getMapItemPoints().add(star.getZ());
		}

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemSets(new ArrayList<MapItemSetModel>(mapItemSetByKey.values()));
		return response;
	}

	// private GetMapItemsResponse getMapItemsBySectorRest(String sectorKey, Integer
	// mapItemType) {
	//
	// RestTemplate restTemplate = createRestTempate();
	// ResponseEntity<Sector> restResponse =
	// restTemplate.exchange(getRestUrl("sector/" + sectorKey), HttpMethod.GET,
	// createHttpEntity(), new ParameterizedTypeReference<Sector>() {
	// });
	// Sector sector = restResponse.getBody();
	//
	// List<String> mapItemKeys = new ArrayList<String>();
	// List<Double> mapItemPoints = new ArrayList<Double>();
	// switch (mapItemType) {
	// case MapItemTypes.STAR:
	// // for (Star star : sector.getStars()) {
	// // mapItemKeys.add(star.getKey());
	// // mapItemPoints.add(star.getX());
	// // mapItemPoints.add(star.getY());
	// // mapItemPoints.add(star.getZ());
	// // }
	// break;
	// case MapItemTypes.SHIP:
	// // TBD
	// break;
	// default:
	// break;
	// }
	//
	// MapItemSetModel mapItemSet = new MapItemSetModel();
	// mapItemSet.setSectorKey(sectorKey);
	// mapItemSet.setMapItemType(mapItemType);
	// mapItemSet.setMapItemKeys(mapItemKeys);
	// mapItemSet.setMapItemPoints(mapItemPoints);
	//
	// List<MapItemSetModel> mapItemSets = new ArrayList<MapItemSetModel>();
	// mapItemSets.add(mapItemSet);
	//
	// GetMapItemsResponse response = new GetMapItemsResponse();
	// response.setMapItemSets(mapItemSets);
	//
	// return response;
	// }

	// @SuppressWarnings("unused")
	// private GetMapItemsResponse getMapItemsBySectorMock(String sectorKey, Integer
	// mapItemType) {
	// List<MockSpaceData.Point> allPoints;
	// switch (mapItemType) {
	// case MapItemTypes.STAR:
	// allPoints = m_mockSpaceData.getStars();
	// break;
	// case MapItemTypes.SHIP:
	// allPoints = m_mockSpaceData.getShips();
	// break;
	// default:
	// return null;
	// }
	//
	// SectorModel sector = m_mockSpaceData.getSectors().stream().filter(s ->
	// s.getKey().equals(sectorKey)).findFirst()
	// .get();
	//
	// List<MockSpaceData.Point> points = allPoints.stream()
	// .filter(p -> sector.getMinimumX() <= p.x && p.x < sector.getMaximumX() &&
	// sector.getMinimumY() <= p.y
	// && p.y < sector.getMaximumY() && sector.getMinimumZ() <= p.z && p.z <
	// sector.getMaximumZ())
	// .collect(Collectors.toList());
	//
	// List<String> mapItemKeys = new ArrayList<String>();
	// List<Double> mapItemPoints = new ArrayList<Double>();
	// for (MockSpaceData.Point point : points) {
	// mapItemKeys.add(point.key);
	// mapItemPoints.add(point.x);
	// mapItemPoints.add(point.y);
	// mapItemPoints.add(point.z);
	// }
	//
	// MapItemSetModel mapItemSet = new MapItemSetModel();
	// mapItemSet.setSectorKey(sectorKey);
	// mapItemSet.setMapItemType(mapItemType);
	// mapItemSet.setMapItemKeys(mapItemKeys);
	// mapItemSet.setMapItemPoints(mapItemPoints);
	//
	// List<MapItemSetModel> mapItemSets = new ArrayList<MapItemSetModel>();
	// mapItemSets.add(mapItemSet);
	//
	// GetMapItemsResponse response = new GetMapItemsResponse();
	// response.setMapItemSets(mapItemSets);
	//
	// return response;
	// }

	private String formatCoordinates(Double x, Double y, Double z) {
		if (x == null && y == null && z == null)
			return null;

		StringBuilder sb = new StringBuilder();
		if (x != null) {
			sb.append(x);
		}
		sb.append(", ");
		if (y != null) {
			sb.append(y);
		}
		sb.append(", ");
		if (z != null) {
			sb.append(z);
		}

		return sb.toString();
	}

	private void addPropertyGroup(List<MapItemPropertyGroupModel> propertyGroups, String title) {

		List<MapItemPropertyModel> properties = new ArrayList<MapItemPropertyModel>();
		MapItemPropertyGroupModel propertyGroup = new MapItemPropertyGroupModel();
		propertyGroup.setTitle(title);
		propertyGroup.setProperties(properties);

		propertyGroups.add(propertyGroup);
	}

	private void addProperty(List<MapItemPropertyGroupModel> propertyGroups, String title, String description,
			String url, Object value) {

		MapItemPropertyModel property = new MapItemPropertyModel();
		property.setTitle(title);
		property.setDescription(HtmlUtils.htmlEscape(description));
		property.setUrl(url);
		if (value != null) {
			property.setValue(value.toString());
		}

		propertyGroups.get(propertyGroups.size() - 1).getProperties().add(property);
	}

}
