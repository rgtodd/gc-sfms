package sfms.web.controllers;

import java.io.IOException;
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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import sfms.common.Constants;
import sfms.common.PropertyFile;
import sfms.common.Secret;
import sfms.rest.api.RestHeaders;
import sfms.rest.api.RestParameters;
import sfms.storage.StorageManagerUtility;
import sfms.web.SfmsController;
import sfms.web.models.DebugEntryModel;
import sfms.web.models.DebugGenerateOptionsModel;

@Controller
@RequestMapping({ "/utility" })
public class UtilityController extends SfmsController {

	private final Logger logger = Logger.getLogger(UtilityController.class.getName());

	private static final int TASK_RECORD_COUNT = 10000;

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
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntity(),
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
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntity(),
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
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntity(),
				new ParameterizedTypeReference<String>() {
				});

		logger.info("Response = " + response);

		return "redirect:/utility";
	}

	@GetMapping({ "/upload" })
	public String upload() {
		return "utilityUpload";
	}

	@GetMapping({ "/uploadComplete" })
	public String uploadComplete(@RequestParam(WebParameters.FILE_NAME) String fileName) throws IOException {

		String bucketName = Constants.CLOUD_STORAGE_BUCKET;
		String blobName = Constants.CLOUD_STOARGE_UPLOAD_FOLDER + "/" + fileName;

		// Exclude header line.
		//
		int lineCount = StorageManagerUtility.getLineCount(bucketName, blobName) - 1;

		int start = 0;
		while (start < lineCount) {

			int recordCount = lineCount - start;
			if (recordCount > TASK_RECORD_COUNT) {
				recordCount = TASK_RECORD_COUNT;
			}

			if (PropertyFile.INSTANCE.isProduction()) {
				uploadPostSubmitTask(fileName, start, recordCount);
			} else {
				uploadPostExecuteService(fileName, start, recordCount);
			}

			start += TASK_RECORD_COUNT;
		}

		return "redirect:/utility";
	}

	private void uploadPostExecuteService(String fileName, int start, int recordCount) {

		String url = "task/processStarFile";
		url += "?" + RestParameters.FILE_NAME + "=" + fileName;
		url += "&" + RestParameters.START + "=" + String.valueOf(start);
		url += "&" + RestParameters.COUNT + "=" + String.valueOf(recordCount);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(getRestUrl(url), HttpMethod.GET, createHttpEntity(), Object.class);
	}

	private void uploadPostSubmitTask(String fileName, int start, int recordCount) {
		TaskOptions taskOptions = TaskOptions.Builder.withUrl("/task/processStarFile")
				.header(RestHeaders.REST_AUTHORIZATION_TOKEN, Secret.getRestAuthorizationToken())
				.method(Method.GET).param(RestParameters.FILE_NAME, fileName)
				.param(RestParameters.START, String.valueOf(start))
				.param(RestParameters.COUNT, String.valueOf(recordCount));

		Queue queue = QueueFactory.getQueue("rest-tasks");
		queue.add(taskOptions);
	}
}
