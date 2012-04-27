<?xml version="1.0" encoding="euc-kr" ?>
<!--
* DAO.xsl
*
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
<xsl:output method="text"  encoding="EUC-KR" />
<xsl:template match="table">
/*
 * @(#)<xsl:value-of select="@class"/>DAO.java
 * <xsl:value-of select="@class"/> Table DAO INFO
 */
package COM.DAO;

import java.util.HashMap;
import java.util.Map;

import framework.db.ConnectionManager;
import framework.db.RecordSet;
import framework.db.AbstractDao;
import framework.db.ValueObject;
import COM.VO.<xsl:value-of select="@class"/>VO;

/**
	Table : <xsl:value-of select="@name"/>
	Primary Key : <xsl:for-each select="primarykey/key">
		<xsl:value-of select="."/>, </xsl:for-each>
	Table Fields
		<xsl:for-each select="columns/column">
		<xsl:value-of select="@name"/>:<xsl:value-of select="@dbType"/>:<xsl:value-of select="@desc"/>\n
		</xsl:for-each>
*/
public class <xsl:value-of select="@class"/>DAO extends AbstractDao {
	// Update Only Map
	private static Map&lt;String, String&gt; updateOnlyMap = new HashMap&lt;String, String&gt;();

	static {<xsl:for-each select="columns/column[not(@update)]">
		updateOnlyMap.put("<xsl:value-of select='@name'/>", "?");</xsl:for-each>

		<xsl:for-each select="columns/column[not(@update ='none') and @update]">
		updateOnlyMap.put("<xsl:value-of select='@name'/>", "<xsl:value-of select='@update'/>");</xsl:for-each>
	}

	public <xsl:value-of select="@class"/>DAO(ConnectionManager ctx) {
		super( ctx );
	}

	public String getInsertSql(){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO <xsl:value-of select='@name'/> (");
		query.append("	<xsl:for-each select="columns/column[not(@insert ='none')]"><xsl:if test='position()!=1'>,</xsl:if> <xsl:value-of select='@name'/> </xsl:for-each>");
		query.append(") ");
		query.append("VALUES (");
		query.append("	<xsl:for-each select="columns/column[not(@insert ='none')]"><xsl:if test='position()!=1'>,</xsl:if><xsl:if test='@insert'><xsl:value-of select='@insert'/></xsl:if><xsl:if test='not(@insert)'>?</xsl:if></xsl:for-each>");
		query.append(")");
		return query.toString();
	}

	public String getUpdateSql(){
		<xsl:variable name="cnt" select="count(columns/column[not(@update ='none') and @update])"/>
		StringBuilder query = new StringBuilder();
		query.append("UPDATE <xsl:value-of select='@name'/> SET ");
		<xsl:for-each select="columns/column[not(@update ='none') and @update]">query.append("	<xsl:if test="position() >1">,</xsl:if><xsl:value-of select="@name"/> = <xsl:value-of select='@update'/> ");
		</xsl:for-each>
		<xsl:for-each select="columns/column[not(@update) and not(@primarykey)]">query.append("	<xsl:if test="$cnt > 0 or position() > 1">,</xsl:if><xsl:value-of select="@name"/> = ?");
		</xsl:for-each>query.append("WHERE 1 = 1 ");
		<xsl:for-each select="columns/column[@primarykey]">query.append("	AND <xsl:value-of select='@name'/> = ?");
		</xsl:for-each>
		return query.toString();
	}

	public String getDeleteSql(){
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM <xsl:value-of select='@name'/> WHERE  1 = 1 ");
		<xsl:for-each select="columns/column[@primarykey]">query.append("	AND <xsl:value-of select='@name'/> = ?");
		</xsl:for-each>
		return query.toString();
	}

	public RecordSet select(ValueObject obj) throws Exception {
		<xsl:value-of select="@class"/>VO vo = (<xsl:value-of select="@class"/>VO) obj;
		StringBuilder query = new StringBuilder();
		query.append("SELECT <xsl:for-each select='columns/column'><xsl:if test='position()!=1'>,</xsl:if> <xsl:value-of select='@name'/></xsl:for-each> ");
		query.append("FROM <xsl:value-of select='@name'/> ");
		query.append("WHERE 1 = 1 ");
		<xsl:for-each select="columns/column[@primarykey]">query.append("	AND <xsl:value-of select='@name'/> = ?");
		</xsl:for-each>
		return executeQuery(query.toString(), vo.getPrimaryKeysValue());
	}

	public RecordSet select(String where) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("SELECT <xsl:for-each select='columns/column'>  <xsl:if test='position()!=1'>,</xsl:if> <xsl:value-of select='@name'/></xsl:for-each> ");
		query.append("FROM <xsl:value-of select='@name'/> ");
		query.append(where);
		return executeQuery(query.toString());
	}

	public String getUpdateOnlySql(String[] fields){
		if (fields == null) {
			getLogger().error("fields Error!");
			return null;
		}
		StringBuilder query = new StringBuilder();
		query.append("UPDATE <xsl:value-of select='@name'/> SET ");
		for (String field : fields) {
			if (field == null) {
				getLogger().error("getUpdateOnlySql field is null!");
				return null;
			}
			if (!updateOnlyMap.containsKey(field.toUpperCase())) {
				continue;
			}
			query.append(field + " = " + updateOnlyMap.get(field.toUpperCase()));
			query.append(" ,");
		}
		query.delete(query.length()-1, query.length());
		query.append("WHERE  1 = 1 ");
		<xsl:for-each select="columns/column[@primarykey]">query.append("	AND <xsl:value-of select='@name'/> = ?");
		</xsl:for-each>
		return query.toString();
	}

	public String getUserUpdateOnlySql(String[] fields, String[] keys){
		if (fields == null) {
			getLogger().error("fields Error!");
			return null;
		}
		if (keys == null) {
			getLogger().error("keys Error!");
			return null;
		}
		StringBuilder query = new StringBuilder();
		query.append("UPDATE <xsl:value-of select="@name"/> SET ");
		for (String field : fields) {
			if(field == null) {
				getLogger().error("field is null!");
				return null;
			}
			if (!updateOnlyMap.containsKey(field.toUpperCase())) {
				continue;
			}
			query.append(field + " = " + updateOnlyMap.get(field.toUpperCase()));
			query.append(" ,");
		}
		query.delete(query.length()-1, query.length());
		query.append("WHERE 1 = 1 ");
		for (String key : keys) {
			query.append("	AND " + key + " = ?");
		}
		return query.toString();
	}

	public String getUserDeleteSql(String[] keys) {
		if (keys == null) {
			getLogger().error("keys Error!");
			return null;
		}
		StringBuilder query = new StringBuilder();
		query.append("DELETE FROM  <xsl:value-of select='@name'/> ");
		query.append("WHERE 1 = 1 ");
		for (String key : keys) {
			query.append("	AND " + key + " = ?");
		}
		return query.toString();
	}
}
</xsl:template>
</xsl:stylesheet>
