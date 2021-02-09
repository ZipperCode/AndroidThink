package com.think.ui.draw;

import android.graphics.Color;
import android.graphics.Path;

import androidx.core.graphics.PathParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Parser {

    public static List<SvgBean> parse(InputStream in) throws Exception {
        List<SvgBean> list = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(in);

        NodeList elements = document.getElementsByTagName("path");

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String pathData = element.getAttribute("d");

            Path path = PathParser.createPathFromPathData(pathData);

            SvgBean svgBean = new SvgBean();
            svgBean.drawPath = path;
            svgBean.fillColor = Color.GRAY;

            list.add(svgBean);
        }
        return list;

    }

}
