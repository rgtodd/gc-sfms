package sfms.web;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import sfms.rest.RestUtility;
import sfms.rest.Secret;

public class SfmsController {

	protected <TEntity> HttpEntity<TEntity> createHttpEntity(TEntity entity) {
		return new HttpEntity<TEntity>(entity, createRequestHeaders());
	}

	protected HttpEntity<?> createHttpEntity() {
		return new HttpEntity<>(null, createRequestHeaders());
	}

	protected RestTemplate createRestTempate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	protected String getRestUrl(String url) {
		String host = SfmsProperties.INSTANCE.getProperty(SfmsProperties.APPLICATION, SfmsProperties.SFMS_REST_HOST);
		return host + "/" + url;
	}

	private MultiValueMap<String, String> createRequestHeaders() {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
		result.add(RestUtility.REST_AUTHORIZATION_TOKEN_HEADER_KEY, Secret.getRestAuthorizationToken());

		return result;
	}

}