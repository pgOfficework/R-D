package com.sapiens.ssi.noSql.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sapiens.ssi.constants.SSIConstant;

public class CoreFactory {
	private static final String FileName = null;

	public static SSICoreInterface getFormat(String type) {
		
		if(type.equalsIgnoreCase(SSIConstant.json)) {
			return new JsonImpl();
		}
		else if(type.equalsIgnoreCase(SSIConstant.xlsx)) {
			return new XlsxImpl();
		}
		else
			return null;
	}
}
