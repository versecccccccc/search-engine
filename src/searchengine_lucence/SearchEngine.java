package searchengine_lucence;

import java.io.File;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// This part is to create the index of these files
class CreateIndex {
	public void createindex() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("/Users/zhengyuecheng/eclipse-workspace/SearchEngine/data/index"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.deleteAll();
            File dFile = new File("/Users/zhengyuecheng/eclipse-workspace/SearchEngine/data/data");
            File[] files = dFile.listFiles();
            for (File file : files) {
                Document document = new Document();
                document.add(new Field("content", new FileReader(file), TextField.TYPE_NOT_STORED));
                document.add(new Field("filename", file.getName(), TextField.TYPE_STORED));
                document.add(new Field("filepath", file.getAbsolutePath(), TextField.TYPE_STORED));
                indexWriter.addDocument(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null) {
                    indexWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

//This part is to search the files which cantain the "keywords" from the input
class SearchPart {  
    public void searchpart(String keyWords) {  
        DirectoryReader directoryReader = null;  
        try {   
            Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("/Users/zhengyuecheng/eclipse-workspace/SearchEngine/data/index")); 
            directoryReader = DirectoryReader.open(directory);  
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);  
            Analyzer analyzer = new StandardAnalyzer();   
            QueryParser queryParser = new QueryParser("content", analyzer);   
            Query query = queryParser.parse(keyWords);  
            TopDocs topDocs = indexSearcher.search(query,100000);  
            System.out.println("results："+topDocs.totalHits); 
            System.out.println("Find files: ");
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
            int flag = 0;
            for (ScoreDoc scoreDoc : scoreDocs) {   
                Document document = indexSearcher.doc(scoreDoc.doc);  
                float score = scoreDoc.score;
                if (score > 7) {
                    System.out.println("file score: " + score);
                    System.out.println("file name: " + document.get("filename") + " " + "file path: " + document.get("filepath"));  
                    flag ++;
                }
            }  
            System.out.println("count: " + flag);
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (directoryReader != null) {  
                    directoryReader.close();  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  

//main
public class SearchEngine {
	public static void main(String[] args){		 
		CreateIndex newIndex = new CreateIndex();
        newIndex.createindex();
        SearchPart newSearch = new SearchPart();
        System.out.println("input the keywords: ");
        Scanner input = new Scanner(System.in);
        String keywords = input.nextLine();
        newSearch.searchpart(keywords);
	 } 
}
