package sfms.web.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import sfms.common.Secret;
import sfms.common.PropertyFile;
import sfms.rest.api.RestParameters;
import sfms.rest.api.RestUtility;
import sfms.storage.Storage;
import sfms.web.SfmsController;
import sfms.web.models.DebugEntryModel;
import sfms.web.models.DebugGenerateOptionsModel;

@Controller
@RequestMapping({ "/utility" })
public class UtilityController extends SfmsController {

	private final Logger logger = Logger.getLogger(UtilityController.class.getName());

	@GetMapping({ "" })
	public String debug(ModelMap modelMap) {

		List<DebugEntryModel> debugEntries = new ArrayList<DebugEntryModel>();

		DebugEntryModel debugEntry = new DebugEntryModel();
		debugEntry.setId("RestAuthorizationToken");
		debugEntry.setValue(Secret.getRestAuthorizationToken());
		debugEntries.add(debugEntry);

		modelMap.addAttribute("debugEntries", debugEntries);

		return "utility";
	}

	@GetMapping({ "/generateSpaceships" })
	public String generateSpaceships(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "utilityGenerateSpaceships";
	}

	@PostMapping({ "/generateSpaceshipsPost" })
	public String generateSpaceshipsPost(@ModelAttribute DebugGenerateOptionsModel options) {

		String url = getRestUrl("utility/generateSpaceships?count=" + options.getRecordCount().toString());

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				createHttpEntity(),
				new ParameterizedTypeReference<String>() {
				});

		logger.info("Response = " + response);

		return "redirect:/utility";
	}

	@GetMapping({ "/generateStarClusters" })
	public String generateStarClusters() {

		String url = getRestUrl("utility/generateClusters");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				createHttpEntity(),
				new ParameterizedTypeReference<String>() {
				});

		logger.info("Response = " + response);

		return "redirect:/utility";
	}

	@GetMapping({ "/generateCrewMembers" })
	public String generateCrewMembers(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "utilityGenerateCrewMembers";
	}

	@PostMapping({ "/generateCrewMembersPost" })
	public String generateCrewMembersPost(@ModelAttribute DebugGenerateOptionsModel options) {

		String url = getRestUrl("utility/generateCrewMembers?count=" + options.getRecordCount().toString());

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				createHttpEntity(),
				new ParameterizedTypeReference<String>() {
				});

		logger.info("Response = " + response);

		return "redirect:/utility";
	}

	@GetMapping({ "/upload" })
	public String upload() {
		return "utilityUpload";
	}

	@PostMapping({ "/uploadPost" })
	public String uploadPost(@RequestParam("ctrlFile") MultipartFile file) {

		ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
		String fileName = file.getOriginalFilename();
		String uploadedFileName = utcNow.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + fileName;

		String bucketName = "rgt-ssms.appspot.com";
		String blobName = "uploads/" + uploadedFileName;
		String contentType = file.getContentType();

		try (InputStream inputStream = file.getInputStream()) {
			try (WritableByteChannel writeChannel = Storage.getManager().getWritableByteChannel(bucketName, blobName,
					contentType)) {
				byte[] buffer = new byte[1024];
				int limit;
				while ((limit = inputStream.read(buffer)) >= 0) {
					writeChannel.write(ByteBuffer.wrap(buffer, 0, limit));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (PropertyFile.INSTANCE.isProduction()) {
			uploadPostSubmitTask(uploadedFileName);
		} else {
			uploadPostExecuteService(uploadedFileName);
		}

		return "redirect:/utility";
	}

	private void uploadPostExecuteService(String fileName) {

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(getRestUrl("task/processStarFile?" + RestParameters.FILE_NAME + "=" + fileName),
				HttpMethod.GET, createHttpEntity(), Object.class);
	}

	private void uploadPostSubmitTask(String fileName) {
		TaskOptions taskOptions = TaskOptions.Builder
				.withUrl("/task/processStarFile")
				.header(RestUtility.REST_AUTHORIZATION_TOKEN_HEADER_KEY, Secret.getRestAuthorizationToken())
				.method(Method.GET)
				.param(RestParameters.FILE_NAME, fileName);

		Queue queue = QueueFactory.getQueue("rest-tasks");
		queue.add(taskOptions);
	}
}
