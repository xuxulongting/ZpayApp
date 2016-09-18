package com.spreadtrum.iit.zpayapp.message;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class MessageBuilder {

	private static XmlSerializer serializer = null;
	private static StringWriter writer = null;
	private static int taskIndex = 1;

	/**
	 * 拼接报文头
	 * @param seId
	 * @param requestType
     */
	private static void buildXML_Header(String seId,String requestType) {
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "tsmdbrequest");
			serializer.attribute(null, "version", "01");

			serializer.startTag(null, "SEIndex");
			serializer.text(seId);
			serializer.endTag(null, "SEIndex");

			serializer.startTag(null,"reqtype");
			serializer.text(requestType);
			serializer.endTag(null,"reqtype");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 拼接报文尾
	 * @return
	 */
	private static String buildXML_End() {
		try {
			serializer.endTag(null, "tsmdbrequest");
			serializer.endDocument();
			// writer.toString().trim();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return writer.toString().trim();
	}

	/**
	 * 拼接请求报文体（添加TSM任务接口）
	 * @param entity
     */
	private static void messageRequestTaskidHandle(RequestTaskidEntity entity){
		try {
			serializer.startTag(null,"reqdata");
			serializer.startTag(null,"tasktype");
			serializer.text(entity.tasktype);
			serializer.endTag(null,"tasktype");
			serializer.startTag(null,"taskcommand");
			serializer.text(entity.taskcommand);
			serializer.endTag(null,"taskcommand");
			serializer.endTag(null,"reqdata");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拼接报文体（应用数据查询请求）
	 * @param requestData
     */
	private static void messageRequestAppInfoHandle(String requestData) {
		try {
			serializer.startTag(null,"reqdata");
			serializer.text(requestData);
			serializer.endTag(null,"reqdata");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * SE返回数据组成业务报文
	 * 
	 * @param apdu
	 *            APDU对象
	 * @return
	 */
//	private static void message_Response_handle(CAPDUInformation apdu) {
//		try {
//			serializer.startTag(null, "RAPDUList");
//			serializer.startTag(null, "GAPDUInformation");
//			serializer.startTag(null, "index");
//			serializer.text(apdu.getIndex());
//			serializer.endTag(null, "index");
//			serializer.startTag(null, "APDU");
//			serializer.text(apdu.getAPDU());
//			serializer.endTag(null, "APDU");
//			serializer.startTag(null, "SW");
//			serializer.text(apdu.getSW());
//			serializer.endTag(null, "SW");
//			serializer.endTag(null, "GAPDUInformation");
//			serializer.endTag(null, "RAPDUList");
//			serializer.startTag(null, "Result");
//			serializer.text(apdu.getResult());
//			serializer.endTag(null, "Result");
//			serializer.startTag(null, "TaskIndex");
//			serializer.text(getTaskIndex());
//			serializer.endTag(null, "TaskIndex");
////			serializer.endTag(null, "business");
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//	/**
//	 * 获取授权码
//	 * @param businessType
//	 * @return
//	 */
//	public static String apply_authCode_Request(String businessType){
//		serializer = Xml.newSerializer();
//		writer = new StringWriter();
//		buildXML_Header(businessType);
//		String xml = buildXML_End();
//		return xml;
//	}

	/**
	 * 生成请求报文（应用数据查询）
	 * @param seId
	 * @param requestType
	 * @param requestData
     * @return
     */
	public static String doBussinessRequest(String seId, String requestType, String requestData) {
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		buildXML_Header(seId,requestType);
		messageRequestAppInfoHandle(requestData);
		String xml = buildXML_End();
		return xml;
	}

	/**
	 * 生成请求报文（添加TSM任务接口）
	 * @param seId
	 * @param requestType
	 * @param entity
     * @return
     */
	public static String doBussinessRequest(String seId,String requestType,RequestTaskidEntity entity){
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		buildXML_Header(seId,requestType);
		messageRequestTaskidHandle(entity);
		String xml = buildXML_End();
		return xml;
	}

//	/**
//	 * 应用报文APDU处理
//	 *
//	 * @param businessType
//	 *            交易类型
//	 * @param appAID
//	 *            待申请应用AID
//	 * @param apdu
//	 *            应用下载指令集
//	 * @return
//	 */
//	public static String do_Bussiness_Progress(String businessType, String appAID, CAPDUInformation apdu) {
//		serializer = Xml.newSerializer();
//		writer = new StringWriter();
//		buildXML_Header(businessType);
//		message_Response_handle(apdu);
//		String xml = buildXML_End();
//		return xml;
//	}

	private static String getTaskIndex() {
		DecimalFormat df = new DecimalFormat("0000");
		return df.format(++taskIndex);
	}

	/**
	 * 下载响应xml转换成实体
	 * 
	 * @param xml
	 * @return
	 */
	public static TSMResponseEntity parseDownLoadXml(String xml) {
		TSMResponseEntity entity = new TSMResponseEntity();
		AppInformation appInformation = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xml));

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("tsmdbrequest".equals(nodeName)) {
						entity.setVersion(parser.getAttributeValue(0));
					} else if ("reqtype".equals(nodeName)) {
						entity.setReqtype(parser.nextText());
					} else if ("result".equals(nodeName)) {
						entity.setResult(parser.nextText());
					} else if ("appinfomation".equals(nodeName)) {
						appInformation = new AppInformation();
					} else if ("index".equals(nodeName)) {
						appInformation.setIndex(parser.nextText());
					} else if ("picurl".equals(nodeName)) {
						appInformation.setPicurl(parser.nextText());
					} else if ("appname".equals(nodeName)) {
						appInformation.setAppname(parser.nextText());
					} else if ("appsize".equals(nodeName)) {
						appInformation.setAppsize(parser.nextText());
					} else if ("apptype".equals(nodeName)) {
						appInformation.setApptype(parser.nextText());
					} else if ("spname".equals(nodeName)) {
						appInformation.setSpname(parser.nextText());
					} else if ("appdesc".equals(nodeName)) {
						appInformation.setAppdesc(parser.nextText());
					} else if("appinstalled".equals(nodeName)){
						appInformation.setAppinstalled(parser.nextText());
					} else if ("appid".equals(nodeName)) {
						appInformation.setAppid(parser.nextText());
					} else if("taskid".equals(nodeName)){
						entity.setTaskId(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if("appinfomation".equals(nodeName)){
						entity.getAppInformationList().add(appInformation);
						//将appInformation存入数据库
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 操作结果
	 * 
	 * @param xml
	 * @return
	 */
	public static String getResultDes(String xml) {
		String res = "";
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xml));

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("resultDes".equals(nodeName)) {
						res = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}
