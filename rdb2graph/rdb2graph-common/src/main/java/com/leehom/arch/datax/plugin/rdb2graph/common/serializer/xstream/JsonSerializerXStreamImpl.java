/**
 * %serializer%
 * %1.2%
 */
package com.leehom.arch.datax.plugin.rdb2graph.common.serializer.xstream;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.leehom.arch.datax.plugin.rdb2graph.common.serializer.Serializer;
import com.leehom.arch.datax.plugin.rdb2graph.common.serializer.SeriallizeException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @类名: JsonSerializerXStreamImpl
 * @说明: json序列化器XStream实现
 *     
 *
 * @author   leehom 
 * @Date	 2010-6-9 上午10:58:40
 *
 *   
 * @see 	 
 */
public class JsonSerializerXStreamImpl implements Serializer {
	
	/** 序列化库*/
	protected XStream xs;	
	/** 类型关联*/
	private Map<String, Class> typeAlias;
	/** 忽略字段*/
	private Map<String, Class> omitAlias;
	/** 
	 * 取消标记, 
	 * @see XStream.addImplicitCollection
	 */
	private Map<String, Class> imCols;
 	/** 转换器*/
	private List<SingleValueConverter> converters;
	/** 转换器类型, 针对需要new的时候传入mapper参数的*/
	private List<Class<Converter>> converterClazz;
	/** 编码*/
	private String encoding = "utf-8";	
	/** */
	private String header = "";

	public JsonSerializerXStreamImpl() {
		
		xs =  new XStream(new JettisonMappedXmlDriver());
		xs.setMode(XStream.NO_REFERENCES);
		// 打开标注
		xs.autodetectAnnotations(true);
		
	}

	public List<SingleValueConverter> getConverters() {
		return converters;
	}

	public void setConverters(List<SingleValueConverter> converters) {
		this.converters = converters;
		for(SingleValueConverter c : converters) {			
			xs.registerConverter(c);
		}
	}

	@Override
	public byte[] Marshal(Object obj) throws SeriallizeException {				
		String h = MessageFormat.format(header, new Object[]{encoding});
		try {
			byte[] objBs =  xs.toXML(obj).getBytes(encoding);
			byte[] r = new byte[h.getBytes().length+objBs.length];
			System.arraycopy(h.getBytes(), 0, r, 0, h.getBytes().length);
			System.arraycopy(objBs, 0, r, h.getBytes().length, objBs.length);
			return r;
		} catch (UnsupportedEncodingException e) {
			throw new SeriallizeException("Serializer Marshal Exception: "+e.getMessage());
		}
		
	}

	@Override
	public Object Unmarshal(byte[] xml) throws SeriallizeException {	
		return xs.fromXML(new ByteArrayInputStream(xml));
		
	}

	public Map<String, Class> getTypeAlias() {
		return typeAlias;
	}

	public void setTypeAlias(Map<String, Class> typeAlias) {
		this.typeAlias = typeAlias;
		//
		for(String alias :  typeAlias.keySet()) { 			
	        xs.alias(alias, typeAlias.get(alias));
		}
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Map<String, Class> getImCols() {
		return imCols;
	}

	public void setImCols(Map<String, Class> imCols) {
		this.imCols = imCols;
		for(String imCol :  imCols.keySet()) { 			
	        xs.addImplicitCollection(imCols.get(imCol), imCol);
		}
		
	}

	/**
	 * @return the converterClazz
	 */
	public List<Class<Converter>> getConverterClazz() {
		return converterClazz;
	}

	/**
	 * @param converterClazz the converterClazz to set
	 */
	public void setConverterClazz(List<Class<Converter>> converterClazzs) {		
		for(Class<Converter> cc : converterClazzs) {
			Converter c;
			try {
				c = cc.getDeclaredConstructor(Mapper.class).newInstance(xs.getMapper());
				xs.registerConverter(c);
			} catch (Exception e) {
				
			} 
			
		}
				
	}

	/**
	 * @return the omitAlias
	 */
	public Map<String, Class> getOmitAlias() {
		return omitAlias;
	}

	/**
	 * @param omitAlias the omitAlias to set
	 */
	public void setOmitAlias(Map<String, Class> omitAlias) {
		this.omitAlias = omitAlias;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
