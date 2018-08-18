package startlucene;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

public class SearchFiles {
	public static void main(String[] args) throws Exception {
		String indexPath = "D:/lucene_test/index"; // 建立索引文件的目录
		String field = "contents";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();

		BufferedReader in = null;
		in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		QueryParser parser = new QueryParser(field, analyzer);
		System.out.println("Enter query:");
		// 从Console读取要查询的语句
		String line = in.readLine();
		if (line == null || line.length() == -1) {
			return;
		}
		line = line.trim();
		if (line.length() == 0) {
			return;
		}

		Query query = parser.parse(line);
		System.out.println("Searching for:" + query.toString(field));
		doPagingSearch(searcher, query);
		in.close();
		reader.close();
	}

	private static void doPagingSearch(IndexSearcher searcher, Query query) throws IOException {
		// TopDocs保存搜索结果
		TopDocs results = searcher.search(query, 10);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = Math.toIntExact(results.totalHits);
		System.out.println(numTotalHits + " total matching documents");
		for (ScoreDoc hit : hits) {
			Document document = searcher.doc(hit.doc);
			System.out.println("文档:" + document.get("path"));
			System.out.println("相关度:" + hit.score);
			System.out.println("================================");
		}

	}
}
