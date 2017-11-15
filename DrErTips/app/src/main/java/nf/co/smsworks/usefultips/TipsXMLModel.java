package nf.co.smsworks.usefultips;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madan on 6/23/17.
 */

public class TipsXMLModel {
    public String name;
    public String desc;
    public String image;

    public List<Title> titleList=new ArrayList<>();


    public void getTitleListModels(List<ListModel> listModels){
        for (Title title : titleList) {
            ListModel listModel=new ListModel();
            title.getListModel(listModel);
            listModels.add(listModel);
        }
    }

    public void decodeFromDOM(Document document){
        Element documentElement = document.getDocumentElement();
        name=documentElement.getAttribute("name");
        desc=documentElement.getAttribute("desc");
        image=documentElement.getAttribute("image");

        NodeList nodeLists = documentElement.getElementsByTagName("title");
        Log.e("ISThisRunning", "Why????"+nodeLists.getLength());
        for (int i=0; i<nodeLists.getLength(); i++){
            Node item = nodeLists.item(i);
            if (item.getNodeType()==Node.ELEMENT_NODE){
                Element node= (Element) item;
                Title title=new Title();
                title.desc=node.getAttribute("desc");
                title.image=node.getAttribute("image");

                NodeList imageList = node.getElementsByTagName("image");
                for (int j=0; j<nodeLists.getLength(); j++){
                    Node image = imageList.item(j);
                    if (image==null) continue;
                    if (image.getNodeType()==Node.ELEMENT_NODE){
                        Element imageElement= (Element) image;
                        title.images.add(imageElement.getAttribute("ref"));
                    }
                }

                NodeList tipsList = node.getElementsByTagName("list");
                for (int j=0; j<tipsList.getLength(); j++){
                    Node tip = tipsList.item(j);
                    if (tip.getNodeType()==Node.ELEMENT_NODE){
                        Element tipElement= (Element) tip;
                        title.tipsList.add(tipElement.getTextContent());
                    }
                }
                titleList.add(title);
            }
        }
    }

    public static class Title{
        public String desc;
        public String image;

        public List<String> images=new ArrayList<>();
        public List<String> tipsList=new ArrayList<>();

        public void getListModel(ListModel listModel){
            listModel.title=desc;
            listModel.image=image;
            try {
                listModel.desc=tipsList.get(0);
            }catch (Exception e){

            }

        }

        public void logCat() {

            Log.e("Tip", desc);
            Log.e("Tip", image);

            for (String tip :
                    images) {
                Log.e("Tip", tip);
            }
            for (String tip :
                    tipsList) {
                Log.e("Tip", tip);
            }
        }
    }

    public void logCat(){
        for (Title title: titleList){
            title.logCat();
        }
    }
}
