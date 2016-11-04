package com.spreadtrum.iit.zpayapp.message;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.spreadtrum.iit.zpayapp.common.ByteUtil;

public class MessageBuilder {

	private static XmlSerializer serializer = null;
	private static StringWriter writer = null;
	private static int taskIndex = 1;

	/**
	 * 生成添加TSM业务请求的TaskId的请求实体
	 * @param appInformation
	 * @param taskType
     * @return
     */
	public static RequestTaskidEntity getRequestTaskidEntity(AppInformation appInformation,String taskType){
		RequestTaskidEntity entity=new RequestTaskidEntity();
		String appid = appInformation.getAppid();
		byte[] bAppid = new byte[5];
		byte[] data = ByteUtil.StringToByteArray(appid);
		System.arraycopy(data,0,bAppid,5-data.length,data.length);
		entity.setTasktype(taskType);
		String strCmd = taskType+"05"+ByteUtil.bytesToString(bAppid,5);
		entity.setTaskcommand(strCmd);
		return entity;
	}

	/**
	 * 拼接报文头，访问TSM数据库（应用数据查询/添加TSM任务)
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
	 * 拼接报文尾,访问TSM数据库（应用数据查询/添加TSM任务)
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

	/**
	 * 解析访问TSM数据库请求的响应xml
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
	 * 获取TSM返回结果（成功 or 失败）
	 * 
	 * @param xml
	 * @return 0为处理成功，非零为处理异常
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
					if ("result".equals(nodeName)) {
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
//			e.printStackTrace();
			return null;
		}
		return res;
	}

	/**
	 * 生成客户端业务发起请求数据XML
	 * @param seId
	 * @param imei
	 * @param phone
	 * @param sessionId
     * @param taskId
     */
	public static String buildBussinessRequestXml(String seId,String imei,String phone,
												String sessionId,String taskId){
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "tsm");
			serializer.attribute(null, "version", "01");
			serializer.startTag(null,"clientInfo");
			serializer.attribute(null,"clientType","1");
			serializer.attribute(null,"clientVer","01");
			serializer.startTag(null,"terminalInfo");
			serializer.startTag(null,"seid");
			serializer.text(seId);
			serializer.endTag(null,"seid");
			serializer.startTag(null,"imei");
			serializer.text(imei);
			serializer.endTag(null,"imei");
			serializer.startTag(null,"phone");
			serializer.text(phone);
			serializer.endTag(null,"phone");
			serializer.endTag(null,"terminalInfo");
			serializer.startTag(null,"request");
			serializer.startTag(null,"sessionID");
			serializer.text(sessionId);
			serializer.endTag(null,"sessionID");
			serializer.startTag(null,"taskID");
			serializer.text(taskId);
			serializer.endTag(null,"taskID");
			serializer.endTag(null,"request");
			serializer.startTag(null,"chksum");
			serializer.endTag(null,"chksum");
			serializer.endTag(null,"tsm");

		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}
		return writer.toString().trim();
	}

	/**
	 * 解析TSM平台业务响应数据APDU
	 * @param xml
	 * @param sessionId
	 * @param taskId
     * @return
     */
	public static TSMResponseData parseBussinessResponseXml(String xml,String sessionId,String taskId){
		TSMResponseData tsmResponseData = new TSMResponseData();
		List<APDUInfo> apduInfoList = tsmResponseData.apduInfoList;
		APDUInfo apduInfo = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xml));
			int eventType = parser.getEventType();
			while (eventType!=XmlPullParser.END_DOCUMENT){
				String nodeName = parser.getName();
				switch (eventType){
					case  XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if(nodeName.equals("sessionID")){
							String session = parser.nextText();
							if(!sessionId.equals(session)) {
								return null;
							}
							tsmResponseData.sessionId = session;
						}
						else if(nodeName.equals("taskID")){
							String task = parser.nextText();
							if(!taskId.equals(task)){
								return null;
							}
							tsmResponseData.taskId = task;
						}
						else if(nodeName.equals("APDUList")){
							apduInfoList = new ArrayList<>();
						}
						else if(nodeName.equals("APDUInfo")){
							apduInfo = new APDUInfo();
						}
						else if(nodeName.equals("index")){
							apduInfo.setIndex(parser.nextText());
						}
						else if(nodeName.equals("APDU")){
							apduInfo.setAPDU(parser.nextText());
						}
						else if(nodeName.equals("SW")){
							apduInfo.setSW(parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if(nodeName.equals("APDUInfo")){
							apduInfoList.add(apduInfo);
						}
						break;
					default:
						break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}

		return tsmResponseData;
	}

	/**
	 * App返回SE业务执行数据XML
	 *
	 * @param apdu
	 *            APDU对象
	 * @return
	 */
	public static String message_Response_handle(String seId,String imei,String phone,
												String sessionId,String taskId,APDUInfo apdu,String result) {
		serializer = Xml.newSerializer();
		writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "tsm");
			serializer.attribute(null, "version", "01");
			serializer.startTag(null,"clientInfo");
			serializer.attribute(null,"clientType","1");
			serializer.attribute(null,"clientVer","01");
			serializer.startTag(null,"terminalInfo");
			serializer.startTag(null,"seid");
			serializer.text(seId);
			serializer.endTag(null,"seid");
			serializer.startTag(null,"imei");
			serializer.text(imei);
			serializer.endTag(null,"imei");
			serializer.startTag(null,"phone");
			serializer.text(phone);
			serializer.endTag(null,"phone");
			serializer.endTag(null,"terminalInfo");
			serializer.startTag(null,"request");
			serializer.startTag(null,"sessionID");
			serializer.text(sessionId);
			serializer.endTag(null,"sessionID");
			serializer.startTag(null,"taskID");
			serializer.text(taskId);
			serializer.endTag(null,"taskID");
			serializer.startTag(null, "result");
			serializer.text(result);
			serializer.endTag(null, "Result");
			serializer.startTag(null,"RAPDUList");
			serializer.startTag(null,"APDUInfo");
			serializer.startTag(null,"index");
			serializer.text(apdu.getIndex());
			serializer.endTag(null,"index");
			serializer.startTag(null,"APDU");
			serializer.text(apdu.getAPDU());
			serializer.endTag(null,"APDU");
			serializer.startTag(null,"SW");
			serializer.text(apdu.getSW());
			serializer.endTag(null,"SW");
			serializer.endTag(null,"APDUInfo");
			serializer.endTag(null,"RAPDUList");
			serializer.endTag(null,"request");
			serializer.startTag(null,"chksum");
			serializer.endTag(null,"chksum");
			serializer.endTag(null,"tsm");

		} catch (IOException e) {
//			e.printStackTrace();
			return null;
		}
		return writer.toString().trim();
	}

	/**
	 * 从XML中获取sessionId,taskId等参数组成TSMRequestData数据结构
	 * @param requestXml
	 * @return
     */
	public static TSMRequestData getTSMRequestDataFromXml(String requestXml){
		TSMRequestData tsmRequestData = new TSMRequestData();
		try {
			XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = xmlPullParserFactory.newPullParser();
			parser.setInput(new StringReader(requestXml));
			int eventType = parser.getEventType();
			while (eventType!=XmlPullParser.END_DOCUMENT){
				String nodeName = parser.getName();
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (nodeName.equals("seid")){
							tsmRequestData.setSeId(parser.nextText());
						}
						else if(nodeName.equals("imei")){
							tsmRequestData.setImei(parser.nextText());
						}
						else if (nodeName.equals("phone"))
						{
							tsmRequestData.setPhone(parser.nextText());
						}
						else if (nodeName.equals("sessionID")){
							tsmRequestData.setSessionId(parser.nextText());
						}
						else if (nodeName.equals("taskID")){
							tsmRequestData.setTaskId(parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					default:
						break;
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tsmRequestData;
	}
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

}
