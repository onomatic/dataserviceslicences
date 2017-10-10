package au.edu.soacourse.dataservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.XdmValue;

public class XQueryProcessor {
		
	public String executeXQuery(InputStream xquery, String xml, Map<String, String> params) throws SaxonApiException, IOException{
		Processor saxon = new Processor(false);
		XQueryCompiler compiler = saxon.newXQueryCompiler();
		XQueryExecutable exec = compiler.compile(xquery);
		XQueryEvaluator query = exec.load();
		for (String s : params.keySet() ){
			query.setExternalVariable(new QName(s), new XdmAtomicValue(params.get(s)));
		}
		query.setExternalVariable(new QName("content"), new XdmAtomicValue(xml));
		XdmValue results = query.evaluate();
		return results.toString();
	}
	
	public String executeXQuery(InputStream xquery, String xml, List<String> params) throws SaxonApiException, IOException{
		Processor saxon = new Processor(false);
		XQueryCompiler compiler = saxon.newXQueryCompiler();
		XQueryExecutable exec = compiler.compile(xquery);
		XQueryEvaluator query = exec.load();
		query.setExternalVariable(new QName("names"), XdmValue.makeSequence(params));
		query.setExternalVariable(new QName("content"), new XdmAtomicValue(xml));
		XdmValue results = query.evaluate();
		return results.toString();
	}

}
