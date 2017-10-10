package au.edu.soacourse.dataservice;

import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;

public class createXml {
	
	private String[] columns = {"total", "C", "LR", "MR", "HR", 
			"HC", "MC", "R"};
	private String[] columns2 = {"total", "learner", "P1", "P2", "unrestricted"};
	
	public void createRawXml(List<Map<String, License>> ms, String fileName){
		Document d = new Document();
		Element root = new Element("quarters");
		for (int j = 1; j <= ms.size(); j++){
			Element q1 = new Element("quarter");
			q1.setAttribute("value", String.valueOf(j));
			Map<String, License> m = ms.get(j-1);
			for (String k : m.keySet()){
				Element postcode = new Element("postcode");
				postcode.setAttribute("value", k);
				Element classData = new Element("class");		
				for (int i = 0; i < m.get(k).classData.size(); i++){
					String text = m.get(k).classData.get(i);
					String title = columns[i];
					classData.addContent(new Element(title).setText(text));
				}
				Element primaryData = new Element("primary");
				for (int i = 0; i < m.get(k).primaryData.size(); i++){
					primaryData.addContent(new Element(columns2[i]).setText(m.get(k).primaryData.get(i)));
				}
				postcode.addContent(classData);
				postcode.addContent(primaryData);
				q1.addContent(postcode);
			}
			root.addContent(q1);
		}		
		d.setRootElement(root);
		try {
            FileWriter writer = new FileWriter(fileName);
            XMLOutputter outputter = new XMLOutputter(); 
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(d, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

}
