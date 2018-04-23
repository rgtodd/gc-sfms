package sfms.web.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.IOUtils;

import sfms.common.Constants;
import sfms.rest.api.FilterCriteria;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.SectorField;
import sfms.storage.Storage;
import sfms.storage.StorageManagerUtility;
import sfms.storage.StorageManagerUtility.ObjectFactory;
import sfms.web.SfmsController;
import sfms.web.mock.MockSpaceData;
import sfms.web.models.MapItemTypes;
import sfms.web.models.ajax.GetMapItemsResponse;
import sfms.web.models.ajax.GetSectorResponse;
import sfms.web.models.ajax.GetSectorsResponse;
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

		GetSectorsResponse response = new GetSectorsResponse();
		response.setSectors(m_mockSpaceData.getSectors());

		return response;
	}

	@GetMapping({ "/getSectorByLocation" })
	public GetSectorResponse getSectorByLocation(
			@RequestParam("x") Double x,
			@RequestParam("y") Double y,
			@RequestParam("z") Double z) {

		SectorModel sector = m_mockSpaceData.getSectors()
				.stream()
				.filter(s -> s.getMinimumX() <= x && x < s.getMaximumX() && s.getMinimumY() <= y && y < s.getMaximumY()
						&& s.getMinimumZ() <= z && z < s.getMaximumZ())
				.findFirst()
				.get();

		GetSectorResponse response = new GetSectorResponse();
		response.setSector(sector);

		return response;
	}

	@GetMapping({ "/getSectorByKey" })
	public GetSectorResponse getSectorByKey(@RequestParam("key") String key) {

		SectorModel sector = m_mockSpaceData.getSectors()
				.stream()
				.filter(s -> s.getKey().equals(key))
				.findFirst()
				.get();

		GetSectorResponse response = new GetSectorResponse();
		response.setSector(sector);

		return response;
	}

	@GetMapping({ "/getMapItemsBySector" })
	public GetMapItemsResponse getMapItemsBySector(
			@RequestParam("sectorKey") String sectorKey,
			@RequestParam("mapItemType") Integer mapItemType) {

		return getMapItemsBySectorRest(sectorKey, mapItemType);
	}

	@GetMapping({ "/getMapItemsByRank/get" })
	public GetMapItemsResponse getMapItemsByRank(
			@RequestParam("rank") Long rank) {

		GetMapItemsResponse response = getMapItemsByRankRest(rank);

		return response;
	}

	@GetMapping({ "/getMapItemsByRank" })
	public void getMapItemsByRankCached(
			@RequestParam("rank") Long rank,
			HttpServletResponse response) throws Exception {

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

		try (ReadableByteChannel readChannel = StorageManagerUtility.getCachedObject(
				Storage.getManager(),
				objectName,
				Constants.CONTENT_TYPE_JSON,
				objectFactory);
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

		FilterCriteria filterCriteria = FilterCriteria.newBuilder()
				.add(SectorField.MinimumX.getName(), FilterCriteria.GE, minimumX.toString())
				.add(SectorField.MinimumX.getName(), FilterCriteria.LT, maximumX.toString())
				.build();

		String uri = UriComponentsBuilder.newInstance()
				.path("sector")
				.queryParam(RestParameters.FILTER, filterCriteria.toString())
				.queryParam(RestParameters.DETAIL, "star")
				.build()
				.toUriString();

		logger.info("Calling " + uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Sector>> restResponse = restTemplate.exchange(
				getRestUrl(uri),
				HttpMethod.GET,
				createHttpEntity(),
				new ParameterizedTypeReference<SearchResult<Sector>>() {
				});
		SearchResult<Sector> searchResults = restResponse.getBody();

		List<MapItemSetModel> mapItemSets = new ArrayList<MapItemSetModel>();
		for (Sector sector : searchResults.getEntities()) {

			// Add sector stars.
			//
			{
				List<String> mapItemKeys = new ArrayList<String>();
				List<Double> mapItemPoints = new ArrayList<Double>();
				for (Star star : sector.getStars()) {
					mapItemKeys.add(star.getKey());
					mapItemPoints.add(star.getX());
					mapItemPoints.add(star.getY());
					mapItemPoints.add(star.getZ());
				}

				MapItemSetModel mapItemSet = new MapItemSetModel();
				mapItemSet.setSectorKey(sector.getKey());
				mapItemSet.setMapItemType(MapItemTypes.STAR);
				mapItemSet.setMapItemKeys(mapItemKeys);
				mapItemSet.setMapItemPoints(mapItemPoints);

				mapItemSets.add(mapItemSet);
			}

			// Add sector ships.
			//
			{
				// TODO
			}
		}

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemSets(mapItemSets);
		return response;
	}

	private GetMapItemsResponse getMapItemsBySectorRest(String sectorKey, Integer mapItemType) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> restResponse = restTemplate.exchange(getRestUrl("sector/" + sectorKey),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});
		Sector sector = restResponse.getBody();

		List<String> mapItemKeys = new ArrayList<String>();
		List<Double> mapItemPoints = new ArrayList<Double>();
		switch (mapItemType) {
		case MapItemTypes.STAR:
			for (Star star : sector.getStars()) {
				mapItemKeys.add(star.getKey());
				mapItemPoints.add(star.getX());
				mapItemPoints.add(star.getY());
				mapItemPoints.add(star.getZ());
			}
			break;
		case MapItemTypes.SHIP:
			// TBD
			break;
		default:
			break;
		}

		MapItemSetModel mapItemSet = new MapItemSetModel();
		mapItemSet.setSectorKey(sectorKey);
		mapItemSet.setMapItemType(mapItemType);
		mapItemSet.setMapItemKeys(mapItemKeys);
		mapItemSet.setMapItemPoints(mapItemPoints);

		List<MapItemSetModel> mapItemSets = new ArrayList<MapItemSetModel>();
		mapItemSets.add(mapItemSet);

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemSets(mapItemSets);

		return response;
	}

	@SuppressWarnings("unused")
	private GetMapItemsResponse getMapItemsBySectorMock(String sectorKey, Integer mapItemType) {
		List<MockSpaceData.Point> allPoints;
		switch (mapItemType) {
		case MapItemTypes.STAR:
			allPoints = m_mockSpaceData.getStars();
			break;
		case MapItemTypes.SHIP:
			allPoints = m_mockSpaceData.getShips();
			break;
		default:
			return null;
		}

		SectorModel sector = m_mockSpaceData.getSectors()
				.stream()
				.filter(s -> s.getKey().equals(sectorKey))
				.findFirst()
				.get();

		List<MockSpaceData.Point> points = allPoints
				.stream()
				.filter(p -> sector.getMinimumX() <= p.x && p.x < sector.getMaximumX() && sector.getMinimumY() <= p.y
						&& p.y < sector.getMaximumY()
						&& sector.getMinimumZ() <= p.z && p.z < sector.getMaximumZ())
				.collect(Collectors.toList());

		List<String> mapItemKeys = new ArrayList<String>();
		List<Double> mapItemPoints = new ArrayList<Double>();
		for (MockSpaceData.Point point : points) {
			mapItemKeys.add(point.key);
			mapItemPoints.add(point.x);
			mapItemPoints.add(point.y);
			mapItemPoints.add(point.z);
		}

		MapItemSetModel mapItemSet = new MapItemSetModel();
		mapItemSet.setSectorKey(sectorKey);
		mapItemSet.setMapItemType(mapItemType);
		mapItemSet.setMapItemKeys(mapItemKeys);
		mapItemSet.setMapItemPoints(mapItemPoints);

		List<MapItemSetModel> mapItemSets = new ArrayList<MapItemSetModel>();
		mapItemSets.add(mapItemSet);

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemSets(mapItemSets);

		return response;
	}

}
