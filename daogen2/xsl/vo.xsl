<?xml version="1.0" encoding="euc-kr" ?>
<!--
* @(#) VO.xsl
*
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="EUC-KR" />
<xsl:template match="table">
/*
 * @(#)<xsl:value-of select="@class"/>VO.java
 * <xsl:value-of select="@class"/> Table VO INFO
 */
package com;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.db.ValueObject;

/**
	Table : <xsl:value-of select="@name"/>
	Primary Key : <xsl:for-each select="primarykey/key">
		<xsl:value-of select="."/></xsl:for-each>
	Table Fields
		<xsl:for-each select="columns/column">
		<xsl:value-of select="@name"/>:<xsl:value-of select="@dbType"/>:<xsl:value-of select="@desc"/>\n
		</xsl:for-each>
*/
public class <xsl:value-of select="@class"/>VO extends ValueObject {
	public static final String TABLE = "<xsl:value-of select='@name'/>";
	public static final String TABLE_DESC = "<xsl:value-of select='description'/>";
	public static final String PRIMARYKEY_LIST[] = { <xsl:for-each select="columns/column[@primarykey]"><xsl:if test='position()!=1'>,</xsl:if>"<xsl:value-of select='@name'/>"</xsl:for-each> };
	public static final String FIELD_LIST[] = { <xsl:for-each select='columns/column'><xsl:if test='position()!=1'>,</xsl:if>"<xsl:value-of select='@name'/>"</xsl:for-each> };
	
	// column name
	private static Map&lt;String, Integer&gt; columnMap = new HashMap&lt;String, Integer&gt;();
	// column type
	private static Map&lt;String, String&gt; typeMap = new HashMap&lt;String, String&gt;();
	// no update list
	private static List&lt;String&gt; noUpdateList = new ArrayList&lt;String&gt;();

	public Map&lt;String, Integer&gt; getColumnMap() {
		return columnMap;
	}

	public Map&lt;String, String&gt; getTypeMap() {
		return typeMap;
	}

	static {<xsl:for-each select="columns/column">
		columnMap.put("<xsl:value-of select='@name'/>", Integer.valueOf(<xsl:value-of select='position()'/>));</xsl:for-each>

		<xsl:for-each select="columns/column">
		typeMap.put("<xsl:value-of select='@name'/>", "<xsl:value-of select='@type'/>");</xsl:for-each>

		<xsl:for-each select="columns/column[@update]">
		noUpdateList.add("<xsl:value-of select='@name'/>");</xsl:for-each>
	}

	<xsl:for-each select="columns/column">
	private <xsl:value-of select="@type"/> _<xsl:value-of select="@name"/>;</xsl:for-each>

	public <xsl:value-of select="@class"/>VO(){}
	<xsl:for-each select="columns/column">
	public <xsl:value-of select="@type"/> get<xsl:value-of select="@name"/>() { <xsl:if test='@default'>
		if (_<xsl:value-of select="@name"/> == null)
			return <xsl:value-of select='@default'/>;
		else
		</xsl:if>
		return _<xsl:value-of select="@name"/>;
	}

	public void set<xsl:value-of select="@name"/>(<xsl:value-of select="@type"/> new<xsl:value-of select="@name"/>) {
		_<xsl:value-of select="@name"/> = new<xsl:value-of select="@name"/>;
	}
	</xsl:for-each>

	public Object getByName(String key) {
		Integer idx = (Integer)columnMap.get(key.toUpperCase());
		if (idx != null) {
			switch(idx.intValue()) { <xsl:for-each select="columns/column">
			case <xsl:value-of select='position()'/>:
				return get<xsl:value-of select='@name'/>();</xsl:for-each>
			}
		}
		return null;
	}

	public void setByName(String key, Object value) {
		Integer idx = (Integer)columnMap.get(key.toUpperCase());
		if (idx != null) {
			switch(idx.intValue()) { <xsl:for-each select="columns/column">
			case <xsl:value-of select='position()'/>:
				set<xsl:value-of select='@name'/>((<xsl:value-of select='@type'/>)value);
				return;</xsl:for-each>
			}
		}
	}

	public String getType(String key){
		return (String)typeMap.get(key.toUpperCase());
	}

	public String[] getPrimaryKeysName() {
		return PRIMARYKEY_LIST;
	}

	public String[] getFieldsName() {
		return FIELD_LIST;
	}

	public Object[] getPrimaryKeysValue() {
		return new Object[] { <xsl:for-each select="columns/column[@primarykey]"><xsl:if test='position()>1'>,</xsl:if>get<xsl:value-of select="@name"/>()</xsl:for-each> };
	}

	public Object[] getFieldsValue() {
		return new Object[] { <xsl:for-each select="columns/column"><xsl:if test='position()>1'>,</xsl:if>get<xsl:value-of select="@name"/>()</xsl:for-each> };
	}

	public String toString(){
		StringBuilder buf = new StringBuilder();
		<xsl:for-each select="columns/column">buf.append("<xsl:value-of select="@name"/>:"+ get<xsl:value-of select="@name"/>()+"\n");
		</xsl:for-each>
		return buf.toString();
	}

	public Object[] getUpdateValue() {
		return new Object[] { <xsl:for-each select="columns/column[not(@update) and not(@primarykey)]"><xsl:if test='position()>1'>,</xsl:if>get<xsl:value-of select="@name"/>()</xsl:for-each><xsl:for-each select="columns/column[@primarykey]">, get<xsl:value-of select='@name'/>()</xsl:for-each> };
	}

	public Object[] getInsertValue() {
		return new Object[] { <xsl:for-each select="columns/column[not(@insert)]"><xsl:if test='position()>1'>,</xsl:if>get<xsl:value-of select='@name'/>()</xsl:for-each> };
	}

	public Object[] getUpdateOnlyValue(String[] fields) {
		if (fields == null) {
			getLogger().error("fields Error!");
			return null;
		}
		List&lt;Object&gt; list = new ArrayList&lt;Object&gt;();
		for (String field : fields) {
			if (field == null) {
				getLogger().error("field is null!");
				return null;
			}
			if (!noUpdateList.contains(field.toUpperCase())) {
				list.add(getByName(field));
			}
		}
		for (Object key : getPrimaryKeysValue()) {
			list.add(key);
		}
		return list.toArray();
	}

	public Object[] getUserUpdateOnlyValue(String[] fields, String[] keys) {
		if ( fields == null ) {
			getLogger().error("fields Error!");
			return null;
		}
		if ( keys == null ) {
			getLogger().error("keys Error!");
			return null;
		}
		List&lt;Object&gt; list = new ArrayList&lt;Object&gt;();
		for (String field : fields) {
			if (field == null) {
				getLogger().error("field is null!");
				return null;
			}
			if (!noUpdateList.contains(field.toUpperCase())) {
				list.add(getByName(field));
			}
		}
		for (String key : keys) {
			list.add(getByName(key));
		}
		return list.toArray();
	}

	public Object[] getUserDeleteValue(String[] keys) {
		if ( keys == null ) {
			getLogger().error("keys Error!");
			return null;
		}
		List&lt;Object&gt; list = new ArrayList&lt;Object&gt;();
		for (String key : keys) {
			list.add(getByName(key));
		}
		return list.toArray();
	}
}
</xsl:template>
</xsl:stylesheet>
