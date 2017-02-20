package monitor;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParser {
	
	public static Document loadTemplate(String templateFile){
		File template = new File(templateFile);
		Document doc = null;
		try {
			doc = Jsoup.parse(template, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	public static Document addItem(String templateFile, String content){
		Document doc = loadTemplate(templateFile);
		Elements body = doc.getElementsByTag("body");
		body.append("<span class='item'>"+content+"</span>");
		return doc;
	}
	
}
