//package Util;
//
//import Bean.MECServerBean;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @author: daqige
// * @date: 2020/2/17 10:47
// */
//public class IoVXMLReader {
//
//
//    public  Element  XMLinit(String xmlPath) throws DocumentException {
//
//        SAXReader saxReader=new SAXReader();
//
//        Document document =saxReader.read(xmlPath);
//
//        return document.getRootElement();//获取root
//    }
//
//    public List<> readXML (String path) {
//        try {
//            InitMECServer();
//
//            Element root=XMLinit(path);
//
//            Element App;
//
//            Iterator<Element> iterator = root.elementIterator("APP");
//            /*main方法运行时，先初始化实例，获得请求总数，以便创建决策变量*/
//            while (iterator.hasNext()) {
//                App = (Element) iterator.next();
//                TotalDevicesCount++ ;
//                ArrayList<Element> itemList=(ArrayList<Element>) App.elements("item");
//                if (!itemList.isEmpty()) {
//                    TotalRequestAmount += itemList.size();
//                }
//            }
//
//        } catch (DocumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public List<Integer> readXML (String path) throws DocumentException {
//        int mecCount = 0 ;
//        Element root = XMLinit(StaticParam.MECSERVERS_XMLPATH);
//
//        Iterator<Element> iterator = root.elementIterator("MEC");
//        while (iterator.hasNext()) {
//            Element mecItem = (Element) iterator.next();
//
//            MECServerBean mecServer= new MECServerBean(
//                    Integer.parseInt(mecItem.elementText("cw"))
//                    ,Float.parseFloat(mecItem.elementText("frequency")));
//            MECList.add(mecServer);
//            mecCount++;
//        }
//
//        StaticParam.ServerAmount=mecCount;
//    }
//}
