package com.example.demo.controller;

import com.example.demo.util.DatabaseTableAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyze")
public class TableAnalyzerController {

    @Autowired
    private DatabaseTableAnalyzer tableAnalyzer;

    @GetMapping("/table/{tableName}")
    public String analyzeTable(@PathVariable String tableName) {
        tableAnalyzer.analyzeTable(tableName);
        return "表结构分析完成，请查看控制台输出";
    }
}