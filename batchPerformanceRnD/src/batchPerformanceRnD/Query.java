package batchPerformanceRnD;

public class Query {
	public static String query="\r\n" + 
			"		SELECT\r\n" + 
			"			PP.ID,\r\n" + 
			"			PP.UPDATE_DATE\r\n" + 
			"		FROM\r\n" + 
			"			(\r\n" + 
			"			SELECT\r\n" + 
			"				*\r\n" + 
			"			FROM\r\n" + 
			"				SI_CORE_IDIT_ODS_SSI.P_POLICY ) PP\r\n" + 
			"		INNER JOIN SI_CORE_DWH_SSI_ABSA.CONTRACT_VERSION CNV ON\r\n" + 
			"			PP.ID = CNV.CONTRACT_VERSION_NUM\r\n" + 
			"		WHERE\r\n" + 
			"			PP.UPDATE_DATE > '1800-01-01'\r\n" + 
			"			AND PP.UPDATE_DATE <= getdate()\r\n" + 
			"	UNION\r\n" + 
			"		SELECT\r\n" + 
			"			PP.ID,\r\n" + 
			"			PP.UPDATE_DATE\r\n" + 
			"		FROM\r\n" + 
			"			SI_CORE_IDIT_ODS_SSI.P_POL_HEADER PPH\r\n" + 
			"		JOIN (\r\n" + 
			"			SELECT\r\n" + 
			"				*\r\n" + 
			"			FROM\r\n" + 
			"				SI_CORE_IDIT_ODS_SSI.P_POLICY ) PP ON\r\n" + 
			"			PPH.ID = PP.POLICY_HEADER_ID\r\n" + 
			"		INNER JOIN SI_CORE_DWH_SSI_ABSA.CONTRACT_VERSION CNV ON\r\n" + 
			"			PP.ID = CNV.CONTRACT_VERSION_NUM\r\n" + 
			"		WHERE\r\n" + 
			"			PPH.UPDATE_DATE > '1800-01-01'\r\n" + 
			"			AND PPH.UPDATE_DATE <= getdate()\r\n" + 
			"	UNION\r\n" + 
			"		SELECT\r\n" + 
			"			PP.ID,\r\n" + 
			"			PP.UPDATE_DATE\r\n" + 
			"		FROM\r\n" + 
			"			SI_CORE_IDIT_ODS_SSI.P_COVER PC\r\n" + 
			"		JOIN (\r\n" + 
			"			SELECT\r\n" + 
			"				*\r\n" + 
			"			FROM\r\n" + 
			"				SI_CORE_IDIT_ODS_SSI.P_POLICY ) PP ON\r\n" + 
			"			PC.ENDORSMENT_ID = PP.ID\r\n" + 
			"		INNER JOIN SI_CORE_DWH_SSI_ABSA.CONTRACT_VERSION CNV ON\r\n" + 
			"			PP.ID = CNV.CONTRACT_VERSION_NUM\r\n" + 
			"		WHERE\r\n" + 
			"			PC.UPDATE_DATE > '1800-01-01'\r\n" + 
			"			AND PC.UPDATE_DATE <= getdate()\r\n" + 
			"	UNION\r\n" + 
			"		SELECT\r\n" + 
			"			PP.ID,\r\n" + 
			"			PP.UPDATE_DATE\r\n" + 
			"		FROM\r\n" + 
			"			SI_CORE_IDIT_ODS_SSI.P_COVER_EXT PCE\r\n" + 
			"		JOIN SI_CORE_IDIT_ODS_SSI.P_COVER PC ON\r\n" + 
			"			PCE.COVER_ID = PC.ID\r\n" + 
			"		JOIN (\r\n" + 
			"			SELECT\r\n" + 
			"				*\r\n" + 
			"			FROM\r\n" + 
			"				SI_CORE_IDIT_ODS_SSI.P_POLICY ) PP ON\r\n" + 
			"			PC.ENDORSMENT_ID = PP.ID\r\n" + 
			"		INNER JOIN SI_CORE_DWH_SSI_ABSA.CONTRACT_VERSION CNV ON\r\n" + 
			"			PP.ID = CNV.CONTRACT_VERSION_NUM\r\n" + 
			"		WHERE\r\n" + 
			"			PCE.UPDATE_DATE > '1800-01-01' \r\n" + 
			"			AND PCE.UPDATE_DATE <= getdate()\r\n" + 
			"	UNION\r\n" + 
			"		SELECT\r\n" + 
			"			PP.ID,\r\n" + 
			"			PP.UPDATE_DATE\r\n" + 
			"		FROM\r\n" + 
			"			SI_CORE_IDIT_ODS_SSI.P_COVER_DISCOUNT_STG PCDS\r\n" + 
			"		JOIN (\r\n" + 
			"			SELECT\r\n" + 
			"				*\r\n" + 
			"			FROM\r\n" + 
			"				SI_CORE_IDIT_ODS_SSI.P_POLICY ) PP ON\r\n" + 
			"			PCDS.POLICY_ID = PP.ID\r\n" + 
			"		INNER JOIN SI_CORE_DWH_SSI_ABSA.CONTRACT_VERSION CNV ON\r\n" + 
			"			PP.ID = CNV.CONTRACT_VERSION_NUM\r\n" + 
			"		WHERE\r\n" + 
			"			PCDS.LAST_UPDATE_DATE > '1800-01-01'\r\n" + 
			"			AND PCDS.LAST_UPDATE_DATE <= getdate() ";
}
