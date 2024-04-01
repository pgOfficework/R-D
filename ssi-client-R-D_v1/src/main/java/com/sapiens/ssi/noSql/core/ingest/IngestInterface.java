package com.sapiens.ssi.noSql.core.ingest;

import java.util.Map;

public interface IngestInterface {
	void load(String tranformResult, Map<String, Object> payload);
}
