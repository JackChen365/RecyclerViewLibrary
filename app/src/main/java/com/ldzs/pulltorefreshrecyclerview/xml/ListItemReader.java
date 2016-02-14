package com.ldzs.pulltorefreshrecyclerview.xml;

import com.ldzs.pulltorefreshrecyclerview.model.ListItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cz on 16/1/27.
 */
@Config("list.xml")
public class ListItemReader extends AssetReader<Integer, ArrayList<ListItem>> {
    private int index;

    @Override
    public XmlParser.OnParserListener getParserListener(HashMap<Integer, ArrayList<ListItem>> configs) {
        return parser -> {
            String name = parser.getName();
            if ("listItem".equals(name)) {
                index = Integer.valueOf(parser.getAttributeValue(null, "name"));
                configs.put(index, new ArrayList<>());
            } else if ("item".equals(name)) {
                ArrayList<ListItem> listItems = configs.get(index);
                String attributeName = parser.getAttributeValue(null, "name");
                String clazz = parser.getAttributeValue(null, "class");
                listItems.add(new ListItem(attributeName, clazz));
            }
        };
    }
}
