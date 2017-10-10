package au.edu.soacourse.dataservice;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

@Path("/class-and-type")
public class licenses implements InitializingBean{
	@Autowired ServletContext servletContext=null;

	
	private String readInputStream(InputStream s){
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		try {
			while ((length = s.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			return result.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String filterXml(String filter, String xml) throws SaxonApiException, IOException{
		if (filter == null) return xml;
		XQueryProcessor xq = new XQueryProcessor();
    	String quarter = null;
    	String postcode = null;
    	Pattern q = Pattern.compile(".*quarter eq ([1-4]).*");
    	Matcher mq = q.matcher(filter);
    	if (mq.matches()) quarter = mq.group(1);
    	Pattern p = Pattern.compile(".*postcode eq (\\d{4}).*");
    	Matcher mp = p.matcher(filter);
    	if (mp.matches()) postcode = mp.group(1);
    	if (quarter == null) quarter = "";
    	if (postcode == null) postcode = "";
    	Map<String, String> ps = new HashMap();
    	ps.put("quarter", quarter);
    	ps.put("postcode", postcode);
    	InputStream simpleExtractPath = servletContext.getResourceAsStream("/WEB-INF/SimpleExtract.xq");
		return xq.executeXQuery(simpleExtractPath, xml, ps);
	}
	
	private String selectXml(String select, String xml) throws SaxonApiException, IOException{
		if (select == null) return xml;
		ArrayList<String> selects = new ArrayList();
    	selects = new ArrayList<String>(Arrays.asList(select.split("\\s*,\\s*")));
    	for (int i = 0; i < selects.size(); i++){
    		String s = selects.get(i);
    		Pattern p = Pattern.compile(".*Class (\\w).*");
    		Matcher mp = p.matcher(s);
    		if (mp.matches()){
    			selects.set(i, mp.group(1));;
    		}
    	}
    	InputStream simpleSelect = servletContext.getResourceAsStream("/WEB-INF/SimpleSelect.xq");
    	XQueryProcessor xq = new XQueryProcessor();
		return xq.executeXQuery(simpleSelect, xml, selects);
	}
	
	private String orderXml(String orderby, String xml) throws SaxonApiException, IOException{
		if (orderby == null) return xml;
		String order = null;
		Pattern p = Pattern.compile(".*Class (\\w).*");
		Matcher mp = p.matcher(orderby);
		if (mp.matches()){
			order = mp.group(1);
		}else{
			order = orderby;
		}
    	InputStream simpleSelect = servletContext.getResourceAsStream("/WEB-INF/filterPostcode.xq");
    	XQueryProcessor xq = new XQueryProcessor();
    	Map<String, String> ps = new HashMap();
    	ps.put("name", orderby);
		return xq.executeXQuery(simpleSelect, xml, ps);
	}

    @GET
    @Produces("application/xml")
    public String getData(@QueryParam("filter") String filter, @QueryParam("orderby") String orderby,
    		@QueryParam("select") String select) {
    	InputStream xmlStream = servletContext.getResourceAsStream("/licence.xml");
    	String xml = readInputStream(xmlStream);
        try {
			xml = filterXml(filter, xml);
			xml = selectXml(select, xml);
			xml = orderXml(orderby, xml);
			return xml;
		} catch (SaxonApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

    }


	@Override
	public void afterPropertiesSet() throws Exception {
		grabData gd = new grabData();
		ArrayList<Map<String,License>> l = new ArrayList();
		for (int i = 1; i <= 4; i++){
			l.add(gd.grab(i));
		}
		createXml cx = new createXml();
		cx.createRawXml(l, servletContext.getRealPath("/licence.xml"));		
	}
}

