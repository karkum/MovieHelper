package org.mad.app.moviehelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class YoutubeXMLParser {
	//TODO ONLY looking at top 10 results to save time
	private static final int MAX_RESULTS = 10;

	public ArrayList<Trailer> parseVideo(String xmlResult) {
		ArrayList<Trailer> trailers = new ArrayList<Trailer>();
		InputStream stream = new ByteArrayInputStream(xmlResult.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		Document doc;
		try {
			doc = db.parse(stream);
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Element root = doc.getDocumentElement();
		NodeList items = root.getElementsByTagName("entry");

		for (int i = 0; i < MAX_RESULTS; i++) {
			String link = "";
			int duration = 0;
			int viewCount = 0;
			String id = "";
			String label = "";
			String title = "";
			Node entry = items.item(i);
			for (int j = 0; j < entry.getChildNodes().getLength(); j++) {
				Node curr = entry.getChildNodes().item(j);
				if (curr.getNodeName().equals("id")) {
					String fullid = curr.getChildNodes().item(0).getNodeValue();
					int ind = fullid.indexOf("videos/");
					id = fullid.substring(ind+7);
				}
				if (curr.getNodeName().equals("media:group")) {
					for (int k = 0; k < curr.getChildNodes().getLength(); k++) {
						Node childOfCurr = curr.getChildNodes().item(k);
						if (childOfCurr.getNodeName().equals("media:content")) {
							if (childOfCurr.getAttributes()
									.getNamedItem("yt:format").getNodeValue()
									.equals("1")) {
								link = childOfCurr.getAttributes()
										.getNamedItem("url").getTextContent();
							}
						} else if (childOfCurr.getNodeName().equals(
								"yt:duration")) {
							duration = Integer.parseInt(childOfCurr
									.getAttributes().getNamedItem("seconds")
									.getNodeValue());

						} else if (childOfCurr.getNodeName().equals(
								"media:title")) {
							title = childOfCurr.getTextContent();
						} else if (childOfCurr.getNodeName().equals(
								"media:category")) {
							label = childOfCurr.getTextContent();
						}
					}
				} else if (curr.getNodeName().equals("yt:statistics")) {
					viewCount = Integer.valueOf(curr.getAttributes()
							.getNamedItem("viewCount").getTextContent());
				}
			}
			Trailer trailer = new Trailer(title, link, duration, viewCount, label, id);
			trailers.add(trailer);
		}

//		for (int i = 0; i < trailers.size(); i++) {
//			// find the best one in the trailers list
//			// check its media:category, if that doesnt work, check its title, if
//			// that doesnt work, check duration, view count
//			Trailer current = trailers.get(i);
//		}
		return trailers;
	}

}
