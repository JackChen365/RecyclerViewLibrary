package com.ldzs.pulltorefreshrecyclerview.xml;

import android.util.Xml;

import com.ldzs.pulltorefreshrecyclerview.util.IOUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * 解析xml内容体
 *
 * @author momo
 * @Date 2014/11/25
 */
public class XmlParser {
    /**
     * 开始解析xml
     *
     * @param inputStream
     * @param listener
     */
    public static void startParser(InputStream inputStream, OnParserListener listener) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "utf-8");
            int eventType = parser.getEventType();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                if (null != listener) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            listener.startParser(parser);
                            break;
                        default:
                            break;
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }


    public interface OnParserListener {
        void startParser(XmlPullParser parser);
    }

}
