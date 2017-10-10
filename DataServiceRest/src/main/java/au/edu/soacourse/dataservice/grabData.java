package au.edu.soacourse.dataservice;

import java.io.File;
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
	
	
	private static String executeXQuery(String filename) throws SaxonApiException, IOException{
		Processor saxon = new Processor(false);
		XQueryCompiler compiler = saxon.newXQueryCompiler();
		XQueryExecutable exec = compiler.compile(new File(filename));
		DocumentBuilder builder = saxon.newDocumentBuilder();
		Source src = new StreamSource(new File("licence.xml"));
		XdmNode doc = builder.build(src);
		
		XQueryEvaluator query = exec.load();
		query.setContextItem(doc);
		query.setExternalVariable(new QName("quarter"), new XdmAtomicValue("1"));
		query.setExternalVariable(new QName("postcode"), new XdmAtomicValue(""));

		XdmValue result = query.evaluate();
		System.out.println(result.toString());
		return filename;
	}
	
	public static void main(String [] args){
		grabData gd = new grabData();
		ArrayList<Map<String,License>> l = new ArrayList();
		for (int i = 1; i <= 4; i++){
			l.add(gd.grab(i));
		}
		createXml cx = new createXml();
		cx.createRawXml(l, "license.xml");
		
		
	}
	
	
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
