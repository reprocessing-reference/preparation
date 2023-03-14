package com.csgroup.reprodatabaseline.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.csgroup.reprodatabaseline.config.UrlsConfiguration;
import com.csgroup.reprodatabaseline.datamodels.AuxFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuxipAccess {
	private static final Logger LOG = LoggerFactory.getLogger(AuxipAccess.class);

	private final HttpHandler httpHandler;

	private final UrlsConfiguration config;

	public AuxipAccess(HttpHandler handler, UrlsConfiguration conf) {
		this.httpHandler = handler;
		this.config = conf;
	}

	public List<String> getListOfAuxFileURLs(final List<AuxFile> files, String bearerToken) throws Exception {
		List<String> res = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		for (AuxFile f : files) {
			String post = httpHandler.getPost(
					config.getAuxip_url() + "/Products?$filter=contains(Name,'" + f.FullName.trim() + "\')",
					bearerToken);
			JsonNode currentObj;
			try {
				currentObj = mapper.readTree(post.replaceAll("@", ""));
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("Malformed json response");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("Malformed json response");
			}
			JsonNode valueNode = currentObj.get("value");
			if (valueNode.isArray()) {
				if (valueNode.size() != 1) {
					throw new Exception("Not the correct number of element returned");
				}
				for (JsonNode value : valueNode) {
					/*
					 * {"@odata.context":"$metadata#Products",
					 * "value":[{"@odata.mediaContentType":"application/json","Id":
					 * "ffc183a9-7555-4427-b246-176e2485abed", "Name":
					 * "S2B_OPER_GIP_G2PARA_MPC__20170206T103032_V20170101T000000_21000101T000000_B00.TGZ",
					 * "ContentType":"application/octet-stream","ContentLength":3039,
					 * "OriginDate":"2017-02-06T09:30:32Z","PublicationDate":
					 * "2021-03-12T10:26:33.658341Z", "EvictionDate":"2123-08-27T09:26:33.658323Z",
					 * "Checksum":[{"ChecksumDate":"2021-03-12T10:26:33.658341Z","Algorithm":"md5",
					 * "Value":"551bb28d4f81a94e866369fa49f89760"}],
					 * "ContentDate":{"Start":"2016-12-31T23:00:00Z","End":"2099-12-31T23:00:00Z"}}]
					 * }
					 */
					UUID id = UUID.fromString(value.get("Id").asText());

					// String post_wasabi = httpHandler.getLocation(config.getAuxip_url()+
					// "/Products("+id.toString()+")/$value", bearerToken);

					String auxipLink = config.getAuxip_url() + "/Products(" + id.toString() + ")/$value";

					res.add(auxipLink);
				}
			}
		}
		return res;
	}

	public void setAuxFileUrls(List<AuxFile> files, String bearerToken)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		for (AuxFile f : files) {
			if (f.AuxipUrl == null) {
				String post = httpHandler.getPost(
						config.getAuxip_url() + "/Products?$filter=contains(Name,'" + f.FullName.trim() + "\')",
						bearerToken);
				JsonNode currentObj;
				try {
					currentObj = mapper.readTree(post.replaceAll("@", ""));
				} catch (JsonMappingException e) {
					e.printStackTrace();
					throw new Exception("Malformed json response");
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					throw new Exception("Malformed json response");
				}
				JsonNode valueNode = currentObj.get("value");
				if (valueNode.isArray()) {
					if (valueNode.size() != 1) {
						throw new Exception("Not the correct number of element returned");
					}
					for (JsonNode value : valueNode) {
						/*
						 * {"@odata.context":"$metadata#Products",
						 * "value":[{"@odata.mediaContentType":"application/json","Id":
						 * "ffc183a9-7555-4427-b246-176e2485abed", "Name":
						 * "S2B_OPER_GIP_G2PARA_MPC__20170206T103032_V20170101T000000_21000101T000000_B00.TGZ",
						 * "ContentType":"application/octet-stream","ContentLength":3039,
						 * "OriginDate":"2017-02-06T09:30:32Z","PublicationDate":
						 * "2021-03-12T10:26:33.658341Z", "EvictionDate":"2123-08-27T09:26:33.658323Z",
						 * "Checksum":[{"ChecksumDate":"2021-03-12T10:26:33.658341Z","Algorithm":"md5",
						 * "Value":"551bb28d4f81a94e866369fa49f89760"}],
						 * "ContentDate":{"Start":"2016-12-31T23:00:00Z","End":"2099-12-31T23:00:00Z"}}]
						 * }
						 */
						UUID id = UUID.fromString(value.get("Id").asText());

						// String post_wasabi = httpHandler.getLocation(config.getAuxip_url()+
						// "/Products("+id.toString()+")/$value", bearerToken);

						String auxipLink = config.getAuxip_url() + "/Products(" + id.toString() + ")/$value";
						f.AuxipUrl = auxipLink;
					}
				}
			}

		}
		// return res;
	}
}
