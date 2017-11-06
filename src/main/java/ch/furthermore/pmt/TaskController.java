package ch.furthermore.pmt;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sample
 * <pre>
 * curl -s -d '{"task":{"callbackUrl":"http://foo", "fields":[{"name":"foo","label":"Foo","value":"default","type":"INPUT"}]}}' -H"Content-Type:application/json" https://pmt.furthermore.ch/tasks
 * curl https://pmt.furthermore.ch/pending/8bd2f85d-ae83-4899-a93d-1533c341b719
 * </pre>
 * 
 * Apache
 * <pre>
 * <IfModule mod_ssl.c>
 * <VirtualHost *:443>
 * RequestHeader set X-Forwarded-Proto "https"
 * RequestHeader set X-Forwarded-Port "443"
 * ServerName pmt.furthermore.ch
 * ServerAdmin pmtadmin@furthermore.ch
 * ErrorLog ${APACHE_LOG_DIR}/error.log
 * CustomLog ${APACHE_LOG_DIR}/access.log combined
 * ProxyPreserveHost On
 * ProxyPass / http://10.0.0.12:9090/
 * ProxyPassReverse / http://10.0.0.12:9090/
 * RewriteEngine on
 * RewriteCond %{SERVER_NAME} =pmt.furthermore.ch
 * SSLCertificateFile /etc/letsencrypt/live/pmt.furthermore.ch/fullchain.pem
 * SSLCertificateKeyFile /etc/letsencrypt/live/pmt.furthermore.ch/privkey.pem
 * Include /etc/letsencrypt/options-ssl-apache.conf
 * </VirtualHost>
 * </IfModule>
 * </pre>
 */
@SuppressWarnings("deprecation")
@Controller
public class TaskController {
	private final static Logger log = LoggerFactory.getLogger(TaskController.class);
	private final static ObjectMapper om = new ObjectMapper();
	
	@Autowired
	private TaskDAO taskDAO;
	
	@Autowired
	private MailClient mailClient;
	
	@Autowired
	private HttpServletRequest request;
	
	@RequestMapping("/done/{taskId}")
    public String showTaskDone(Task task, @PathVariable("taskId") String taskId) throws JsonParseException, JsonMappingException, IOException {
        return "taskdone";
    }
	
	@RequestMapping("/pending/{taskId}")
    public String showTask(Task task, @PathVariable("taskId") String taskId) throws JsonParseException, JsonMappingException, IOException {
		task.replaceWith(taskDAO.load(taskId));
		
        return "task";
    }
	
	@RequestMapping(value="/pending/{taskId}", method=RequestMethod.POST)
	public String saveTask(@Valid Task task, BindingResult bindingResult, @PathVariable("taskId") String taskId) throws JsonParseException, JsonMappingException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		task.enrichWith(taskDAO.load(taskId)); 
		
	    if (bindingResult.hasErrors()) {
	        return "task";
	    }
	    
	    if (task.getCallbackUrl() != null) {
		    Map<String,Object> data = new HashMap<>(); 
		    for (Field f : task.getFields()) {
		    	data.put(f.getName(), f.getValue());
		    }
		    
		    post(task.getCallbackUrl(), "text/plain", "application/json", om.writeValueAsString(data));
	    }
	    
	    return "redirect:/done/" + taskId;
	}
	
	@RequestMapping(path="/tasks",method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	CreateTaskResponse createTask(@RequestBody CreateTaskRequest request) throws NoSuchMethodException, IOException, ScriptException {
		CreateTaskResponse response = new CreateTaskResponse();
		try {
			taskDAO.insert(request.getTask());
			
			response.setSuccess(true);
			response.setTaskId(request.getTask().getId());
			
			if (request.getTask().getEmail() != null) {
				String taskUrl = serverUrlPrefix() + "pending/" + request.getTask().getId();
				String subject = (String)request.getTask().toMap().get("subject");
				if (subject == null) subject = "New Task";
				mailClient.send(request.getTask().getEmail(), subject, taskUrl);
			}
		}
		catch (Exception e) {
			response.setSuccess(false);
			response.setError(e.getMessage());
		}
		return response;
	}
	
	private String serverUrlPrefix() {
		/*
		 * Header x-forwarded-proto='https'
		 * Header x-forwarded-port='443'
		 * Header x-forwarded-host='pmt.furthermore.ch'
		 */
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
			String name = e.nextElement();
			
			log.info("Header {}='{}'", name, request.getHeader(name)); //TODO DEBUG log
		}
		
		StringBuilder dashboardUrlPrefix = new StringBuilder();
		
		if (request.getHeader("x-forwarded-proto") != null) {
			dashboardUrlPrefix.append(request.getHeader("x-forwarded-proto"));
		}
		else {
			dashboardUrlPrefix.append(request.getScheme()); 
		}
		
		dashboardUrlPrefix.append("://");
		
		if (request.getHeader("x-forwarded-host") != null) {
			dashboardUrlPrefix.append(request.getHeader("x-forwarded-host"));
		}
		else {
			dashboardUrlPrefix.append(request.getServerName());
		}
		
		dashboardUrlPrefix.append(":");
		
		if (request.getHeader("x-forwarded-port") != null) {
			dashboardUrlPrefix.append(request.getHeader("x-forwarded-port"));
		}
		else {
			dashboardUrlPrefix.append(request.getServerPort());
		}
		
		dashboardUrlPrefix.append(request.getContextPath());
		
		dashboardUrlPrefix.append("/");
		
		return dashboardUrlPrefix.toString();
	}
	
	private static String post(String url, String accept, String contentType, String data) 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException //FIXME avoid deprecated API 
	{
		CloseableHttpClient httpClient = HttpClients.custom()
                .setHostnameVerifier(new AllowAllHostnameVerifier())
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        return true; //FIXME temporary work-around for let's encrypt certs :(
                    }
                }).build())
                .build();
	    try {
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new StringEntity(data));
	        httpPost.setHeader("Content-type", contentType);
	        httpPost.setHeader("Accept", accept);
	        
	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
	        
	        String result = httpClient.execute(httpPost, responseHandler);
	        
	        log.info("HTTP POST '{}' to {} => '{}'", data, url, result); //FIXME debug log
	        
			return result;
	    } finally {
	    	httpClient.close();
	    }
	}
}
