package com.cours_m2.gastroguidemichelin.search.service;

import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
@Data
public class LuceneEvaluationService {
    public static final String EVALUATION_INDEX_DIR = "./data/lucene/evaluation_index";

    private final IndexWriter evaluationIndexWriter;
    private IndexSearcher evaluationIndexSearcher;
    private final QueryParser evaluationQueryParser;
    private final Directory index;

    public LuceneEvaluationService() {
        try {
            Path path = Paths.get(EVALUATION_INDEX_DIR);
            this.index = FSDirectory.open(path);
            StandardAnalyzer analyzer = new StandardAnalyzer();

            this.evaluationQueryParser = new QueryParser("comment", analyzer);

            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            this.evaluationIndexWriter = new IndexWriter(index, indexWriterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void indexEvaluation(EvaluationEntity evaluation) {
        try {
            Document document = new Document();

            document.add(new StringField("id", evaluation.getId().toString(), Field.Store.YES));
            document.add(new TextField("comment", evaluation.getComment(), Field.Store.YES));

            evaluationIndexWriter.updateDocument(new Term("id", evaluation.getId().toString()), document);
            evaluationIndexWriter.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEvaluationIndex(Long id) {
        try {
            evaluationIndexWriter.deleteDocuments(new Term("id", id.toString()));
            evaluationIndexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Long> searchEvaluationsComments(List<String> keywords) {
        try {
            String toSearch = String.join(" ", keywords);
            this.evaluationIndexSearcher = new IndexSearcher(DirectoryReader.open(index));
            Query query = this.evaluationQueryParser.parse(QueryParser.escape(toSearch));
            TopDocs topDocs = this.evaluationIndexSearcher.search(query, 10);

            return Stream.of(topDocs.scoreDocs)
                    .map(scoreDoc -> {
                        try {
                            Document document = evaluationIndexSearcher.storedFields().document(scoreDoc.doc);
                            return Long.valueOf(document.get("id"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
