package sfms.web;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import sfms.common.Secret;
import sfms.common.Constants;
import sfms.common.PropertyFile;

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

	protected UriBuilder getUriBuilder() {
		String host = PropertyFile.INSTANCE.getServerProperty(PropertyFile.APPLICATION, PropertyFile.SFMS_REST_HOST);
		UriBuilder result = UriComponentsBuilder.fromUriString(host);
		return result;
	}

	protected String getRestUrl(String url) {
		String host = PropertyFile.INSTANCE.getServerProperty(PropertyFile.APPLICATION, PropertyFile.SFMS_REST_HOST);
		return host + "/" + url;
	}

	private MultiValueMap<String, String> createRequestHeaders() {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
		result.add(Constants.REST_AUTHORIZATION_TOKEN_HEADER_KEY, Secret.getRestAuthorizationToken());

		return result;
	}

}
