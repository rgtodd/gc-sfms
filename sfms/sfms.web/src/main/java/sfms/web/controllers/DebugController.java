package sfms.web.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import sfms.rest.api.CreateResult;
import sfms.rest.api.RestUtility;
import sfms.rest.api.Secret;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.test.ValueGenerator;
import sfms.web.SfmsController;
import sfms.web.models.DebugEntryModel;
import sfms.web.models.DebugGenerateOptionsModel;

@Controller
public class DebugController extends SfmsController {

	@GetMapping({ "/debug" })
	public String debug(ModelMap modelMap) {

		List<DebugEntryModel> debugEntries = new ArrayList<DebugEntryModel>();

		DebugEntryModel debugEntry = new DebugEntryModel();
		debugEntry.setId("RestAuthorizationToken");
		debugEntry.setValue(Secret.getRestAuthorizationToken());
		debugEntries.add(debugEntry);

		modelMap.addAttribute("debugEntries", debugEntries);

		return "debug";
	}

	@GetMapping({ "/debug_generateSpaceships" })
	public String generateSpaceships(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "debugGenerateSpaceships";
	}

	@PostMapping({ "/debug_generateSpaceshipsPost" })
	public String generateSpaceshipsPost(@ModelAttribute DebugGenerateOptionsModel options) {

		RestTemplate restTemplate = createRestTempate();

		for (int idx = 0; idx < options.getRecordCount(); ++idx) {
			String name = "USS " + ValueGenerator.getRandomAdjective() + " " + ValueGenerator.getRandomNoun();

			Spaceship spaceship = new Spaceship();
			spaceship.setName(name);

			@SuppressWarnings("unused")
			ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(
					getRestUrl("spaceship"),
					HttpMethod.POST,
					createHttpEntity(spaceship),
					new ParameterizedTypeReference<CreateResult<String>>() {
					});
		}

		return "redirect:/debug";
	}

	@GetMapping({ "/debug_generateCrewMembers" })
	public String generateCrewMembers(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "debugGenerateCrewMembers";
	}

	@PostMapping({ "/debug_generateCrewMembersPost" })
	public String generateCrewMembersPost(@ModelAttribute DebugGenerateOptionsModel options) {

		RestTemplate restTemplate = createRestTempate();

		for (int idx = 0; idx < options.getRecordCount(); ++idx) {
			CrewMember crewMember = new CrewMember();
			crewMember.setFirstName(ValueGenerator.getRandomFirstName());
			crewMember.setLastName(ValueGenerator.getRandomLastName());

			@SuppressWarnings("unused")
			ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(
					getRestUrl("crewMember"),
					HttpMethod.POST,
					createHttpEntity(crewMember),
					new ParameterizedTypeReference<CreateResult<String>>() {
					});
		}

		return "redirect:/debug";
	}

	@GetMapping({ "/debug_upload" })
	public String upload() {
		return "debugUpload";
	}

	@PostMapping({ "/debug_uploadPost" })
	public String uploadPost(@RequestParam("ctrlFile") MultipartFile file) {

		ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
		String fileName = file.getOriginalFilename();
		String uploadedFileName = utcNow.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + fileName;

		String bucketName = "rgt-ssms.appspot.com";
		String blobName = "uploads/" + uploadedFileName;
		BlobId blobId = BlobId.of(bucketName, blobName);

		BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
				.setContentType(file.getContentType())
				.build();

		try (InputStream inputStream = file.getInputStream()) {
			Storage storage = StorageOptions.getDefaultInstance().getService();
			try (WriteChannel writeChannel = storage.writer(blobInfo)) {
				byte[] buffer = new byte[1024];
				int limit;
				while ((limit = inputStream.read(buffer)) >= 0) {
					writeChannel.write(ByteBuffer.wrap(buffer, 0, limit));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// uploadPostExecuteService(uploadedFileName);
		uploadPostSubmitTask();

		return "redirect:/debug";
	}

	@SuppressWarnings("unused")
	private void uploadPostExecuteService(String uploadedFileName) {
		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(getRestUrl("task/processStarFile?filename=" + uploadedFileName),
				HttpMethod.GET, createHttpEntity(), Object.class);
	}

	private void uploadPostSubmitTask() {
		TaskOptions taskOptions = TaskOptions.Builder
				.withUrl("/task/processStarFile")
				.header(RestUtility.REST_AUTHORIZATION_TOKEN_HEADER_KEY, Secret.getRestAuthorizationToken())
				.method(Method.GET)
				.param("filename", "20180331_213222_hygdata_v3.csv");

		Queue queue = QueueFactory.getQueue("rest-tasks");
		queue.add(taskOptions);
	}
}
