package com.finance.app.domain.service;

import com.finance.app.domain.entity.ParsedTransaction;

import java.io.InputStream;
import java.util.List;

public interface StatementParser {

    List<ParsedTransaction> parse(InputStream inputStream);

    boolean supports(String fileName, String contentType);

}
