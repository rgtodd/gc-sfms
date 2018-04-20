package sfms.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sfms.rest.api.models.MapItemTypes;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.web.SfmsController;
import sfms.web.mock.MockSpaceData;
import sfms.web.models.ajax.GetMapItemsResponse;
import sfms.web.models.ajax.GetSectorResponse;
import sfms.web.models.ajax.GetSectorsResponse;
import sfms.web.models.ajax.SectorModel;

@RestController
@RequestMapping({ "/ajax" })
public class AjaxController extends SfmsController {

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

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemKeys(mapItemKeys);
		response.setMapItemPoints(mapItemPoints);

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

		GetMapItemsResponse response = new GetMapItemsResponse();
		response.setMapItemKeys(mapItemKeys);
		response.setMapItemPoints(mapItemPoints);

		return response;
	}

}
