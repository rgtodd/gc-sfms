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

import sfms.rest.api.models.ObjectTypes;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.web.SfmsController;
import sfms.web.mock.MockSpaceData;
import sfms.web.models.ajax.GetObjectsResponse;
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

	@GetMapping({ "/getObjectsBySector" })
	public GetObjectsResponse getObjectsBySector(
			@RequestParam("sectorKey") String sectorKey,
			@RequestParam("objectType") Integer objectType) {

		return getObjectsBySectorRest(sectorKey, objectType);
	}

	private GetObjectsResponse getObjectsBySectorRest(String sectorKey, Integer objectType) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> restResponse = restTemplate.exchange(getRestUrl("sector/" + sectorKey),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});
		Sector sector = restResponse.getBody();

		List<String> objectKeys = new ArrayList<String>();
		List<Double> objectPoints = new ArrayList<Double>();
		switch (objectType) {
		case ObjectTypes.STAR:
			for (Star star : sector.getStars()) {
				objectKeys.add(star.getKey());
				objectPoints.add(star.getX());
				objectPoints.add(star.getY());
				objectPoints.add(star.getZ());
			}
			break;
		case ObjectTypes.SHIP:
			// TBD
			break;
		default:
			break;
		}

		GetObjectsResponse response = new GetObjectsResponse();
		response.setObjectKeys(objectKeys);
		response.setObjectPoints(objectPoints);

		return response;
	}

	@SuppressWarnings("unused")
	private GetObjectsResponse getObjectsBySectorMock(String sectorKey, Integer objectType) {
		List<MockSpaceData.Point> allPoints;
		switch (objectType) {
		case ObjectTypes.STAR:
			allPoints = m_mockSpaceData.getStars();
			break;
		case ObjectTypes.SHIP:
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

		List<String> objectKeys = new ArrayList<String>();
		List<Double> objectPoints = new ArrayList<Double>();
		for (MockSpaceData.Point point : points) {
			objectKeys.add(point.key);
			objectPoints.add(point.x);
			objectPoints.add(point.y);
			objectPoints.add(point.z);
		}

		GetObjectsResponse response = new GetObjectsResponse();
		response.setObjectKeys(objectKeys);
		response.setObjectPoints(objectPoints);

		return response;
	}

}
