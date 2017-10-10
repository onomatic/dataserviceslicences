package au.edu.soacourse.dataservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class grabData {
	
	
	private String classUrl = "http://www.rms.nsw.gov.au/about/corporate-publications/"
			+ "statistics/registrationandlicensing/tables/table215_2016";
	private String primaryUrl = "http://www.rms.nsw.gov.au/about/corporate-publications/statistics/"
			+ "registrationandlicensing/tables/table225_2016";
	
	private Map<String, License> m;
	
	
	private Map<String, License> parseTable(Map<String, License> m, String url, Integer i) throws IOException{
		Document doc = Jsoup.connect(url).get();
		Elements table = doc.select("table.table > tbody > tr");
		if (m == null) m = new HashMap<String, License>();
		for (Element e : table){
			String k = e.select("th").text();
			License l = m.get(k);
			if (l == null) l = new License();
			for (Element er : e.select("td")){
				if (i == 1){
					l.classData.add(er.text());
				}else{
					l.primaryData.add(er.text());
				}
			}
			m.put(k, l);
		}
		return m;
	}
	
	public Map<String, License> grab(Integer q){
		m = new HashMap<String, License>();
		try {
			m = parseTable(m, classUrl + "q" + q + ".html", 1);
			m = parseTable(m, primaryUrl + "q" + q + ".html", 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return m;
	}
	

}
